package Dininghall;


import java.util.ArrayList;
import java.util.List;

public class Dininghall {

    /**
     * List of chairs in th dining hall.
     */
    private final List<Chair> chairs;

    /**
     * List of forks in the dining hall.
     */
    private final List<Fork> forks;

    /**
     * int number of places.
     */
    private final int numberOfPlaces;

    /**
     * Constructor for the dining hall.
     * Initializes with the number of places matching amount of chairs and forks.
     *
     * @param numberOfPlaces number of places the dining hall can have
     */
    public Dininghall(final int numberOfPlaces) {
        this.chairs = new ArrayList<Chair>();
        this.forks = new ArrayList<Fork>();
        this.numberOfPlaces = numberOfPlaces;
    }

    /**
     * This method initializes the hall with chairs and forks.
     * When the number of places equals one, an extra fork will be created.
     */
    public void initHall() {
        for (int i = 0; i < numberOfPlaces; i++) {
            chairs.add(new Chair(i));
            forks.add(new Fork(i));
        }
        if (numberOfPlaces == 1) {
            forks.add(new Fork(numberOfPlaces));
        }
    }

    /**
     * This method gets the left fork for the matching chair,
     * if the fork is taken it returns null.
     *
     * @param chair  Chair from where the method tries to take the left fork
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return the left fork if not already taken, null otherwise
     */
    public synchronized Fork getLeftFork(final Chair chair, final int philId) {
        final Fork leftFork = forks.get(chair.getId());
        if (leftFork.isTaken()) {
            return null;
        } else {
            leftFork.setTaken(true);
            System.out.printf("\tPhilospher [%d] took left fork: %d\n", philId, leftFork.getId());
            return leftFork;
        }

    }

    /**
     * This method gets the right fork for the matching chair,
     * if the fork is taken it return null.
     *
     * @param chair  Chair from where the method tries to take the right fork
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return the right fork if not already taken, null otherwise
     */
    public synchronized Fork getRightFork(final Chair chair, final int philId) {
        final Fork rightFork;
        //If the current chair is the last chair in the list
        //The right fork is the first fork in the list
        if (chair.getId() == numberOfPlaces - 1) {
            rightFork = forks.get(0);
        } else {
            rightFork = forks.get(chair.getId() + 1);
        }
        if (rightFork.isTaken()) {
            return null;
        } else {
            System.out.printf("\t\tPhilospher [%d] took right fork: %d\n", philId, rightFork.getId());
            rightFork.setTaken(true);
            return rightFork;
        }
    }

    /**
     * This method iterates through the list of chairs,
     * tries to find a chair which is not taken
     * with the left fork which not taken.
     *
     * @param philId Id of the philosopher trying to get the fork.
     *               For logging used.
     * @return chair if not taken and has left fork, null otherwise
     */
    public synchronized Chair getChair(final int philId) {
        for (Chair chair : chairs) {
            if (!chair.isTaken() && !forks.get(chair.getId()).isTaken()) {
                chair.setTaken(true);
                System.out.printf("Philospher [%d] took chair: %d\n", philId, chair.getId());
                return chair;
            }
        }
        return null;
    }

}
