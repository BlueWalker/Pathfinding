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
     */
    public GridNode(int x, int y, int z, boolean walkable) {
        super(x, y, z);
        this.walkable = walkable;
    }

    /**
     * Class constructor.
     */
    public GridNode(RectCoordinates location, boolean walkable) {
        super(location);
        this.walkable = walkable;
    }

    public boolean walkable() {
        return this.walkable;
    }
}
