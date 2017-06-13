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

    /**
     * Logger for logging.
     */
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
     * List of clients.
     */
    private List<ClientControl> clients;

    /**
     * Tablemaster over seeing the philosophers
     * and sends those who ate to much into a longer sleeping phase
     */
    private TableMaster tableMaster;


    public Client() {
        this.numberOfPhilosophers = 0;
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
    public void init(final int numberOfPhilosophers, final int numberOfSeats, final Registry registry, final int prevSeats, final int prevPhils, final int totalSeats, final List<ClientControl> clients) throws RemoteException {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.totalSeats = totalSeats;
        this.clients = clients;
        clients.remove(this);

        //Set up the dining hall
        dininghall = new Dininghall(numberOfSeats, this);
        dininghall.initHall(registry, prevSeats);

        meditationHall = new MeditationHall(numberOfPhilosophers, dininghall);
        tableMaster = new TableMaster(this);

        //Initializes the philosophers
        meditationHall.initPhilosophers(0, tableMaster, prevPhils);

        //Initializes the table master
        tableMaster.initMap(meditationHall.getPhilosophers());
        LogInitData(numberOfPhilosophers, numberOfSeats);
    }

    /**
     * This method logs the philosopher, chairs and forks which got initialized.
     *
     * @param numberOfPhilosophers number of philosophers
     * @param numberOfSeats        number of seats
     */
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

    void setServer(ServerControl server) {
        this.server = server;
    }

    @Override
    public int avgCalc() {
        return tableMaster.calcAvgConsumption();
    }

    @Override
    public void initNewTable(final int numberOfSeats, final Registry registry, final int startValue, final int totalSeats) throws RemoteException {
        this.totalSeats = totalSeats;

        final int startIndicez = numberOfSeats * startValue;
        dininghall.setNumberOfPlaces(numberOfSeats);
        dininghall.initHall(registry, startIndicez);
        LogInitData(numberOfPhilosophers, numberOfSeats);
    }

    @Override
    public void clearDininghall() {
        final Thread myThread = new Thread(new Client.PhilosopherPause());
        myThread.start();
    }

    private class PhilosopherPause implements Runnable {

        @Override
        public void run() {
            for (final Philosopher philosopher : meditationHall.getPhilosophers()) {
                philosopher.setThreadState(false);
            }
            for (Philosopher philosopher : meditationHall.getPhilosophers()) {
                LOGGER.info("[" + philosopher.getId() + "] ate " + philosopher.getTotalEatCounter() + " times.");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dininghall.resetLists();
            LOGGER.info("Cleared client dininghall");
        }
    }

    public void stopClient() {
        final Thread thread = new Thread(new Client.PhilosophStopper());
        thread.start();
    }


    public class PhilosophStopper implements Runnable {

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
            tableMaster.addPhilToTableMaster(philosopher);
            final Thread thread = new Thread(philosopher);
            threads.add(thread);
            LOGGER.info("Philosopher[" + philosopher.getId() + "] started");
            thread.start();
        }

    }

    @Override
    public void stopPhils(final int amount) {
        int amountToDelete = amount;
        if (meditationHall.getPhilosophers().size() < amount) {
            amountToDelete = meditationHall.getPhilosophers().size();
        }
        for (int i = 0; i < amountToDelete; i++) {
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

    public void proxyBind(final String name, Remote object) {
        try {
            server.proxyBind(name, object);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * Search for an empty seat on other clients.
     * Start with random client
     * @param philosopherId id of the philosopher that tries to aquire a chair
     * @return Returns a chair if available else null
     * @throws RemoteException
     */
    public ChairRemote searchFreeChair(final int philosopherId) throws RemoteException {
        int startClient = (int) (clients.size() * Math.random());

        if(clients.size() == 1){
            startClient = 0;
        }

        for(int i =  startClient; i < clients.size();  i++){
            final ChairRemote chair = clients.get(i).searchEmptyChair(philosopherId);
            if(chair != null) {
                return chair;
            }
        }

        for(int j = 0; j < startClient; j++){
            final ChairRemote chair = clients.get(j).searchEmptyChair(philosopherId);
            if(chair != null) {
                return chair;
            }
        }

        return null;
    }

    @Override
    public int calcTotalAvg() {
        int totalAvg = 0;
        for (ClientControl client : clients) {
            try {
                totalAvg += client.avgCalc();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        totalAvg /= clients.size();
        return totalAvg;
    }
}
