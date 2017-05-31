package Dininghall;


import java.rmi.RemoteException;

public class Fork implements ForkRemote {

    private boolean taken;

    private int id;

    public Fork(final int id) {
        this.taken = false;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public synchronized void setTaken(boolean taken) {
        if (!taken) {
            this.notify();
        }
        this.taken = taken;
    }

    @Override
    public boolean isTaken() throws RemoteException {
        return taken;
    }

    public synchronized boolean aquireFork() {
        boolean result = false;
        if (!this.taken) {
            this.taken = true;
            result = true;
        }
        return result;
    }
}
