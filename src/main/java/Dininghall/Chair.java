package Dininghall;

import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chair implements ChairRemote {

    private static final Logger LOGGER = LoggerFactory.getLogger(Chair.class);

    private boolean taken;

    private int id;

    private Philosopher queuedPhil = null;

    public Chair(final int id) {
        this.taken = false;
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chair chair = (Chair) o;

        return taken == chair.taken && id == chair.id;
    }

    @Override
    public int hashCode() {
        int result = (taken ? 1 : 0);
        result = 31 * result + id;
        return result;
    }


    public synchronized void setTaken(boolean taken) {
        this.taken = taken;
        if (queuedPhil != null && !taken) {
            LOGGER.info("Chair [" + id + "] Notified Philosopher [" + queuedPhil.getId() + "]");
            queuedPhil.setWaiting(false);
            this.notify();
            resetQueue();
        }
    }

    public synchronized boolean aquireChair() {
        boolean aquired = false;
        if (!this.taken) {
            this.setTaken(true);
            aquired = true;
        }
        return aquired;
    }

    public synchronized boolean aquireQueuedChair(Philosopher philosopher) {
        boolean aquired = false;
        if (this.taken && this.queuedPhil == null) {
            aquired = true;
            this.queuedPhil = philosopher;
            queuedPhil.setWaiting(true);
            LOGGER.info("Added Philosopher [" + queuedPhil.getId() + "] to the queue of chair[" + id + "]");
        }
        return aquired;
    }

    private void resetQueue() {
        this.queuedPhil = null;
        LOGGER.info("Reset queue of chair [" + id + "]");
    }

    @Override
    public boolean isTaken() {
        return taken;
    }

    public int getId() {
        return id;
    }
}
