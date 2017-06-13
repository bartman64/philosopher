package Server;

import Client.ClientControl;
import Dininghall.ChairRemote;
import Dininghall.ForkRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerControl extends Remote {
    int getId() throws RemoteException;

    String proxyBind(String name, Remote obj) throws RemoteException;

}
