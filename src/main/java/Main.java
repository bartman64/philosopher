import Dininghall.Dininghall;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;
import Dininghall.TableMaster;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        long millis = System.currentTimeMillis() / 1000;
        final int numberOfPhilosophers = 10;
        final int numberOfPlaces = 2;

        final Dininghall dininghall = new Dininghall(numberOfPlaces);
        dininghall.initHall();
        final MeditationHall meditationHall = new MeditationHall(numberOfPhilosophers, dininghall);
        final TableMaster tableMaster = new TableMaster();

        meditationHall.initPhilosophers(0, tableMaster);
        tableMaster.initMap(meditationHall.getPhilosophers());
        tableMaster.start();

        final List<Thread> threadList = new ArrayList<Thread>();

        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            final Thread thread = new Thread(philosopher);
            threadList.add(thread);
            thread.start();
        }

        while (System.currentTimeMillis() / 1000 - millis != 5) {
        }

        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            philosopher.setThreadState(false);
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            System.out.println("[" + philosopher.getId() + "] ate " + philosopher.getTotalEatCounter() + " times.");
        }
    }
}
