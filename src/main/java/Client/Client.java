package Client;


import Dininghall.Dininghall;
import Dininghall.ChairRemote;
import Dininghall.TableMaster;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;
import Server.ServerControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client implements ClientControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    /**
     * Int id to log the clients.
     */
    private int id;

    /**
     * Int value for the amount of philosophers in the meditation hall.
     */
    private int numberOfPhilosophers;

    /**
     * Int value for the amount of seats in the dining hall.
     */
    private int numberOfSeats;

    /**
     * List of threads containing the runnable philosophers.
     */
    private final List<Thread> threads;

    /**
     * Dininghall containing the seats and the forks.
     */
    private Dininghall dininghall;

    /**
     * Meditationhall containing the philosophers.
     */
    private MeditationHall meditationHall;

    /**
     * ServerControl server for connecting to the other clients.
     */
    private ServerControl server;


    /**
     * Tablemaster over seeing the philosophers
     * and sends those who ate to much into a longer sleeping phase
     */
    private TableMaster tableMaster;

    public Client() {
        this.numberOfPhilosophers = 0;
        this.numberOfSeats = 0;
        this.threads = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void init(final int numberOfPhilosophers, final int numberOfSeats, final Registry registry, final int startValue) throws RemoteException {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.numberOfSeats = numberOfSeats;
        final int startIndicez = numberOfSeats * startValue;
        dininghall = new Dininghall(numberOfSeats, this);
        dininghall.initHall(registry, startIndicez);
        meditationHall = new MeditationHall(numberOfPhilosophers, dininghall);
        tableMaster = new TableMaster();
        meditationHall.initPhilosophers(0, tableMaster, startIndicez);
        tableMaster.initMap(meditationHall.getPhilosophers());
        LOGGER.info("Client[" + getId() + "] finished initialization with Seats[" +
                numberOfSeats + "] and  Philosopher[" + numberOfPhilosophers + "]\n");
    }

    @Override
    public void startClient() {
        tableMaster.start();
        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            final Thread thread = new Thread(philosopher);
            threads.add(thread);
            thread.start();
        }
    }

    @Override
    public ChairRemote searchEmptyChair(final int philosophersId) throws RemoteException {
        return dininghall.getChair(philosophersId);
    }

    public void setServer(ServerControl server) {
        this.server = server;
    }

    /**
     * This method calls the server to search for free seats on the other clients.
     *
     * @return empty chair for the philosopher,
     * if every chair is occupied null gets returned
     */
    public ChairRemote searchForEmptySeat(final int philospherId) {
        try {
            return server.searchFreeChair(philospherId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
