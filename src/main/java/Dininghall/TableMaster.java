package Dininghall;

import MeditationHall.Philosopher;

import java.util.*;

public class TableMaster extends Thread implements Observer {

    private final List<Philosopher> philosophers;

    private final Map<Philosopher, Integer> phil2EatCount;

    public TableMaster(List<Philosopher> philosophers) {
        this.philosophers = philosophers;
        phil2EatCount = new HashMap<Philosopher, Integer>();

    }

    public void run() {
        initMap();
        while (true) {

        }
    }

    private void initMap() {
        for (Philosopher phil : philosophers) {
            phil2EatCount.put(phil, 0);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        for (Map.Entry<Philosopher, Integer> entries : phil2EatCount.entrySet()) {
            System.out.println("test");
            if(entries.getKey().equals(o)){
                System.out.println("test");
                entries.setValue(entries.getValue() + 1);
                System.out.println(entries);
                if(entries.getValue()%5 == 0){
                    System.out.println("[*]" + entries.getKey().getId() + "is forced to sleep! [*]");
                    entries.getKey().forceSleep();
                }
            }
        }

    }
}
