package Server;

import Client.ClientControl;
import Dininghall.ChairRemote;
import Dininghall.ForkRemote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerControl extends Remote {
    int getId() throws RemoteException;

    ChairRemote searchFreeChair(final int philosoperId) throws RemoteException;

    int calcTotalAvg() throws RemoteException;

}
