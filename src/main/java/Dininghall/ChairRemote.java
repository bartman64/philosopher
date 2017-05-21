package Dininghall;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChairRemote extends Remote {

    void setTaken(final boolean flag) throws RemoteException;

    int getId() throws RemoteException;
}
