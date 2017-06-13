package Client;

import Dininghall.Chair;
import Dininghall.ChairRemote;
import Dininghall.ForkRemote;
import com.sun.org.apache.regexp.internal.RE;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;

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
    void init(final int numberOfPhilosophers, final int numberOfSeats, final Registry registry, final int prevSeats, final int prevPhils, final int totalSeats, final List<ClientControl> clients
              ) throws RemoteException;


    /**
     * This method start the runnable philosophers and the tablemaster thread.
     */
    void startClient() throws RemoteException;

    /**
     * This method searches for an unused chair in the on a client.
     *
     * @param philosophersId Philosopher which wants to acquire a chair
     * @return ChairRemote if an unused chair was found, null otherwise
     * @throws RemoteException Connection lost
     */
    ChairRemote searchEmptyChair(final int philosophersId) throws RemoteException;

    /**
     * Gets the id of a client
     *
     * @return id as int value
     * @throws RemoteException Connection lost
     */
    int getId() throws RemoteException;

    /**
     * This method calls the table master for calculating the average consumption
     *
     * @return the average consumption as int value of a client
     * @throws RemoteException Connection lost
     */
    int avgCalc() throws RemoteException;

    int calcTotalAvg() throws RemoteException;

    /**
     * This method calls the server to calculte the average consumption between all clients.
     *
     * @return the average consumption as int value of a client
     * @throws RemoteException Connection lost
     */
    int getTotalAvg() throws RemoteException;

    /**
     * This method initializes the table new for a client.
     *
     * @param numberOfSeats Number of seats each client will have
     * @param registry      Registry needed for the dininghall fro binding the chairs and forks
     * @param startValue    Start value for the chair and fork indices
     * @param totalSeats    Total amount of seats has to be updated
     * @throws RemoteException Connection lost
     */
    void initNewTable(final int numberOfSeats, final Registry registry, final int startValue, final int totalSeats) throws RemoteException;

    /**
     * This method start a thread which sets the active flag for each philosopher
     * to false to stop the philosopher from running.
     *
     * @throws RemoteException Connection lost
     */
    void clearDininghall() throws RemoteException;

    /**
     * This method restarts the clients and notifies the philosophers after done work.
     *
     * @throws RemoteException Connection lost
     */
    void restartClients() throws RemoteException;

    /**
     * This method adds philosophers to the client.
     *
     * @param amountOfNewPhils amount of new philosophers
     * @param totalPhils       the total amount of curretn philosophers
     * @throws RemoteException Connection lost
     */
    void addPhils(final int amountOfNewPhils, final int totalPhils) throws RemoteException;

    /**
     * This method stops a given amount of philosophers on a client
     * If the amount is bigger than the given amount of
     * philosophers only the existing philosopher get stopped.
     *
     * @param amount Amount of philospher to be stopped
     * @throws RemoteException Connection lost
     */
    void stopPhils(final int amount) throws RemoteException;

    /**
     * This method stops the clients and start a thread which stops the philosophers
     * parallel and outputs the amount of times they ate.
     *
     * @throws RemoteException Connection lost
     */
    void stopClient() throws RemoteException;
}
