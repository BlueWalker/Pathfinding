package walker.blue.path.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FloorConnector extends GridNode {
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
     * Elevator or staircase? Name needed for telling user?
     */
    private Type type;
    /**
     * The other connectors connected to this.
     */
    private List<FloorConnector> connections;
    /**
     * Constructor.
     * @param x
     * @param y
     * @param z
     * @param walkable
     */
    public FloorConnector(int x, int y, int z, boolean walkable) {
        this(x, y, z, walkable, new ArrayList<FloorConnector>(), Type.NONE);
    }

    public FloorConnector(int x, int y, int z, boolean walkable, Type type) {
        this(x, y, z, walkable, new ArrayList<FloorConnector>(), type);
    }

    public FloorConnector(int x, int y, int z, boolean walkable, List<FloorConnector> connections) {
        this(x, y, z, walkable, connections, Type.NONE);
    }

    /**
     * Constructor.
     * @param x
     * @param y
     * @param z
     * @param walkable
     * @param connections
     */
    public FloorConnector(int x,
                          int y,
                          int z,
                          boolean walkable,
                          List<FloorConnector> connections,
                          Type type) {
        super(x, y, z, walkable);
        this.connections = connections;
        this.type = type;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

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
     *
     * @return
     */
    public List<FloorConnector> getConnections() {
        return connections;
    }
    
    public boolean addConnection(FloorConnector connection) {
        return connections.add(connection);
    }

    public boolean addAllConnections(Collection<? extends FloorConnector> c) {
        return connections.addAll(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

//        FloorConnector that = (FloorConnector) o;
//
//        if (index != that.index) return false;
//        if (!connections.equals(that.connections)) return false;
//        if (!type.equals(that.type)) return false;

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