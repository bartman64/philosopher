package Dininghall;

import java.rmi.Remote;

public class Chair implements ChairRemote {

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

    public int getId() {
        return id;
    }
}
