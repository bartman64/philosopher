import Dininghall.Dininghall;
import MeditationHall.MeditationHall;
import MeditationHall.Philosopher;
import Dininghall.TableMaster;

public class Main {
    public static void main(String[] args) {
        final Dininghall dininghall = new Dininghall(10);
        dininghall.initHall();
        final MeditationHall meditationHall = new MeditationHall(5, dininghall);
        final TableMaster tableMaster= new TableMaster(meditationHall.getPhilosophers());

        meditationHall.initPhilosophers(1, tableMaster);
        tableMaster.start();

        for (Philosopher philosopher : meditationHall.getPhilosophers()) {
            new Thread(philosopher).start();
        }
    }
}
