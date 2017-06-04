package MeditationHall;

import Dininghall.Dininghall;
import Dininghall.TableMaster;

import java.util.ArrayList;
import java.util.List;

public class MeditationHall {

    /**
     * int value indicating the number of philosophers in the meditation hall.
     */
    private final int numberOfPhilosophers;

    /**
     * List holding the philosophers.
     */
    private List<Philosopher> philosophers;

    /**
     * Dininghall needed to initialize the philosophers.
     */
    private final Dininghall dininghall;

    /**
     * Constructor for the meditation hall.
     * Holds the philosophers in a list.
     *
     * @param numberOfPhilosophers the amount of philosophers in the dining hall
     * @param dininghall           dining hall where the philosophers will eat
     */
    public MeditationHall(final int numberOfPhilosophers, final Dininghall dininghall) {
        this.philosophers = new ArrayList<Philosopher>();
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.dininghall = dininghall;
    }

    /**
     * This method initializes the list of philosophers.
     * Creating a certain amount of normal and hungry philosophers depending of the input
     *
     * @param hungryPhils indicates how many hungry philosophers have to be created
     * @param tableMaster table master needed to be able to over see the amount of times the philosophers ate.
     */
    public void initPhilosophers(final int hungryPhils, final TableMaster tableMaster, final int startValue) {
        for (int i = startValue; i < startValue + numberOfPhilosophers - hungryPhils; i++) {
            philosophers.add(new Philosopher(dininghall, i, tableMaster));
        }
        for (int i = numberOfPhilosophers - hungryPhils; i < numberOfPhilosophers; i++) {
            philosophers.add(new Philosopher(dininghall, i, 2, tableMaster));

        }
    }

    /**
     * Gets the list of philosophers.
     */
    public List<Philosopher> getPhilosophers() {
        return philosophers;
    }

    public void addPhil(final Philosopher philosopher) {
        philosophers.add(philosopher);
    }

    public void removePhil(final Philosopher philosopher) {
        philosophers.remove(philosopher);
    }
}
