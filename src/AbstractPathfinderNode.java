
public abstract class AbstractPathfinderNode implements Comparable<AbstractPathfinderNode> {
    /**
     * Holds the index of the node within the search area
     */
    protected int index;
    /**
     * Holds the parent node that will help with following the path
     */
    protected AbstractPathfinderNode parent;
    /**
     * Distance from the starting node to this node following the current path
     */
    protected double g;
    /**
     * Estimated distance from this node to the destination node
     */
    protected double h;

    public AbstractPathfinderNode(int index) {
        this.index = index;
        this.g = 0.0;
        this.h = 0.0;
    }
    
    public int index() {
        return index;
    }
    
    public AbstractPathfinderNode parent() {
        return parent;
    }
    
    public void setParent(AbstractPathfinderNode par) {
        parent = par;
    }
    
    public double g() {
        return g;
    }
    
    public void setG(double val) {
        g = val;
    }
    
    public double h() {
        return h;
    }
      
    public void setH(double val) {
        h = val;
    }

    @Override
    public int compareTo(AbstractPathfinderNode another) {
        double f = this.h + this.g;
        double anotherF = another.h + another.g;
        if(f < anotherF) {
            return -1;
        }
        else if(f > anotherF) {
            return 1;
        }
        else {
            return 0;
        }
    }
    
}

