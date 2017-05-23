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

    public void setTaken(boolean taken){
        this.taken = taken;
    }

    public int getId() {
        return id;
    }

    public synchronized boolean aquireFork(){
        boolean result = false;
        if(!this.taken) {
            this.taken = true;
            result = true;
        }
            return result;
    }
}
