package Dininghall;

import MeditationHall.Philosopher;

public class Chair {

    private boolean taken;

    private int id;

    private Philosopher queuedPhil = null;

    public Chair(final int id) {
        this.taken = false;
        this.id = id;
    }

    public synchronized void setTaken(boolean taken) {
        this.taken = taken;
        if (queuedPhil != null && !taken) {
            System.out.printf("Chair [%d] Notified Philosopher [%d]\n", id, queuedPhil.getId());
            this.notify();
            resetQueue();
        }
    }

    public boolean isTaken() {
        return taken;
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
            System.out.printf("Added Philosopher [%d] to the queue of chair[%d]\n", queuedPhil.getId(), id);
        }
        return aquired;
    }

    private void resetQueue() {
        this.queuedPhil = null;
        System.out.printf("Reset queue of chair [%d]\n", id);
    }


    public int getId() {
        return id;
    }
}
