package Dininghall;


public class Fork {

    private boolean taken;

    private int id;

    public Fork(final int id) {
        this.taken = false;
        this.id = id;
    }

    public boolean isTaken() {
        return taken;
    }

    public int getId() {
        return id;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
}
