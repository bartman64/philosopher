package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.Chair;
import Dininghall.Fork;
import Dininghall.TableMaster;

import java.util.Observable;

public class Philosopher extends Observable implements Runnable {

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
                System.out.printf("\t\t\t\t\tPhilospher [%d]  is sleeping\n", id);
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
    public void eat() {
        try {
            final Chair chair = dininghall.getChair(id);
            if (chair != null) {
                final Fork leftFork = dininghall.getLeftFork(chair, id);
                if (leftFork != null) {

                    final Fork rightFork = dininghall.getRightFork(chair, id);
                    if (rightFork != null) {
                        System.out.printf("\t\t\tPhilospher [%d]  is eating\n", id);
                        Thread.sleep(EAT_TIME_MS);
                        eatCounter++;
                        totalEatCounter++;
                        leftFork.setTaken(false);
                        rightFork.setTaken(false);
                        System.out.printf("\t\tPhilospher [%d] released left fork: %d\n", id, leftFork.getId());
                        System.out.printf("\tPhilospher [%d] released right fork: %d\n", id, rightFork.getId());
                        notifyOb();
                    } else {
                        leftFork.setTaken(false);
                        System.out.printf("Philospher [%d] released left fork: %d\n", id, leftFork.getId());
                    }
                }
                chair.setTaken(false);
                System.out.printf("Philospher [%d] leaves table\n", id);
            } else {
                dininghall.addPhilToQueue(this);
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
