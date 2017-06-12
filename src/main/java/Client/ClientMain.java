package Client;

import Server.Server;
import Server.ServerControl;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientMain {

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ServerControl server = (ServerControl) registry.lookup("Server");
            final Client client = new Client();
            ClientControl clientSkelet = (ClientControl) UnicastRemoteObject.exportObject(client, 0);
            final int clientId = server.getId();
            registry.bind("Client" + clientId, clientSkelet);
            client.setId(clientId);
            client.setServer(server);

        } catch (RemoteException | NotBoundException | AlreadyBoundException e) {
            e.printStackTrace();
        }

    }
}
