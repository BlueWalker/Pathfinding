
public class GridNode extends AbstractPathfinderNode {
    /**
     * Holds the state of the node, whether its a wall or a walkable area
     */
    private boolean walkable;
    
    public GridNode(int index, boolean walkable) {
        super(index);
        this.walkable = walkable;
    }
    
    public boolean walkable() {
        return this.walkable;
    }
}
