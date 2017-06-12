package Dininghall;


import Client.Client;

import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;


import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Dininghall {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Dininghall.class);

    /**
     * List of chairs in th dining hall.
     */
    private final List<Chair> chairs;

    /**
     * List of forks in the dining hall.
     */
    private final List<Fork> forks;

    /**
     * int number of places.
     */
    private int numberOfPlaces;

    /**
     * ClientControl needed to contact the server for seats.
     */
    private final Client client;

    /**
     * Int value indicating the start of the chair and seats index.
     */
    private int startValue;

    private Registry registry;

    /**
     * Constructor for the dining hall.
     * Initializes with the number of places matching amount of chairs and forks.
     *
     * @param numberOfPlaces number of places the dining hall can have
     */
    public Dininghall(final int numberOfPlaces, final Client client) {
        this.chairs = new ArrayList<Chair>();
        this.forks = new ArrayList<Fork>();
        this.numberOfPlaces = numberOfPlaces;
        this.client = client;
    }

    public List<Fork> getForks() {
        return forks;
    }

    public List<Chair> getChairs() {
        return chairs;
    }


    /**
     * This method initializes the hall with chairs and forks.
     * When the number of places equals one, an extra fork will be created.
     */
    public void initHall(final Registry register, final int startValue) {
        this.startValue = startValue;
        this.registry = register;
        for (int i = startValue; i < startValue + numberOfPlaces; i++) {
            chairs.add(new Chair(i));
            forks.add(new Fork(i));
            try {
                ChairRemote chair = (ChairRemote) UnicastRemoteObject.exportObject(chairs.get(i - startValue), 0);
                ForkRemote fork = (ForkRemote) UnicastRemoteObject.exportObject(forks.get(i - startValue), 0);
                client.proxyBind("Chair" + i, chair);
                client.proxyBind("Fork" + i, fork);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (client.getTotalSeats() == 1 && numberOfPlaces == 1) {
            forks.add(new Fork(1));
            try {
                ForkRemote fork = (ForkRemote) UnicastRemoteObject.exportObject(forks.get(1), 0);
                client.proxyBind("Fork" + 1, fork);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This method gets the left fork for the matching chair,
     * if the fork is taken it returns null.
     *
     * @param chair  Chair from where the method tries to take the left fork
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return the left fork if not already taken, null otherwise
     */
    public synchronized ForkRemote getLeftFork(final ChairRemote chair, final int philId) {
        ForkRemote leftFork = null;
        try {
            //If the current chair is a remote chair from another table part search for the matching remote fork.
            if (isRemoteChair(chair)) {
                try {
                    leftFork = (ForkRemote) registry.lookup("Fork" + chair.getId());
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.info("Chair Id: " + String.valueOf(chair.getId() + " Startvalue: " + startValue));
                leftFork = forks.get(chair.getId() - startValue);
            }
            if (leftFork != null && !leftFork.aquireFork()) {
                return null;
            } else if (leftFork != null) {
                LOGGER.info("\tPhilosopher [" + philId + "] took left fork: " + leftFork.getId());
                return leftFork;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method gets the right fork for the matching chair,
     * if the fork is taken it return null.
     *
     * @param chair  Chair from where the method tries to take the right fork
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return the right fork if not already taken, null otherwise
     */
    public ForkRemote getRightFork(final ChairRemote chair, final int philId) {
        final ForkRemote rightFork;
        //If the current chair is the last chair in the list
        //The right fork is the first fork in the list
        try {
            //If the current chair is a remote chair from another table part search for the matching remote fork.
            //And Check if the current chair is the last in the list
            if (isRemoteChair(chair) || (chair.getId() - startValue) == (chairs.size() - 1)) {
                rightFork = getRemoteFork(chair);
            } else {
                rightFork = forks.get(chair.getId() + 1 - startValue);
            }
            if (!rightFork.aquireFork()) {
                return null;
            } else {
                LOGGER.info("\t\tPhilosopher [" + philId + "] took right fork: " + rightFork.getId());
                return rightFork;
            }
        } catch (RemoteException | NotBoundException e) {
            LOGGER.error("Status:  Phil[" + philId + "]");
            e.printStackTrace();
        }
        return null;
    }

    private ForkRemote getRemoteFork(ChairRemote chair) throws RemoteException, NotBoundException {
        ForkRemote rightFork;
        String forkName;
        LOGGER.info("Chair: " + chair.getId() + "check if last chair");
        if (client.getTotalSeats() - 1 == chair.getId() && client.getTotalSeats() > 1) {
            forkName = "Fork0";
        } else {
            forkName = "Fork" + (chair.getId() + 1);
        }
        LOGGER.info("Forkname: " + forkName);
        rightFork = (ForkRemote) registry.lookup(forkName);
        return rightFork;
    }

    /**
     * This method checks if the given chair is a remote chair
     * or the last chair of a client.
     *
     * @param chair Chair which will be checked
     * @return true if the chair is from another client or is the last for a client
     * @throws RemoteException Connection lost
     */
    private boolean isRemoteChair(ChairRemote chair) throws RemoteException {
        return chair.getId() < startValue || chair.getId() >= startValue + numberOfPlaces;
    }

    /**
     * This method iterates random through the list of chairs,
     * tries to find a chair which is not taken
     * with the left fork which not taken.
     *
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return chair if not taken and has left fork, null otherwise
     */
    public Chair getChair(final int philId) {
        int size = chairs.size() - 1;
        int start = (int) (size * Math.random());
        if(chairs.size() == 1){
            size = 1;
            start = 0;
        }
        int j = start;
        for (; j < size; j++) {
            Chair chair = chairs.get(start);
            if (chair.aquireChair()) {
                LOGGER.info("Philosopher [" + philId + "]took chair:" + chair.getId());
                return chair;
            }
        }
        for (int i = 0; i < start; i++) {
            Chair chair = chairs.get(i);
            if (chair.aquireChair()) {
                LOGGER.info("Philosopher [" + philId + "]took chair:" + chair.getId());
                return chair;
            }
        }
        return null;
    }

    /**
     * This method iterates through the chairs and tries to acquire one with an empty queue.
     *
     * @param philosopher Philosopher which wants to acquire a empty chair
     * @return Chair in which the philosopher got added to the queue, null otherwise
     */

    public Chair getQueueChair(final Philosopher philosopher) {
        for (Chair chair : chairs) {
            if (chair.aquireQueuedChair(philosopher)) {
                return chair;
            }
        }
        return null;
    }

    public ChairRemote clientSearch(final int philosopherId) {
        return client.searchForEmptySeat(philosopherId);
    }


    /**
     * Returns the fork as a lock in order for the philosopher to wait on it.
     *
     * @param chair Chair needed to get the matching left fork
     * @return fork on which the philosopher waits
     */
    public ForkRemote aquireWaitFork(final ChairRemote chair) {
        ForkRemote waitFork = null;
        int chairId = 0;
        try {
            chairId = chair.getId();
            if (isRemoteChair(chair)) {
                //waitFork = (ForkRemote) registry.lookup("Fork" + chairId);
            } else {
                waitFork = forks.get(chairId - startValue);
            }
            return waitFork;
        } catch (final Exception e) {
            throw new RuntimeException();
        }

    }

    /**
     * This method clears the list of chairs and forks.
     */
    public void resetLists() {
        forks.clear();
        chairs.clear();
        LOGGER.info("Lists cleared");
    }

    public void setNumberOfPlaces(int numberOfPlaces) {
        this.numberOfPlaces = numberOfPlaces;
    }
}
