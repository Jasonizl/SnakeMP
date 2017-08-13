package game;

/**
 * Created by Jason on 13.08.2017.
 */
public class Pair {

    private int x;
    private int y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @param obj
     * @return true if x and y are the same
     */
    @Override
    public boolean equals(Object obj) {
        Pair secPair = (Pair) obj;
        if (this.getX() == secPair.getX() && this.getY() == secPair.getY())
            return true;
        return false;

    }
}
