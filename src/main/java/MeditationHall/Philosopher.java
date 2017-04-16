package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.Chair;
import Dininghall.Fork;

public class Philosopher extends Thread {

    private static final int MAX_EAT_COUNTER = 3;

    private final int id;

    private int eatCounter;

    final private Dininghall dininghall;

    public Philosopher(final Dininghall dininghall, final int id) {
        this.eatCounter = 0;
        this.dininghall = dininghall;
        this.id = id;
    }

    public void run() {
        while (true) {
            meditiern(2000);
            eat(4000);
            if (eatCounter == MAX_EAT_COUNTER) {
                sleep(10000);
                eatCounter = 0;
            }
        }
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

}
