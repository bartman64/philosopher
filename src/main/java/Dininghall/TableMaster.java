package Dininghall;

import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TableMaster extends Thread implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableMaster.class);

    private final Map<Philosopher, Integer> phil2EatCount;

    public TableMaster() {
        phil2EatCount = new HashMap<>();
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
                LOGGER.info("AverageConsumption: " + avgConsumption);
                LOGGER.info("ConsumptionDiff: " + consumptionDiff);
                LOGGER.info("Philosopher[" + entries.getKey().getId() + "] has eaten " + currConsumption + " times in a row!");
                if (consumptionDiff >= 5) {
                    LOGGER.info("[*] Philosopher[" + entries.getKey().getId() + "] is forced to sleep! [*]");
                    entries.getKey().forceSleep();
                }
            }
        }

    }
}
