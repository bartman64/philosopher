package Dininghall;

import MeditationHall.Philosopher;

import java.util.*;

public class TableMaster extends Thread implements Observer {

    private final Map<Philosopher, Integer> phil2EatCount;

    private boolean threadState;

    public TableMaster() {
        phil2EatCount = new HashMap<>();
        threadState = true;
    }

    public void run() {
        while (threadState) {

        }
    }

    public void initMap(List<Philosopher> philosophers) {
        for (Philosopher phil : philosophers) {
            phil2EatCount.put(phil, 0);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        int avgConsumption = 0;
        for (Map.Entry<Philosopher, Integer> entries : phil2EatCount.entrySet()) {
            if (!entries.getKey().equals(o)) {
                avgConsumption += entries.getValue();
            }
        }
        if (phil2EatCount.size() > 1) {
            avgConsumption = avgConsumption / (phil2EatCount.size() - 1);
        }
        for (Map.Entry<Philosopher, Integer> entries : phil2EatCount.entrySet()) {
            if (entries.getKey().equals(o)) {
                entries.setValue(entries.getValue() + 1);
                int currConsumption = entries.getValue();
                int consumptionDiff = currConsumption - avgConsumption;
                System.out.println("AverageConsumption: " + avgConsumption);
                System.out.println("ConsumptionDiff: " + consumptionDiff);
                System.out.println("Philosopher[" + entries.getKey().getId() + "] has eaten " + currConsumption + " times in a row!");
                if (consumptionDiff >= 5) {
                    System.out.println("[*] Philosopher[" + entries.getKey().getId() + "] is forced to sleep! [*]");
                    entries.getKey().forceSleep();
                }
            }
        }

    }

    public void setThreadState(boolean threadState) {
        this.threadState = threadState;
    }
}
