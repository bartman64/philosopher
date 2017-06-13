package Dininghall;

import MeditationHall.Philosopher;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.logging.Logger;

public class Fork implements ForkRemote {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Philosopher.class);

    /**
     * Boolean indicating if a fork is in use
     */
    private boolean taken;

    /**
     * Id of the fork
     */
    private int id;

    private boolean remoteWaitingPhil = false;

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
            LOGGER.info("Fork [" + id + "] Notified waiting Philosophers");
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

    @Override
    public synchronized void waitOnObject() throws RemoteException {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
