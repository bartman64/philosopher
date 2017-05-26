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
            final Registry registry = LocateRegistry.createRegistry(1099);
            Server server = new Server(10, 10);
            ServerControl serverStub = (ServerControl) UnicastRemoteObject.exportObject(server, 0);
            registry.bind("Server", serverStub);
            Thread.sleep(10000);
            System.out.println(Arrays.toString(registry.list()));
            server.fillClientList(registry);
            server.initClients(registry);
            server.startClients();
        } catch (RemoteException | AlreadyBoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
