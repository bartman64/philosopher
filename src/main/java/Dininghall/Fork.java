package Dininghall;


import java.rmi.RemoteException;

public class Fork implements ForkRemote {

    /**
     * Boolean indicating if a fork is in use
     */
    private boolean taken;

    /**
     * Id of the fork
     */
    private int id;

    /**
     * Ctor of a Fork
     * @param id Id of the fork
     */
    public Fork(final int id) {
        this.taken = false;
        this.id = id;
    }

    /**
     * Returns the fork id
     */
    public int getId() {
        return id;
    }

    /**
     * Changes the taken state of a fork
     * @param taken Taken state of the fork
     */
    public synchronized void setTaken(boolean taken) {
        if (!taken) {
            this.notifyAll();
        }
        this.taken = taken;
    }

    /**
     * Remote Method which returns the taken state of the fork
     * @return Returns taken
     * @throws RemoteException
     */
    @Override
    public boolean isTaken() throws RemoteException {
        return taken;
    }

    /**
     * Sets the taken State of the fork to true if it is not used
     * @return
     */
    public synchronized boolean aquireFork() {
        boolean result = false;
        if (!this.taken) {
            this.taken = true;
            result = true;
        }
        return result;
    }
}
