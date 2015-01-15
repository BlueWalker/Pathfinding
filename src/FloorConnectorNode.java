import java.util.List;

/**
 * Created by Josh on 12/17/2014.
 */
public class FloorConnectorNode extends GridNode {
    private int index;
    private List<RectCoordinates> connections;

    public FloorConnectorNode(int x, int y, int z, boolean walkable, int index) {
        super(x, y, z, walkable);
        this.index = index;
    }

    public FloorConnectorNode(int x, int y, int z, boolean walkable, int index, List<RectCoordinates> connections) {
        super(x, y, z, walkable);
        this.index = index;
        this.connections = connections;
    }

    public int index() { return index; }

    public List<RectCoordinates> connections() { return connections; }
}