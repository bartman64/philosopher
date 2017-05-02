package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.TableMaster;

import java.util.ArrayList;
import java.util.List;

public class MeditationHall {

    private final int amount;

    private List<Philosopher> philosophers;

    private final Dininghall dininghall;

    public MeditationHall(final int amount, final Dininghall dininghall) {
        this.philosophers = new ArrayList<Philosopher>();
        this.amount = amount;
        this.dininghall = dininghall;
    }

    public void initPhilosophers(final int hungryPhils, final TableMaster tableMaster){
        for(int i = 0; i < amount - hungryPhils; i++){
            philosophers.add(new Philosopher(dininghall, i, tableMaster));
        }
        for(int i = amount - hungryPhils; i < amount; i++){
            philosophers.add(new Philosopher(dininghall, i, 500, tableMaster));

        }
    }

    public List<Philosopher> getPhilosophers() {
        return philosophers;
    }
}
