package walker.blue.path.lib;

/**
 * This class holds the remaining necessary node information for
 * path planning to take place when using algorithms that don't
 * rely on a grid layout search space (i.e. adjacency list, etc.)
 */
public class GraphNode extends AbstractPathfinderNode {
    protected int index;

    /**
     * Class constructor taking in x, y, and z coordinates that
     * set the node's location.
     */
    public GraphNode(int x, int y, int z, int index) {
        super(x, y, z);
        this.index = index;
    }

    /**
     * Class constructor taking in a RectCoordinates object
     * that sets the node's location.
     */
    public GraphNode(RectCoordinates location, int index) {
        super(location);
        this.index = index;
    }

    /**
     * Access method to retrieve the node's index.
     * @return int
     */
    public int getIndex() {
        return index;
    }
}
