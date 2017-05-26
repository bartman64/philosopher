package Server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import Client.ClientControl;
import Dininghall.ChairRemote;

public class Server implements ServerControl {

    /**
     * Int counter counting the number of clients.
     * Also used to set the id for each client.
     */
    private static int counter = 0;

    /**
     * Int value number of total seats in the system.
     */
    private int totalSeats;

    /**
     * Int value number of total philosophers in the system.
     */
    private int totalPhilosophers;

    /**
     * List of clients.
     */
    private final List<ClientControl> clients;


    public Server(final int totalSeats, final int totalPhilosopher) {
        clients = new ArrayList<>();
        this.totalSeats = totalSeats;
        this.totalPhilosophers = totalPhilosopher;
    }

    @Override
    public int getId() throws RemoteException {
        return ++counter;
    }

    @Override
    public ChairRemote searchFreeChair(final int philosoperId) throws RemoteException {
        for (final ClientControl client : clients) {
            final ChairRemote chair = client.searchEmptyChair(philosoperId);
            if (chair != null) {
                return chair;
            }
        }
        return null;
    }

    /**
     * This method fills the looks up the clients in the registry
     * and adds them to the list of client controls
     *
     * @param registry containing the binded clients
     */
    public void fillClientList(final Registry registry) {
        for (int i = 0; i < counter; i++) {
            try {
                clients.add((ClientControl) registry.lookup("Client" + (i + 1)));
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method initializes every client in the list.
     * Calculates the distribution rate of seats and philosophers between the amount of clients.
     */
    public void initClients(final Registry registry) {
        int deltaSeats = totalSeats;
        int deltaPhilosophers = totalPhilosophers;
        int numberOfSeats = totalSeats / counter;
        int numberOfPhilosophers = totalPhilosophers / counter;
        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).init(numberOfPhilosophers, numberOfSeats, registry, i);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            deltaPhilosophers -= numberOfPhilosophers;
            //The last client gets the rest of the seats an philosophers.
            numberOfPhilosophers = deltaPhilosophers - numberOfPhilosophers < 0 || i == counter - 2 ? deltaPhilosophers : numberOfPhilosophers;
            deltaSeats -= numberOfSeats;
            numberOfSeats = deltaSeats - numberOfSeats < 0 || i == counter - 2 ? deltaPhilosophers : numberOfSeats;
        }
    }

    public void startClients() {
        for (final ClientControl client : clients) {
            try {
                client.startClient();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
