package walker.blue.path.lib.finder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import walker.blue.path.lib.base.AbstractPathfinder;
import walker.blue.path.lib.node.GridNode;

/**
 * This class can be used to find a path between two nodes in a given
 * two-dimensional list of nodes.
 */
public class GridAStar extends AbstractPathfinder<GridNode, List<List<GridNode>>> {

    /**
     * Returns a List representing the path between two nodes in a two-dimensional search space.
     *
     * @param searchArea a 2D list holding all of the GridNodes that describe the space
     * @param start      the start node for the path search and must inherit from Node
     * @param dest       the destination node for the path search and must inherit from Node
     * @return           the List of nodes in the path starting from the start node and
     *                   ending with the dest node
     */
    public List<GridNode> findPath(List<List<GridNode>> searchArea, GridNode start, GridNode dest) {
        HashSet<GridNode> closedSet = new HashSet<GridNode>();
        PriorityQueue<GridNode> openQueue = new PriorityQueue<GridNode>();

        start.setParent(null);
        start.setG(0);
        openQueue.add(start);

        // Run while the open list is not empty (if it is, then the destination was never found)
        // and while the open list does not contain the destination node (once it has the
        // destination node the path has been found).
        while(!openQueue.isEmpty()) {

            GridNode node = openQueue.remove();

            // If the destination node has been reached, then return the reconstructed path.
            if(node == dest) {
                return reconstructPath(start, node);
            }

            closedSet.add(node);

            // Find neighbors in the 3D array
            for(int i = -1; i < 2; i++) {
                for(int j = -1; j < 2; j++) {
                    if(i != 0 || j != 0) {
                        try {
                            GridNode neighbor =
                                    searchArea.get(node.getLocation().getY() + i).
                                            get(node.getLocation().getX() + j);

                            // If the neighbor can be walked through and has not been visited
                            // directly, then check to see if the neighbor's values can be updated.
                            //if(!closedSet.contains(neighbor) && neighbor.walkable()) {
                            if(!closedSet.contains(neighbor) && neighbor.isTraversable()) {
                                // If the neighbor has not been added to the priority queue,
                                // then set its parent node to null and its G value to "infinity".
                                if(!openQueue.contains(neighbor)) {
                                    neighbor.setParent(null);
                                    neighbor.setG(Double.POSITIVE_INFINITY);
                                    neighbor.setH(getManhattanDistance(neighbor, dest));
                                }

                                double oldG = neighbor.getG();
                                // Determine which of the two paths are the best option
                                computeBestPath(node, neighbor);
                                if(neighbor.getG() < oldG) {
                                    // If the neighbor is in the open queue, then remove it and
                                    // add it again, so that it can be sorted in the right place,
                                    // instead of having to sort the whole queue again.
                                    if(openQueue.contains(neighbor)) {
                                        openQueue.remove(neighbor);
                                    }
                                    openQueue.add(neighbor);
                                }
                            }
                        } // try
                        catch (IndexOutOfBoundsException e) {}
                    } // if(i != 0 || j != 0)
                } // j
            } // i
        }
        return null;
    }

    /**
     * Determines whether to set a neighbor's parent to the node or to the node's parent.
     *
     * @param node          the current node being visited in the path search
     * @param neighbor      a neighbor of node
     */
    protected void computeBestPath(GridNode node, GridNode neighbor) {
        int nodeNeighborDistance = distanceBetweenNodes(node, neighbor);
        if(node.getG() + nodeNeighborDistance < neighbor.getG()) {
            neighbor.setParent(node);
            neighbor.setG(node.getG() + nodeNeighborDistance);
        }
    }

    /**
     * Returns the approximate distance between two nodes in the search area.
     *
     * @param a                 first node
     * @param b                 second node
     * @return                  an int representing the approximate distance between the two nodes
     */
    protected int distanceBetweenNodes(GridNode a, GridNode b) {
        if(a.getLocation().getX() != b.getLocation().getX() &&
                a.getLocation().getY() != b.getLocation().getY()) {
            return 14;
        }
        else {
            return 10;
        }
    }

    /**
     * Returns the distance between two nodes using the Manhattan method of adding up the
     * x distance and the y distance together.
     *
     * @param a                 first node
     * @param b                 second node
     * @return                  an int representing the Manhattan distance between the two nodes
     */
    protected int getManhattanDistance(GridNode a, GridNode b) {
        int xDelta = b.getLocation().getX() - a.getLocation().getX();
        int yDelta = b.getLocation().getY() - a.getLocation().getY();
        // Calculate absolute value because distance is always positive.
        if(xDelta < 0) {
            xDelta = -xDelta;
        }
        if(yDelta < 0) {
            yDelta = -yDelta;
        }
        // Multiply by 10, because adjacent nodes are a distance of 10 apart.
        return 10 * (xDelta + yDelta);
    }

    /**
     * Reconstructs the path by traversing from the destination node back through each parent
     * node until the start node is reached.
     *
     * @param start         the node used as the starting point for the path search
     * @param dest          the destination node that ends the path search
     * @return              Returns a List of GridNodes, which are the path with the start node
     *                      at the head of the List and the dest node at the tail
     */
    protected List<GridNode> reconstructPath(GridNode start, GridNode dest) {
        List<GridNode> path = new ArrayList<GridNode>();
        GridNode node = dest;

        while(true) {
            path.add(0, node);

            if(node == start) {
                break;
            }

            node = (GridNode) node.getParent();
        }
        return path;
    }

    /**
     * Prints the given search area in an easy-to-view format.
     *
     * @param searchArea a 2D list holding all of the GridNodes that describe the space
     */
    public void printSearchArea(List<List<GridNode>> searchArea) {
        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                if(searchArea.get(i).get(j).isTraversable()) {
                    System.out.print('O');
                }
                else {
                    System.out.print('X');
                }
            }
            System.out.println();
        }
    }

    /**
     * Prints the search area along with the given path in an easy-to-view format.
     *
     * @param searchArea a 2D list holding all of the GridNodes that describe the space
     * @param path the path that is printed over the search area
     */
    public void printPath(List<List<GridNode>> searchArea, List<GridNode> path) {
        if(path == null) {
            System.out.println("No path.");
        }
        else {
            char[][] printedPath = new char[searchArea.size()][searchArea.get(0).size()];
            for(int i = 0; i < searchArea.size(); i++) {
                for(int j = 0; j < searchArea.get(0).size(); j++) {
                    if(searchArea.get(i).get(j).isTraversable()) {
                        printedPath[i][j] = 'O';
                    }
                    else {
                        printedPath[i][j] = 'X';
                    }
                }
            }

            for(int i = 0; i < path.size(); i++) {
                if(i == 0) {
                    printedPath[path.get(i).getLocation().getY()][path.get(i).getLocation().getX()] = 'S';
                }
                else if(i == path.size() - 1) {
                    printedPath[path.get(i).getLocation().getY()][path.get(i).getLocation().getX()] = 'E';
                }
                else {
                    printedPath[path.get(i).getLocation().getY()][path.get(i).getLocation().getX()] = 'P';
                }
            }

            for(int i = 0; i < printedPath.length; i++) {
                for (int j = 0; j < printedPath[0].length; j++) {
                    System.out.print(printedPath[i][j]);
                }
                System.out.println();
            }
        }
    }
}
