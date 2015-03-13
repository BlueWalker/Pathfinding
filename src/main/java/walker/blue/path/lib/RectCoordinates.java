package walker.blue.path.lib;

/**
 * This class provides a simple way to group 3D rectangular coordinates
 * together as int primitives.
 */
public class RectCoordinates {

    /**
     * Holds the x-coordinate
     */
    private int x;

    /**
     * Holds the y-coordinate
     */
    private int y;

    /**
     * Holds the z-coordinate
     */
    private int z;

    /**
     * Class constructor accepting x, y, and z coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public RectCoordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Access method for the x-coordinate.
     * @return int
     */
    public int getX() {
        return x;
    }

    /**
     * Access method for the y-coordinate.
     * @return int
     */
    public int getY() {
        return y;
    }

    /**
     * Access method for the z-coordinate
     * @return int
     */
    public int getZ() {
        return z;
    }

    /**
     * Overrides the equals checking if the x, y, and z coordinates are equal.
     * @param obj the other object
     * @return boolean
     */
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

    /**
     * Gives the x, y, and z coordinates in String form.
     * @return String
     */
    @Override
    public String toString() {
        return "X: " + this.x + ", Y: " + this.y + ", Z: " + this.z;
    }
}
