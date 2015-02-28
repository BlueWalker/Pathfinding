package walker.blue.path.lib;

/**
 * Created by Josh on 12/14/2014.
 */
public class RectCoordinates {
    private int x;
    private int y;
    private int z;

    /**
     * Class constructor.
     */
    public RectCoordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int x() { return x; }

    public int y() { return y; }

    public int z() { return z; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RectCoordinates)) {
            return false;
        }
        RectCoordinates other = (RectCoordinates) obj;
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        if (z != other.z) {
            return false;
        }
        return true;
    }
}
