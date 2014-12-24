
public abstract class AbstractPathfinderNode implements Comparable<AbstractPathfinderNode> {
    /**
     * Used to give an x, y, and z position of the node in 3D space.
     */
    protected RectCoordinates location;
    /**
     * Holds the parent node that will help with following the path
     */
    protected AbstractPathfinderNode parent;
    /**
     * Distance from the starting node to this node following the current path
     */
    protected double g;
    /**
     * Estimated distance from this node to the destination node
     */
    protected double h;

    public AbstractPathfinderNode(int x, int y, int z) {
//        this.location.x = x;
//        this.location.y = y;
//        this.location.z = z;
        location = new RectCoordinates(x, y, z);
        this.g = 0.0;
        this.h = 0.0;
    }

    public AbstractPathfinderNode(RectCoordinates location) {
        this.location = location;
        this.g = 0.0;
        this.h = 0.0;
    }

    public RectCoordinates location() {
        return location;
    }

    public AbstractPathfinderNode parent() {
        return parent;
    }

    public void setParent(AbstractPathfinderNode par) {
        parent = par;
    }

    public double g() {
        return g;
    }

    public void setG(double val) {
        g = val;
    }

    public double h() {
        return h;
    }

    public void setH(double val) {
        h = val;
    }

    @Override
    public int compareTo(AbstractPathfinderNode another) {
        double f = this.h + this.g;
        double anotherF = another.h + another.g;
        if(f < anotherF) {
            return -1;
        }
        else if(f > anotherF) {
            return 1;
        }
        else {
            return 0;
        }
    }

}

