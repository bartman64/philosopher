package Dininghall;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChairRemote extends Remote {

    /**
     * This method sets the taken flag.
     * If the chair was used and the flag will be set to false,
     * it notifies the queue if there is a philosopher in it.
     *
     * @param taken boolean variable indicating if the chair will be taken or released
     * @throws RemoteException If the connection is lost
     */
    void setTaken(final boolean taken) throws RemoteException;

    /**
     * Gets the id.
     *
     * @return int value indicating the id
     * @throws RemoteException If the connection is lost
     */
    int getId() throws RemoteException;

}
