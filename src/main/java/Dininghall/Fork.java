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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public synchronized void setTaken(boolean taken) {
        if (!taken) {
            this.notifyAll();
        }
        this.taken = taken;
    }

    @Override
    public boolean isTaken(){
        return taken;
    }

    @Override
    public synchronized boolean aquireFork() {
        boolean result = false;
        if (!this.taken) {
            this.taken = true;
            result = true;
        }
        return result;
    }
}
