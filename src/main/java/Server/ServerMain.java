package Server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class ServerMain {
    public static void main(String[] args) {
        try {
            final int totalSeats = 10;
            final int totalPhilosphers = 10;
            final Registry registry = LocateRegistry.createRegistry(1099);
            Server server = new Server(totalSeats, totalPhilosphers);
            ServerControl serverStub = (ServerControl) UnicastRemoteObject.exportObject(server, 0);
            registry.bind("Server", serverStub);
            Thread.sleep(6000);
            System.out.println(Arrays.toString(registry.list()));
            server.fillClientList(registry);
            server.initClients(registry);
            server.startClients();
            Thread.sleep(5000);
            server.increaseTableSize(20, registry);

           Thread.sleep(12000);
            server.increaseTableSize(10, registry);



        } catch (RemoteException | AlreadyBoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
