package Client;


import Dininghall.Dininghall;
import Dininghall.TableMaster;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
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
    public void init(final int numberOfPhilosophers, final int numberOfSeats) throws RemoteException {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.numberOfSeats = numberOfSeats;

        dininghall = new Dininghall(numberOfSeats);
        dininghall.initHall();
        meditationHall = new MeditationHall(numberOfPhilosophers, dininghall);
        tableMaster = new TableMaster();
        meditationHall.initPhilosophers(0, tableMaster);
        tableMaster.initMap(meditationHall.getPhilosophers());
        LOGGER.info("Client[" + getId() + "] finished initialization with Seats[" +
                numberOfSeats + "] and  Philosopher[" + numberOfPhilosophers + "]\n");
    }

    /**
     * This method start the runnable philosophers and the tablemaster thread.
     */
    private void startClient() {
        tableMaster.start();
        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            final Thread thread = new Thread(philosopher);
            threads.add(thread);
            thread.start();
        }
    }
}
