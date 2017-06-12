package Dininghall;

import Client.ClientControl;
import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;

public class TableMaster extends Thread implements Observer {

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TableMaster.class);

    /**
     * Map of Philosophers and their consumption
     */
    private final Map<Philosopher, Integer> phil2EatCount;

    /**
     * Client on which the Tablemaster runs
     */
    private final ClientControl client;

    /**
     * Ctor of the TableMaster
     * @param client Client of the Tablemaster
     */
    public TableMaster(final ClientControl client) {
        phil2EatCount = new HashMap<>();
        this.client = client;
    }


    /**
     * Initializes the Philosopher - Consumption Map of the TableMaster
     * @param philosophers List containing all philosophers of a client
     */
    public void initMap(List<Philosopher> philosophers) {
        for (Philosopher phil : philosophers) {
            phil2EatCount.put(phil, 0);
        }
    }

    /**
     * Updates Consumption - Philosopher Map of the TableMaster
     * and checks if the Philosopher that called the TableMaster
     * should be forced to sleep.
     * @param o Philosopher Object that called the TableMaster
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        int avgConsumption = 0;
        try {
            avgConsumption = client.getTotalAvg();
        } catch (RemoteException e) {
            e.printStackTrace();
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
                    LOGGER.info("[*] Philosopher [" + entries.getKey().getId() + "] is forced to sleep! [*]");
                    entries.getKey().forceSleep();
                }
            }
        }
    }

    /**
     * Calculates the average philosopher consumption.
     * @return Average Philosopher consumption
     */
    public int calcAvgConsumption() {
        int avgConsumption = 0;
        for (Map.Entry<Philosopher, Integer> entries : phil2EatCount.entrySet()) {
            avgConsumption += entries.getValue();
        }
        if (phil2EatCount.size() > 1) {
            avgConsumption = avgConsumption / phil2EatCount.size();
        }
        return avgConsumption;
    }
}
