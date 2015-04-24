package walker.blue.path.lib.base;

import walker.blue.path.lib.node.RectCoordinates;

/**
 * This abstract class holds basic node information needed to find a
 * path between two nodes in a group of nodes.
 */
public abstract class AbstractPathfinderNode implements Comparable<AbstractPathfinderNode> {

    /**
     * Used to give an x, y, and z position of the node in 3D space.
     */
    protected RectCoordinates location;

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

    /**
     * Class constructor
     *
     * @param x the column the node is located in the grid
     * @param y the row the node is located in the grid
     * @param z the aisle the node is located in the grid
     */
    public AbstractPathfinderNode(int x, int y, int z) {
        this.location = new RectCoordinates(x, y, z);
        this.g = 0.0;
        this.h = 0.0;
    }

    /**
     * Class constructor taking in pre-made RectCoordinates location.
     *
     * @param location the index of the node in the grid
     */
    public AbstractPathfinderNode(RectCoordinates location) {
        this.location = location;
        this.g = 0.0;
        this.h = 0.0;
    }

    /**
     * Returns the location of the node in the grid
     *
     * @return the location of the node in the grid
     */
    public RectCoordinates getLocation() {
        return this.location;
    }

    /**
     * Gets the parent node of this node
     *
     * @return the parent node of this node as an AbstractPathfinderNode
     */
    public AbstractPathfinderNode getParent() {
        return this.parent;
    }

    /**
     * Sets the parent of this node. Used when calculating a path.
     *
     * @param parent new parent
     */
    public void setParent(AbstractPathfinderNode parent) {
        this.parent = parent;
    }

    /**
     * Returns the distance from the node to the start node in the path
     * search taking the current path that reaches this node.
     *
     * @return the distance between the start node and this node
     */
    public double getG() {
        return this.g;
    }

    /**
     * Sets the g value for this node. Used when calculating a path.
     *
     * @param val new value of G
     */
    public void setG(double val) {
        this.g = val;
    }

    /**
     * Returns the approximate distance from this node to the destination
     * node in the path search.
     *
     * @return the approximate distance from this node to the destination
     */
    public double getH() {
        return this.h;
    }

    /**
     * Sets the h value for this node. Used when calculating a path.
     *
     * @param val new value of H
     */
    public void setH(double val) {
        this.h = val;
    }

    /**
     * Calculates the f values of both nodes, which is just the addition
     * of the g and h values, to determine whether the other node
     * is farther or closer to the destination while following the current
     * path.
     *
     * @param another the other AbstractPathfinderNode being compared to this one
     * @return  -1 if this node has a smaller f value,
     *          0 if both nodes have the same f value,
     *          1 if this node has a greater f value
     */
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

    /**
     * Returns a formatted String holding the location of the node.
     *
     * @return String
     */
    @Override
    public String toString() {
        return this.location.toString();
    }
}

