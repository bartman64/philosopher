package Dininghall;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ForkRemote extends Remote {

    /**
     * Changes the taken state of a fork
     * @param taken Taken state of the fork
     */
    void setTaken(boolean taken) throws RemoteException;

    /**
     * Remote Method which returns the taken state of the fork
     * @return Returns taken
     */
    boolean isTaken() throws RemoteException;

    /**
     * Returns the fork id
     */
    int getId() throws RemoteException;

    /**
     * Sets the taken State of the fork to true if it is not used
     * @return Returns wether or not the fork got aquired
     */
    boolean aquireFork() throws RemoteException;
}
