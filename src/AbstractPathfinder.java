import java.util.List;

public abstract class AbstractPathfinder<E, T> implements Pathfinder<E, T> {
    protected T searchArea;

    public AbstractPathfinder(T searchArea) {
        this.searchArea = searchArea;
    }

    public abstract List<E> findPath(E start, E dest);
}
