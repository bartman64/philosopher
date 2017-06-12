package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.ChairRemote;
import Dininghall.ForkRemote;
import Dininghall.TableMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Observable;

public class Philosopher extends Observable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Philosopher.class);

    /**
     * int value indicating the max amount of times the philosopher can eat before he goes to sleep.
     */
    private static final int MAX_EAT_COUNTER = 3;

    /**
     * int value indicating the amount of time it takes to sleep in milliseconds.
     */
    private static final int SLEEP_TIME_MS = 1;

    /**
     * int value indicating the amount of time it takes to eat in milliseconds.
     */
    private static final int EAT_TIME_MS = 1;

    /**
     * int value indicating the amount if time it takes to meditate in milliseconds.
     */
    private int medtime_ms = 5;

    /**
     * int id of an philosophers to be able to identify him later.
     */
    private final int id;

    /**
     * int value indicating the amount of time a philosopher ate.
     * Which will be resetted after the philosopher hit the max eat counter.
     */
    private int eatCounter;

    /**
     * int value indicating the total amount of eat sessions.
     */
    private int totalEatCounter;

    /**
     * Dininghall where the philosophers go to eat.
     */
    final private Dininghall dininghall;

    private boolean threadState;

    private boolean isWaiting = false;

    private boolean running = true;


    /**
     * Constructor for the philosopher that has a dining hall to eat,
     * an id to be identified and a table master who over sees the eating..
     *
     * @param dininghall  Dininghall where the philosopher go to eat
     * @param id          Id indicating its identity
     * @param tableMaster TableMaster overseeing the process of eating
     */
    public Philosopher(final Dininghall dininghall, final int id, final TableMaster tableMaster) {
        this.eatCounter = 0;
        this.totalEatCounter = 0;
        this.dininghall = dininghall;
        this.id = id;
        this.addObserver(tableMaster);
        threadState = true;
    }


    /**
     * Constructor for the hungry philosophers. It gets in addition the time it takes him to meditate,
     * which will be lower then the time for the others
     *
     * @param dininghall  Dininghall where the philosopher go to eat
     * @param id          Id indicating its identity
     * @param medTime     time it takes him to meditate in milliseconds
     * @param tableMaster TableMaster overseeing the process of eating
     */
    public Philosopher(final Dininghall dininghall, final int id, final int medTime, final TableMaster tableMaster) {
        this.eatCounter = 0;
        this.dininghall = dininghall;
        this.id = id;
        this.medtime_ms = medTime;
        this.addObserver(tableMaster);
        threadState = true;

    }

    /**
     * The process of meditating, eating and sleeping if the max eat counter is surpassed.
     */
    public void run() {
        while (running) {
            try {
                synchronized (dininghall) {
                    while (!threadState) {
                        dininghall.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            meditate();
            eat();
            if (eatCounter == MAX_EAT_COUNTER) {
                LOGGER.info("\t\t\t\t\tPhilospher [" + id + "]  is sleeping");
                sleep();
                eatCounter = 0;
            }
        }
        LOGGER.info("Got DELETED!!!!!");
    }

    /**
     * Notifies the table master that the philospher ate.
     */
    private void notifyOb() {
        setChanged();
        notifyObservers();
    }

    /**
     * This method simulates the process of eating.
     * Finding an emptry chair, trying to get both forks and then be able to eat.
     * It prints out status messages after each step.
     */
    private void eat() {
        try {
            ChairRemote chair = dininghall.getChair(id);
            if (chair == null) {
                chair = tryGettingIntoQueue();
                if (chair == null) {
                    chair = tryGettingRemoteChair();
                }
            }
            if (chair != null) {
                ForkRemote leftFork = dininghall.getLeftFork(chair, id);
                if (leftFork == null) {
                    leftFork = waitForLeftFork(chair);
                }
                if (leftFork != null) {
                    ForkRemote rightFork = dininghall.getRightFork(chair, id);
                    if (rightFork == null) {
                        Thread.sleep(0, 500);
                        rightFork = dininghall.getRightFork(chair, id);
                    }
                    if (rightFork != null) {
                        eatAndReleaseProcess(chair, leftFork, rightFork);
                    } else {
                        leftFork.setTaken(false);
                        LOGGER.info("\t\tPhilospher [" + id + "] released left fork: " + leftFork.getId());
                        chair.setTaken(false);
                        LOGGER.info("Philospher [" + id + "] leaves table\n", id);
                    }
                } else {
                    chair.setTaken(false);
                    LOGGER.info("Philospher [" + id + "] leaves table\n", id);
                }
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method simulates the process of eating.
     * The philosopher thread sleeps for the amount of time it takes to eat.
     * When the eat process is done the forks and chair gets release.
     *
     * @param chair     Chair where the philosopher is sitting on and has to releas when finish
     * @param leftFork  Fork needs to be release when finish
     * @param rightFork Fork needs to be release when finish
     * @throws InterruptedException When an interruption occurs
     * @throws RemoteException      connection lost
     */
    private void eatAndReleaseProcess(ChairRemote chair, ForkRemote leftFork, ForkRemote rightFork) throws InterruptedException, RemoteException {
        LOGGER.info("\t\t\tPhilospher [" + id + "]  is eating");
        Thread.sleep(EAT_TIME_MS);

        eatCounter++;
        totalEatCounter++;

        leftFork.setTaken(false);
        LOGGER.info("\t\tPhilospher [" + id + "] released left fork: " + leftFork.getId());

        rightFork.setTaken(false);
        LOGGER.info("\tPhilospher [" + id + "] released right fork: " + rightFork.getId());

        chair.setTaken(false);
        LOGGER.info("Philospher [" + id + "] leaves table");

        notifyOb();
    }

    /**
     * This method waits for the left fork to be released.
     *
     * @param chair Chair needed to acquire the left fork
     * @return the left fork of the given chair
     * @throws RemoteException      Connection lost
     * @throws InterruptedException when an interruption occurs
     */
    private ForkRemote waitForLeftFork(ChairRemote chair) throws RemoteException, InterruptedException {
        ForkRemote leftFork;
        leftFork = dininghall.aquireWaitFork(chair, "left");
        synchronized (leftFork) {
            LOGGER.info("\t\tPhilosopher [" + id + "] is waiting for LeftFork[" + leftFork.getId() + "]");
            while (leftFork.isTaken()) {
                leftFork.wait();
            }
            leftFork = dininghall.getLeftFork(chair, id);
        }
        return leftFork;
    }

    /**
     * This method tries to get a remote chair.
     * It asks the server to find an empty chair
     *
     * @return Chair if an empty seat was found, null otherwise
     * @throws RemoteException Connection lost
     */
    private ChairRemote tryGettingRemoteChair() throws RemoteException {
        ChairRemote chair;
        chair = dininghall.clientSearch(id);
        if (chair != null) {
            LOGGER.info("Remote Chair[" + chair.getId() + "] aquired from " + id);
        }
        return chair;
    }

    /**
     * This method tries to find an empty queue for a chair.
     * The philosopher waits until someone leaves the table and then tries to get a chair.
     *
     * @return Chair on where the philosopher seats, null otherwise
     * @throws InterruptedException if an interruption occurs
     */
    private ChairRemote tryGettingIntoQueue() throws InterruptedException {
        ChairRemote chair;
        if ((chair = dininghall.getQueueChair(this)) != null) {
            synchronized (chair) {
                LOGGER.info("Philosopher [" + id + "] waiting for notification");
                while (isWaiting) {
                    chair.wait();
                }
            }
            chair = dininghall.getChair(id);
        }
        return chair;
    }

    /**
     * This method sets the thread to sleep to simulate the meditation.
     */
    private void meditate() {
        try {
            Thread.sleep(medtime_ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method sets the thread to sleep to simulate sleeping.
     */
    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the id from the philosopher.
     */
    public int getId() {
        return id;
    }

    //TODO IS this force sleep necessary?
    public void forceSleep() {
        this.sleep();
    }

    public int getTotalEatCounter() {
        return totalEatCounter;
    }

    public synchronized void setThreadState(boolean threadState) {
        this.threadState = threadState;
    }

    public void stopPhil() {
        this.running = false;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }
}
