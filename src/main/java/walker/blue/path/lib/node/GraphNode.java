package walker.blue.path.lib.node;

import walker.blue.path.lib.base.AbstractPathfinderNode;

/**
 * This class holds the remaining necessary node information for
 * path planning to take place when using algorithms that don't
 * rely on a grid layout search space (i.e. adjacency list, etc.)
 */
public class GraphNode extends AbstractPathfinderNode {

    /**
     * Index of the node
     */
    protected int index;

    /**
     * Class constructor taking in x, y, and z coordinates that
     * set the node's location.
     *
     * @param x X value of the nodes location
     * @param y Y value of the nodes location
     * @param z Z value of the nodes location
     * @param index index of the node
     */
    public GraphNode(int x, int y, int z, int index) {
        super(x, y, z);
        this.index = index;
    }

    /**
     * Class constructor taking in a RectCoordinates object
     * that sets the node's location.
     *
     * @param location Location of the node
     * @param index index of the node
     */
    public GraphNode(RectCoordinates location, int index) {
        super(location);
        this.index = index;
    }

    /**
     * Access method to retrieve the node's index.
     *
     * @return int
     */
    public int getIndex() {
        return index;
    }
}
