import Dininghall.Dininghall;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;

public class Main {
    public static void main(String[] args) {
        final Dininghall dininghall = new Dininghall(10);
        dininghall.initHall();
        final MeditationHall meditationHall = new MeditationHall(5 ,dininghall);
        meditationHall.initPhilosophers();

        for(Philosopher philosopher: meditationHall.getPhilosophers()){
            philosopher.start();
        }
    }
}
