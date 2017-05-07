package Dininghall;


import java.util.ArrayList;
import java.util.List;

public class Dininghall {

    private final List<Chair> chairs;

    private final List<Fork> forks;

    private final int numberOfPlaces;

    public Dininghall(final int numberOfPlaces) {
        this.chairs = new ArrayList<Chair>();
        this.forks = new ArrayList<Fork>();
        this.numberOfPlaces = numberOfPlaces;
    }

    public void initHall() {
        for (int i = 0; i < numberOfPlaces; i++) {
            chairs.add(new Chair(i));
            forks.add(new Fork(i));
        }
        if(numberOfPlaces == 1) {
            forks.add(new Fork(numberOfPlaces));
        }
    }

    public synchronized Fork getLeftFork(final Chair chair, final int philId) {
        final Fork leftFork = forks.get(chair.getId());
        if(leftFork.isTaken()){
            return null;
        } else {
            leftFork.setTaken(true);
            System.out.printf("\tPhilospher [%d] took left fork: %d\n", philId, leftFork.getId());
            return leftFork;
        }

    }

    public synchronized Fork getRightFork(final Chair chair, final int philId) {
        final Fork rightFork;
        if (chair.getId() == numberOfPlaces - 1) {
            rightFork = forks.get(0);
        } else {
           rightFork = forks.get(chair.getId() + 1);
        }
        if(rightFork.isTaken()){
            return null;
        } else {
            System.out.printf("\t\tPhilospher [%d] took right fork: %d\n", philId, rightFork.getId());
            rightFork.setTaken(true);
            return rightFork;
        }
    }

    public synchronized Chair getChair(final int philId){
        for(Chair chair : chairs){
            if(!chair.isTaken() && !forks.get(chair.getId()).isTaken()){
                chair.setTaken(true);
                System.out.printf("Philospher [%d] took chair: %d\n", philId, chair.getId());
                return chair;
            }
        }
        return null;
    }

}
