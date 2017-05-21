package Server;

import Dininghall.Chair;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerControl extends Remote{
    int getId() throws RemoteException;
    Chair searchFreeChair() throws RemoteException;
}
