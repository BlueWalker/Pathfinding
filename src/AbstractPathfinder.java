import java.util.List;

public abstract class AbstractPathfinder<E, T> implements Pathfinder<E, T> {
    public abstract List<E> findPath(T searchArea, E start, E dest);
}
