/**
 * Created by Josh on 12/14/2014.
 */
public class GraphNode extends AbstractPathfinderNode {
    protected int index;

    /**
     * Class constructor.
     */
    public GraphNode(int x, int y, int z, int index) {
        super(x, y, z);
        this.index = index;
    }

    /**
     * Class constructor.
     */
    public GraphNode(RectCoordinates location, int index) {
        super(location);
        this.index = index;
    }

    public int index() { return index; }
}
