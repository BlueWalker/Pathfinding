package walker.blue.path.lib.base;

import java.util.List;

/**
 * Abstract class that implements the Pathfinder interface.
 * @param <E> the node type used in the searchArea
 * @param <T> the type used to hold all the nodes
 */
public abstract class AbstractPathfinder<E, T> implements Pathfinder<E, T> {

    /**
     * Generates a list of nodes of type E holding the path.
     *
     * @param searchArea the space that is used in the path search
     * @param start the start node
     * @param dest the end node
     * @return
     */
    public abstract List<E> findPath(T searchArea, E start, E dest);
}
