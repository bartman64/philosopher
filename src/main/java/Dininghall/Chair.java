package Dininghall;

public class Chair implements ChairRemote {

    private boolean taken;

    private int id;

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
