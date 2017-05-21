package Client;

import Dininghall.Chair;
import Dininghall.ChairRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public interface ClientControl extends Remote {

    /**
     * This method initializes a client set up.
     * It gets the number of philosophers and
     * the number of seats to set up the meditation and dining hall.
     *
     * @param numberOfPhilosophers amount of philosophers in the client system.
     * @param numberOfSeats        amount of philosopher in the client system.
     * @throws RemoteException if an remote error occurs
     */
    void init(final int numberOfPhilosophers, final int numberOfSeats, final Registry registry) throws RemoteException;

    /**
     * This method start the runnable philosophers and the tablemaster thread.
     */
    void startClient() throws RemoteException;

    ChairRemote searchEmptyChair(final int philosophersId) throws RemoteException;

}
