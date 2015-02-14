package walker.blue.path.lib;

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

    /**
     * Class constructor
     * @param x the column the node is located in the grid
     * @param y the row the node is located in the grid
     * @param z the aisle the node is located in the grid
     */
    public AbstractPathfinderNode(int x, int y, int z) {
        location = new RectCoordinates(x, y, z);
        this.g = 0.0;
        this.h = 0.0;
    }

    /**
     * Class constructor taking in pre-made RectCoordinates location.
     * @param location the index of the node in the grid
     */
    public AbstractPathfinderNode(RectCoordinates location) {
        this.location = location;
        this.g = 0.0;
        this.h = 0.0;
    }

    /**
     * Returns the location of the node in the grid
     * @return the location of the node in the grid
     */
    public RectCoordinates location() {
        return location;
    }

    /**
     * Gets the parent node of this node
     * @return the parent node of this node as an AbstractPathfinderNode
     */
    public AbstractPathfinderNode parent() {
        return parent;
    }

    public void setParent(AbstractPathfinderNode par) {
        parent = par;
    }

    /**
     * Returns the distance from the node to the start node in the path
     * search taking the current path that reaches this node.
     * @return the distance between the start node and this node
     */
    public double g() {
        return g;
    }

    public void setG(double val) {
        g = val;
    }

    /**
     * Returns the approximate distance from this node to the destination
     * node in the path search.
     * @return the approximate distance from this node to the destination
     */
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

