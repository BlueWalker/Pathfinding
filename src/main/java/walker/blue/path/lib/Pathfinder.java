package walker.blue.path.lib;

import java.util.List;

public interface Pathfinder<E, T> {
    public List<E> findPath(T searchArea, E start, E dest);
}
