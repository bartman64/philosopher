package Dininghall;


import Client.Client;
import Client.ClientControl;

import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;


import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Dininghall {

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
    private final int numberOfPlaces;

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
                register.bind("Chair" + i, chair);
                register.bind("Fork" + i, fork);
            } catch (RemoteException | AlreadyBoundException e) {
                e.printStackTrace();
            }
        }
        if (numberOfPlaces == 1) {
            forks.add(new Fork(numberOfPlaces));
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
            leftFork = forks.get(chair.getId());
            if (!leftFork.aquireFork()) {
                return null;
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
            if (leftFork != null && leftFork.isTaken()) {
                return null;
            } else if (leftFork != null) {
                leftFork.setTaken(true);
                LOGGER.info("\tPhilospher ["+ philId + "] took left fork: " + leftFork.getId() +" \n");
                LOGGER.info("\tPhilospher [" + philId + "] took left fork: " + leftFork.getId());
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
    public Fork getRightFork(final ChairRemote chair, final int philId) {
        final Fork rightFork;
        //If the current chair is the last chair in the list
        //The right fork is the first fork in the list
        try {
            //If the current chair is a remote chair from another table part search for the matching remote fork.
            //And Check if the current chair is the last in the list
            if (isRemoteChair(chair) || chair.equals(chairs.get(chairs.size() - 1))) {
                String forkName;
                if (client.getNumberOfSeats() - 1 == chair.getId()) {
                    forkName = "Fork0";
                } else {
                    forkName = "Fork" + (chair.getId() + 1);
                }
                LOGGER.info("Forkname: " + forkName);
                rightFork = (ForkRemote) registry.lookup(forkName);
            } else {
                rightFork = forks.get(chair.getId() + 1 - startValue);
            }
            if (!rightFork.aquireFork()) {
                return null;
            } else {
                LOGGER.info("\t\tPhilospher [" + philId + "] took right fork: " + rightFork.getId());
                rightFork.setTaken(true);
                return rightFork;
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isRemoteChair(ChairRemote chair) throws RemoteException {
        return chair.getId() < startValue || chair.getId() >= startValue + numberOfPlaces;
    }

    /**
     * This method iterates through the list of chairs,
     * tries to find a chair which is not taken
     * with the left fork which not taken.
     *
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return chair if not taken and has left fork, null otherwise
     */
    public Chair getChair(final int philId) {
        for (Chair chair : chairs) {
            if (chair.aquireChair()) {
            if (!chair.isTaken() && !forks.get(chair.getId() - startValue).isTaken()) {
                chair.setTaken(true);
                LOGGER.info("Philospher [" + philId + "] took chair: " + chair.getId());
                return chair;
            }
        }
        return null;
    }

    /**
     *
     * @param philosopher
     * @return
     */
    public Chair getQueueChair(final Philosopher philosopher){
        for(Chair chair : chairs){
            if(chair.aquireQueuedChair(philosopher)){
                return chair;
            }
        }
        return null;
    }

    public ChairRemote clientSearch(final int philosopherId) {
        return client.searchForEmptySeat(philosopherId);
    }

}
