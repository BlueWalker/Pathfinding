package walker.blue.path.lib.node;

import walker.blue.path.lib.base.AbstractPathfinderNode;

/**
 * This class subclasses AbstractPathfinderNode and inludes the
 * rest of the information needed to perform path planning using
 * a grid as the structure for describing the search area.
 */
public class GridNode extends AbstractPathfinderNode {

    /**
     * Holds the state of whether the node is an obstruction or walkable area
     */
    protected boolean traversable;

    /**
     * Class constructor taking in x, y, and z coordinates to set the location.
     *
     * @param x the column the node is located in the grid
     * @param y the row the node is located in the grid
     * @param z the aisle the node is located in the grid
     * @param traversable determines whether the node can be traversed through
     */
    public GridNode(int x, int y, int z, boolean traversable) {
        super(x, y, z);
        this.traversable = traversable;
    }

    /**
     * Class constructor taking in a RectCoordinates object to set the location.
     *
     * @param location the index of the node in the grid
     * @param traversable determines whether the node can be traversed through
     */
    public GridNode(RectCoordinates location, boolean traversable) {
        super(location);
        this.traversable = traversable;
    }

    /**
     * Access method to determine if the node is traversable or not.
     *
     * @return boolean representing whether the node is transversable
     */
    public boolean isTraversable() {
        return this.traversable;
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
        if (traversable != other.traversable) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Traversable: " + this.traversable;
    }
}
