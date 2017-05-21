package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientControl extends Remote {

    /**
     * This method initializes a client set up.
     * It gets the number of philosophers and
     * the number of seats to set up the meditation and dining hall.
     *
     * @param numberOfPhilosophers amount of philosophers in the client system.
     * @param numberOfSeats amount of philosopher in the client system.
     * @throws RemoteException if an remote error occurs
     */
    void init(final int numberOfPhilosophers, final int numberOfSeats) throws RemoteException;

}
