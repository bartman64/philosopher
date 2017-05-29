package Dininghall;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ForkRemote extends Remote {

    void setTaken(boolean taken) throws RemoteException;

    boolean isTaken() throws RemoteException;

    int getId() throws RemoteException;
}
