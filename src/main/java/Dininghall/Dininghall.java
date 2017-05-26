package Dininghall;


import Client.Client;
import Client.ClientControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
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
        for (int i = startValue; i < startValue + numberOfPlaces; i++) {
            chairs.add(new Chair(i));
            forks.add(new Fork(i));
            try {
                ChairRemote chair = (ChairRemote) UnicastRemoteObject.exportObject(chairs.get(i), 0);
                register.bind("Chair" + i, chair);
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
    public synchronized Fork getLeftFork(final ChairRemote chair, final int philId) {
        final Fork leftFork;
        try {
            leftFork = forks.get(chair.getId());
            if (leftFork.isTaken()) {
                return null;
            } else {
                leftFork.setTaken(true);
                LOGGER.info("\tPhilospher [%d] took left fork: %d\n", philId, leftFork.getId());
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
    public synchronized Fork getRightFork(final ChairRemote chair, final int philId) {
        final Fork rightFork;
        //If the current chair is the last chair in the list
        //The right fork is the first fork in the list
        try {
            if (chair.getId() == numberOfPlaces - 1) {
                rightFork = forks.get(0);
            } else {
                rightFork = forks.get(chair.getId() + 1);
            }
            if (rightFork.isTaken()) {
                return null;
            } else {
                LOGGER.info("\t\tPhilospher [" + philId + "] took right fork: " + rightFork.getId());
                rightFork.setTaken(true);
                return rightFork;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
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
    public synchronized Chair getChair(final int philId) {
        for (Chair chair : chairs) {
            if (!chair.isTaken() && !forks.get(chair.getId()).isTaken()) {
                chair.setTaken(true);
                LOGGER.info("Philospher [" + philId + "] took chair: " + chair.getId());
                return chair;
            }
        }
        return null;
    }

    public ChairRemote clientSearch(final int philosopherId) {
        return client.searchForEmptySeat(philosopherId);
    }

}
