package walker.blue.path.lib.floor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import walker.blue.path.lib.node.GridNode;

/**
 * This class inherits from GridNode and holds extra information
 * needed to connect different floors in a 3D grid together.
 * This class represents either a staircase, elevator, or anything
 * that connects two floors together in a building.
 */
public class FloorConnector extends GridNode {

    /**
     * Enum holding the possible different types of a floor connector.
     */
    public enum Type {
        NONE,
        STAIRS,
        ELEVATOR
    }

    /**
     * Index within array of other connectors.
     */
    private int index;

    /**
     * The type of floor connector.
     */
    private Type type;

    /**
     * The other connectors connected to this.
     */
    private List<FloorConnector> connections;

    /**
     * Constructor taking in individual x, y, and z coordinates as well as
     * a traversable boolean.
     * as the
     *
     * @param x the x coordinate of the node
     * @param y the y coordinate of the node
     * @param z the z coordinate of the node
     * @param traversable the value determining whether the node an be traversed
     */
    public FloorConnector(int x, int y, int z, boolean traversable) {
        this(x, y, z, traversable, new ArrayList<FloorConnector>(), Type.NONE);
    }

    /**
     * Constructor uniquely accepting a floor connector type.
     *
     * @param x the x coordinate of the node
     * @param y the y coordinate of the node
     * @param z the z coordinate of the node
     * @param traversable the value determining whether the node an be traversed
     * @param type the type of the floor connector
     */
    public FloorConnector(int x, int y, int z, boolean traversable, Type type) {
        this(x, y, z, traversable, new ArrayList<FloorConnector>(), type);
    }

    /**
     * Constructor uniquely accepting the connections for the floor connector.
     *
     * @param x the x coordinate of the node
     * @param y the y coordinate of the node
     * @param z the z coordinate of the node
     * @param traversable the value determining whether the node an be traversed
     * @param connections the connections that are connected to this node
     */
    public FloorConnector(int x, int y, int z, boolean traversable, List<FloorConnector> connections) {
        this(x, y, z, traversable, connections, Type.NONE);
    }

    /**
     * Constructor uniquely accepting both the type and connectiong for
     * the floor connector.
     *
     * @param x the x coordinate of the node
     * @param y the y coordinate of the node
     * @param z the z coordinate of the node
     * @param traversable the value determining whether the node an be traversed
     * @param connections the connections that are connected to this node
     * @param type the type of the floor connector
     */
    public FloorConnector(int x,
                          int y,
                          int z,
                          boolean traversable,
                          List<FloorConnector> connections,
                          Type type) {
        super(x, y, z, traversable);
        this.connections = connections;
        this.type = type;
    }

    /**
     * Access method to get the index.
     *
     * @return index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Sets the index for the floor connector
     *
     * @param index value to set the index member
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Access method to get the type.
     *
     * @return type
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Sets the type for the floor connector
     *
     * @param type value to set the type member
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the type of the floor connector as a String.
     *
     * @return floor connector type as a String
     */
    public String getTypeString() {
        switch(type) {
            case NONE:
                return "None";
            case STAIRS:
                return "Stairs";
            case ELEVATOR:
                return "Elevator";
            default:
                return "Unknown";
        }
    }

    /**
     * Access method to get the connections of the floor connector.
     *
     * @return a list of connected FloorConnectors
     */
    public List<FloorConnector> getConnections() {
        return connections;
    }

    /**
     * Adds a connection to the current connections list.
     *
     * @param connection FloorConnector to be added to the current connections
     * @return true on success, false otherwise
     */
    public boolean addConnection(FloorConnector connection) {
        return connections.add(connection);
    }

    /**
     * Adds a collection of FloorConnectors to the current connections list.
     *
     * @param c the collection of FloorConnectors to be added
     * @return true on success, false otherwise
     */
    public boolean addAllConnections(Collection<? extends FloorConnector> c) {
        return connections.addAll(c);
    }

    /**
     * Uses the super class's equals method to determine equality.
     *
     * @param o the other object
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + connections.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if(connections != null) {
            for(FloorConnector connector : connections) {
                buffer.append(connector.getIndex() + " ");
            }
        }
        else {
            buffer.append("null");
        }
        return super.toString() + "\n" +
                "Index: " + this.index + "\n" +
                "Type: " + this.getTypeString() + "\n" +
                "Connections: " + buffer;
    }
}