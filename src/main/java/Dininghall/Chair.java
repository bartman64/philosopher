package Dininghall;

import MeditationHall.Philosopher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chair implements ChairRemote {

    private static final Logger LOGGER = LoggerFactory.getLogger(Chair.class);

    /**
     * Boolean variable indicating if the chair got taken from  philosopher.
     */
    private boolean taken;

    /**
     * Int value for identifying a chair.
     */
    private int id;

    /**
     * Philosopher which is queued behind the chair waiting for a empty seat.
     */
    private Philosopher queuedPhil = null;

    /**
     * Constructure for chair.
     * Getting the id needed to identify each chair in the system.
     *
     * @param id Int value for identification purpose
     */
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

    @Override
    public synchronized void setTaken(final boolean taken) {
        this.taken = taken;
        if (queuedPhil != null && !taken) {
            LOGGER.info("Chair [" + id + "] Notified Philosopher [" + queuedPhil.getId() + "]");
            queuedPhil.setWaiting(false);
            this.notify();
            resetQueue();
        }
    }

    /**
     * This method is called if an philosopher wants a chair.
     * It looks if the chair is already acquired and
     * if not it will set the boolean to true.
     *
     * @return true if the chair was empty before and got acquired, false otherwise
     */
    public synchronized boolean aquireChair() {
        boolean aquired = false;
        if (!this.taken) {
            this.setTaken(true);
            aquired = true;
        }
        return aquired;
    }

    /**
     * This method looks if the queue place is empty and sets the philosopher into the queue.
     *
     * @param philosopher Philosopher which wants to acquire the queue.
     * @return false if the queue is already occupied, true otherwise
     */
    public synchronized boolean aquireQueuedChair(final Philosopher philosopher) {
        boolean aquired = false;
        if (this.taken && this.queuedPhil == null) {
            aquired = true;
            this.queuedPhil = philosopher;
            queuedPhil.setWaiting(true);
            LOGGER.info("Added Philosopher [" + queuedPhil.getId() + "] to the queue of chair[" + id + "]");
        }
        return aquired;
    }

    /**
     * This method emtpies the queue.
     */
    private void resetQueue() {
        this.queuedPhil = null;
        LOGGER.info("Reset queue of chair [" + id + "]");
    }

    @Override
    public int getId() {
        return id;
    }
}
