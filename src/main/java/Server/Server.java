package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Client.ClientControl;
import Dininghall.ChairRemote;
import Dininghall.ForkRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements ServerControl {


    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
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


    /**
     * Registry needed to call proxyBind.
     */
    private final Registry registry;


    public Server(final int totalSeats, final int totalPhilosopher, Registry registry) {
        clients = new ArrayList<>();
        this.totalSeats = totalSeats;
        this.totalPhilosophers = totalPhilosopher;
        this.registry = registry;
    }

    @Override
    public int getId() throws RemoteException {
        return ++counter;
    }

    @Override
    public ChairRemote searchFreeChair(final int philosopherId) throws RemoteException {
        for (final ClientControl client : clients) {
            final ChairRemote chair = client.searchEmptyChair(philosopherId);
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
        int prevSeats = 0;
        int prevPhils = 0;
        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).init(numberOfPhilosophers, numberOfSeats, registry, prevSeats, prevPhils, totalSeats);
                LOGGER.info("Client[" + i + "] with Seats: " + numberOfSeats + " and Phils: " + numberOfPhilosophers);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            prevSeats += numberOfSeats;
            prevPhils += numberOfPhilosophers;

            deltaPhilosophers -= numberOfPhilosophers;
            //The last client gets the rest of the seats an philosophers.
            numberOfPhilosophers = deltaPhilosophers - numberOfPhilosophers < 0 || i == counter - 2 ? deltaPhilosophers : numberOfPhilosophers;
            deltaSeats -= numberOfSeats;
            numberOfSeats = deltaSeats - numberOfSeats < 0 || i == counter - 2 ? deltaSeats : numberOfSeats;

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

    @Override
    public int calcTotalAvg() {
        int totalAvg = 0;
        for (ClientControl client : clients) {
            try {
                totalAvg += client.avgCalc();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        totalAvg /= clients.size();
        return totalAvg;
    }

    /**
     * Bind a Remote obj to the Server that isn't running on the same System as the Server.
     *
     * @param name name of the remote obj
     * @param obj  remote obj
     * @return String that indicates success
     * @throws RemoteException on failure
     */
    @Override
    public String proxyBind(String name, Remote obj) throws RemoteException {
        String res = "Registered" + name + " successfully!";
        Runnable bindToRegistry = () -> {
            try {
                registry.bind(name, obj);
                System.out.print("Registered " + name + "\n");
            } catch (RemoteException | AlreadyBoundException e) {
                e.printStackTrace();
            }
        };

        Thread binder = new Thread(bindToRegistry);

        binder.start();

        return res;
    }


    private void initNewTable(final Registry registry) {
        int deltaSeats = totalSeats;
        int numberOfSeats = totalSeats / counter;

        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).initNewTable(numberOfSeats, registry, i, totalSeats);
                LOGGER.info("Client[" + i + "] with Seats: " + numberOfSeats);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            deltaSeats -= numberOfSeats;
            numberOfSeats = deltaSeats - numberOfSeats < 0 || i == counter - 2 ? deltaSeats : numberOfSeats;
        }
    }


    public void increaseTableSize(final int newSeatAmount, final Registry registry) {
        try {

            for (final ClientControl client : clients) {
                client.clearDininghall();
            }
            Thread.sleep(1000);
            for (int i = 0; i < totalSeats; i++) {
                registry.unbind("Chair" + i);
                registry.unbind("Fork" + i);

            }
            setTotalSeats(newSeatAmount);
            initNewTable(registry);
            for (final ClientControl clientControl : clients) {
                clientControl.restartClients();
                LOGGER.info("Restart clients");
            }
        } catch (RemoteException | NotBoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setTotalPhilosophers(int totalPhilosophers) {
        this.totalPhilosophers = totalPhilosophers;
    }

    public void addPhils(final int amount) {
        int totalNew = amount;
        int philsPerClient = amount / counter;
        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).addPhils(philsPerClient, totalPhilosophers);
                totalNew -= philsPerClient;
                philsPerClient = totalNew - philsPerClient < 0 || i == counter - 2 ? totalNew : philsPerClient;
                setTotalPhilosophers(totalPhilosophers + philsPerClient);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void removePhils(final int amount) {
        int totalNew = amount;
        int philsPerClient = amount / counter;
        for (int i = 0; i < clients.size(); i++) {
            try {
                clients.get(i).stopPhils(philsPerClient);
                totalNew -= philsPerClient;
                philsPerClient = totalNew - philsPerClient < 0 || i == counter - 2 ? totalNew : philsPerClient;
                setTotalPhilosophers(totalPhilosophers - philsPerClient);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Removed " + amount + "phils");

    }

    public void stopClients() {
        for (final ClientControl client : clients) {
            try {
                client.stopClient();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("STOPPED");
    }
}
