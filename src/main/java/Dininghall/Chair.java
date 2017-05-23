package Dininghall;

public class Chair {

    private boolean taken;

    private int id;

    public Chair(final int id) {
        this.taken = false;
        this.id = id;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public boolean isTaken() {
        return taken;
    }

    public synchronized boolean aquireChair(){
        boolean aquired = false;
        if(!this.taken) {
            this.taken = true;
            aquired = true;
        }
        return aquired;
    }

    public int getId() {
        return id;
    }
}
