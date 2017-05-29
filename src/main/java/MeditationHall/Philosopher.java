package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.ChairRemote;
import Dininghall.Fork;
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
    private static final int SLEEP_TIME_MS = 10;

    /**
     * int value indicating the amount of time it takes to eat in milliseconds.
     */
    private static final int EAT_TIME_MS = 1;

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

    /**
     * int value indicating the amount if time it takes to meditate in milliseconds.
     */
    private int medtime_ms = 5;

    /**
     * Boolean to check wether the current Philospher is waiting.
     */
    private boolean waiting = false;

    private boolean threadState;

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
        while (threadState) {
            meditate();
            eat();
            if (eatCounter == MAX_EAT_COUNTER) {
                LOGGER.info("\t\t\t\t\tPhilospher [" + id + "]  is sleeping\n");
                sleep();
                eatCounter = 0;
            }
        }
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
            boolean eaten = false;
            ChairRemote chair = dininghall.getChair(id);
            if (chair == null) {
                if((chair = dininghall.getQueueChair(this)) != null){
                    waiting = true;
                    synchronized (chair){
                        LOGGER.info("Philosopher [" + id + "] waiting for notification\n");
                        while(waiting) {
                            chair.wait();
                        }
                    }
                    chair = dininghall.getChair(id);
                }
                if(chair == null){
                    chair = dininghall.clientSearch(id);
                    LOGGER.info("Remote Chair aquired");
                }
            }
            if (chair != null) {
                final Fork leftFork = dininghall.getLeftFork(chair, id);
                if (leftFork != null) {

                    final Fork rightFork = dininghall.getRightFork(chair, id);
                    if (rightFork != null) {
                        eaten = true;
                        LOGGER.info("\t\t\tPhilospher [" + id + "]  is eating\n");
                        Thread.sleep(EAT_TIME_MS);
                        eatCounter++;
                        totalEatCounter++;
                        leftFork.setTaken(false);
                        rightFork.setTaken(false);
                        LOGGER.info("\t\tPhilospher [" + id + "] released left fork: " + leftFork.getId());
                        LOGGER.info("\tPhilospher [" + id + "] released right fork: " + rightFork.getId());
                    } else {
                        leftFork.setTaken(false);
                        LOGGER.info("Philospher [" + id + "] released left fork: " + leftFork.getId());
                    }
                }
                try {
                    chair.setTaken(false);
                    if(eaten){
                        notifyOb();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                LOGGER.info("Philospher [" + id + "] leaves table\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public void setWaitingStatus(boolean status){
        this.waiting = status;
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
}
