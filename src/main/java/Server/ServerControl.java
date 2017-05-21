package Server;

import Dininghall.ChairRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerControl extends Remote {
    int getId() throws RemoteException;

    ChairRemote searchFreeChair(final int philosoperId) throws RemoteException;
}
