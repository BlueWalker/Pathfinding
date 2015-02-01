package walker.blue.path.lib;

public class NodeMapper {
    
    private double nodeDistance;
    
    private double floorHeight;
    
    
    public NodeMapper(double nodeDistance, double floorHeight) {
        this.nodeDistance = nodeDistance;
        this.floorHeight = floorHeight;
    }
    
    public RectCoordinates getGridLocation(RectCoordinates realLoc) {
        int xIndex = (int)(realLoc.y() / nodeDistance);
        int yIndex = (int)(realLoc.x() / nodeDistance);
        int zIndex = (int)(realLoc.z() / floorHeight);
        
        return new RectCoordinates(xIndex, yIndex, zIndex);
    }
    
    public RectCoordinates getRealLocation(RectCoordinates gridLocation) {
        int xLoc = (int)(gridLocation.x() * nodeDistance);
        int yLoc = (int)(gridLocation.y() * nodeDistance);
        int zLoc = (int)(gridLocation.z() * floorHeight);
        
        return new RectCoordinates(xLoc, yLoc, zLoc);
    }
    
    public double getNodeDistance() {
        return this.nodeDistance;
    }
    
    public double getFloorHeight() {
        return this.floorHeight;
    }
}
