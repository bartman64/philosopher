import Dininghall.Dininghall;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;
import Dininghall.TableMaster;

public class Main {
    public static void main(String[] args) {
        final Dininghall dininghall = new Dininghall(3);
        dininghall.initHall();
        final MeditationHall meditationHall = new MeditationHall(2, dininghall);
        final TableMaster tableMaster= new TableMaster();

        meditationHall.initPhilosophers(1, tableMaster);
        tableMaster.initMap(meditationHall.getPhilosophers());
        tableMaster.start();

        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            new Thread(philosopher).start();
        }
    }
}
