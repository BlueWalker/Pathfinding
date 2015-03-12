package walker.blue.path.lib;

/**
 * Created by Josh on 12/14/2014.
 */
public class GridNode extends AbstractPathfinderNode {
    /**
     * Holds the state of the node, whether its a wall or a walkable area
     */
    protected boolean walkable;

    /**
     * Class constructor.
     * @param x the column the node is located in the grid
     * @param y the row the node is located in the grid
     * @param z the aisle the node is located in the grid
     * @param walkable determines whether the node can be walked through
     */
    public GridNode(int x, int y, int z, boolean walkable) {
        super(x, y, z);
        this.walkable = walkable;
    }

    /**
     * Class constructor.
     * @param location the index of the node in the grid
     * @param walkable determines whether the node can be walked through
     */
    public GridNode(RectCoordinates location, boolean walkable) {
        super(location);
        this.walkable = walkable;
    }

    public boolean walkable() {
        return this.walkable;
    }

    /**
     * TODO: Should we check other things?
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GridNode)) {
            return false;
        }
        GridNode other = (GridNode) obj;
        if (!location.equals(other.location)) {
            return false;
        }
        if (walkable != other.walkable) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Walkable: " + this.walkable();
    }
}
