package Client;


import Dininghall.Dininghall;
import Dininghall.ChairRemote;
import Dininghall.TableMaster;
import Dininghall.Chair;
import Dininghall.Fork;
import Dininghall.ForkRemote;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;
import Server.ServerControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
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

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

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

    private int totalSeats;

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

    public int getTotalSeats() {
        return totalSeats;
    }

    @Override
    public void init(final int numberOfPhilosophers, final int numberOfSeats, final Registry registry, final int startValue, final int totalSeats) throws RemoteException {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.numberOfSeats = numberOfSeats;
        this.totalSeats = totalSeats;

        final int startIndicez = numberOfSeats * startValue;
        final int startPhilsInidez = numberOfPhilosophers * startValue;
        dininghall = new Dininghall(numberOfSeats, this);
        dininghall.initHall(registry, startIndicez);
        meditationHall = new MeditationHall(numberOfPhilosophers, dininghall);
        tableMaster = new TableMaster(this);
        LOGGER.info(String.valueOf(startIndicez));
        meditationHall.initPhilosophers(0, tableMaster, startPhilsInidez);
        tableMaster.initMap(meditationHall.getPhilosophers());
        LogInitData(numberOfPhilosophers, numberOfSeats);
    }

    private void LogInitData(int numberOfPhilosophers, int numberOfSeats) {
        LOGGER.info("Client[" + getId() + "] finished initialization with Seats[" +
                numberOfSeats + "] and  Philosopher[" + numberOfPhilosophers + "]\n");
        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            LOGGER.info("Phils: [" + philosopher.getId() + "]");
        }
        for (Chair chair : dininghall.getChairs()) {
            LOGGER.info("Chair: [" + chair.getId() + "]");
        }

        for (Fork fork : dininghall.getForks()) {
            LOGGER.info("Fork: [" + fork.getId() + "]");
        }
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
    public void restartClients() {
        for (final Philosopher philosopher : meditationHall.getPhilosophers()) {
            philosopher.setThreadState(true);
        }
        synchronized (dininghall) {
            dininghall.notifyAll();
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

    @Override
    public int avgCalc() {
        return tableMaster.calcAvgConsumption();
    }

    public int getTotalAvg() {
        try {
            return server.calcTotalAvg();
        } catch (RemoteException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void initNewTable(final int numberOfSeats, final Registry registry, final int startValue, final int totalSeats) throws RemoteException {
        this.numberOfSeats = numberOfSeats;
        this.totalSeats = totalSeats;

        final int startIndicez = numberOfSeats * startValue;
        dininghall.setNumberOfPlaces(numberOfSeats);
        dininghall.initHall(registry, startIndicez);
        LogInitData(numberOfPhilosophers, numberOfSeats);
    }

    @Override
    public void clearDininghall() {
        final Thread myThread = new Thread(new MyThread());
        myThread.start();
    }

    public class MyThread implements Runnable {

        @Override
        public void run() {
            for (final Philosopher philosopher : meditationHall.getPhilosophers()) {
                philosopher.setThreadState(false);
            }
            for (Philosopher philosopher : meditationHall.getPhilosophers()) {
                LOGGER.info("[" + philosopher.getId() + "] ate " + philosopher.getTotalEatCounter() + " times.");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dininghall.resetLists();
            LOGGER.info("Cleared client dininghall");
        }
    }

    public void stopClient() {
        final Thread thread = new Thread(new Stop());
        thread.start();
    }

    public class Stop implements Runnable {

        @Override
        public void run() {
            for (final Philosopher philosopher : meditationHall.getPhilosophers()) {
                philosopher.stopPhil();
            }
            for (final Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (Philosopher philosopher : meditationHall.getPhilosophers()) {
                LOGGER.info("[" + philosopher.getId() + "] ate " + philosopher.getTotalEatCounter() + " times.");
            }
        }
    }

    @Override
    public void addPhils(final int amountOfNewPhils, final int totalPhils) {
        for (int i = totalPhils; i < totalPhils + amountOfNewPhils; i++) {
            final Philosopher philosopher = new Philosopher(dininghall, i, tableMaster);
            meditationHall.addPhil(philosopher);
            final Thread thread = new Thread(philosopher);
            threads.add(thread);
            LOGGER.info("Philosopher[" + philosopher.getId() + "] started");
            thread.start();
        }

    }

    public void stopPhils(final int amount) {
        for (int i = 0; i < amount; i++) {
            final Philosopher philosopher = meditationHall.getPhilosophers().get(0);
            philosopher.stopPhil();
            meditationHall.removePhil(philosopher);
            LOGGER.info("Removed Phil with id [" + philosopher.getId() + "]");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void proxyBind(final String name, Remote object){
        try {
            server.proxyBind(name,object );
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
