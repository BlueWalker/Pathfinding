import java.util.List;

public interface Pathfinder<E, T> {
    public List<E> findPath(E start, E dest);
}
