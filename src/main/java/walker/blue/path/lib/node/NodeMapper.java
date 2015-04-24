package walker.blue.path.lib.node;

/**
 * This class can be used to convert between the grid coordinate system
 * that uses indices to determine the distance between nodes and a real world
 * coordinate system which uses real world units. Assumptions must be made
 * for this class to work properly: 1) The floor heights of the building
 * must be all equal.
 *
 * <p>Example:  If node distance is 5, the distance between two nodes
 *              in a grid that are adjacent to each other would be 1,
 *              whereas the real world distance would be 5.
 */
public class NodeMapper {

    /**
     * Holds the real world distance between adjacent nodes in the grid.
     */
    private double nodeDistance;

    /**
     * Holds the real world floor height of the building. In a 3D grid, this
     * value is used to determine the distance between adjacent nodes
     * along the vertical/z axis. Note: This assumes that all floors
     * in the building are of equal height.
     */
    private double floorHeight;

    /**
     * Constructor that accepts the node distance of the grid and
     * the floor height of the grid.
     *
     * @param nodeDistance distance between adjacent nodes along horizontal axes
     * @param floorHeight distance between adjacent nodes along vertical axis
     */
    public NodeMapper(double nodeDistance, double floorHeight) {
        this.nodeDistance = nodeDistance;
        this.floorHeight = floorHeight;
    }

    /**
     * Gets the index of the grid according to the given real world
     * rectangular coordinates.
     *
     * @param realLoc real world coordinates
     * @return location of the grid
     */
    public RectCoordinates getGridLocation(RectCoordinates realLoc) {
        int xIndex = (int)(realLoc.getY() / nodeDistance);
        int yIndex = (int)(realLoc.getX() / nodeDistance);
        int zIndex = (int)(realLoc.getZ() / floorHeight);
        
        return new RectCoordinates(xIndex, yIndex, zIndex);
    }

    /**
     * Gets the real world coordinates from the given grid index location
     * by using the node distance and floor height member variables.
     *
     * @param gridLocation specific indices within the grid
     * @return RectCoordinates
     */
    public RectCoordinates getRealLocation(RectCoordinates gridLocation) {
        int xLoc = (int)(gridLocation.getX() * nodeDistance);
        int yLoc = (int)(gridLocation.getY() * nodeDistance);
        int zLoc = (int)(gridLocation.getZ() * floorHeight);
        
        return new RectCoordinates(xLoc, yLoc, zLoc);
    }

    /**
     * Access method to get the node distance.
     *
     * @return double
     */
    public double getNodeDistance() {
        return this.nodeDistance;
    }

    /**
     * Access method to get the floor height.
     *
     * @return double
     */
    public double getFloorHeight() {
        return this.floorHeight;
    }
}
