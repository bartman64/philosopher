package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.Chair;
import Dininghall.Fork;
import Dininghall.TableMaster;

import java.util.Observable;

public class Philosopher extends Observable implements Runnable {

    private static final int MAX_EAT_COUNTER = 3;

    private final int id;

    private int eatCounter;

    private final TableMaster tableMaster;

    final private Dininghall dininghall;

    private int medtime = 2000;


    public Philosopher(final Dininghall dininghall, final int id, final TableMaster tableMaster) {
        this.eatCounter = 0;
        this.dininghall = dininghall;
        this.id = id;
        this.tableMaster = tableMaster;
        this.addObserver(tableMaster);
    }

    public Philosopher(final Dininghall dininghall, final int id, final int medTime, final TableMaster tableMaster){
        this.eatCounter = 0;
        this.dininghall = dininghall;
        this.id = id;
        this.medtime = medTime;
        this.tableMaster = tableMaster;
        this.addObserver(tableMaster);
    }

    public void run() {
        while (true) {
            meditiern(medtime);
            eat(4000);
            if (eatCounter == MAX_EAT_COUNTER) {
                System.out.printf("\t\t\t\t\tPhilospher [%d]  is sleeping\n", id);
                sleep(10000);
                eatCounter = 0;
            }
        }
    }

    private void notifyOb() {
        setChanged();
        notifyObservers();
    }

    private void eat(final int eatTime) {
        try {
            final Chair chair = dininghall.getChair(id);
            if (chair != null) {
                final Fork leftFork = dininghall.getLeftFork(chair, id);
                if (leftFork != null) {

                    final Fork rightFork = dininghall.getRightFork(chair, id);
                    if (rightFork != null) {
                        System.out.printf("\t\t\tPhilospher [%d]  is eating\n", id);
                        Thread.sleep(eatTime);
                        eatCounter++;
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
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void meditiern(final int medTime) {
        try {
            Thread.sleep(medTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sleep(final int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getEatCounter() {
        return eatCounter;
    }

    public int getId() {
        return id;
    }

    public void forceSleep(){

        this.sleep(10000);
    }
}
