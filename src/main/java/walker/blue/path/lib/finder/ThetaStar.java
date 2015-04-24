package walker.blue.path.lib.finder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.lang.Math;
import java.util.PriorityQueue;

import walker.blue.path.lib.node.GridNode;

/**
 * This class expands upon GridAStar by providing a means for generating
 * paths with fewer turns by incorporating a line of sight check during
 * execution so that as long as the previous node's parent is within
 * line of sight of the current node, the current node's parent is set to
 * the previous node's parent. If there is no line of sight, then the
 * current node's parent is set to the previous node, which introduces a
 * turn in the path.
 */
public class ThetaStar extends GridAStar {

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
                                computeBestPath(searchArea, node, neighbor);
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
     * @param searchArea    a 2D list holding all of the GridNodes that describe the space
     * @param node          the current node being visited in the path search
     * @param neighbor      a neighbor of node
     */
    protected void computeBestPath(List<List<GridNode>> searchArea,
                                   GridNode node,
                                   GridNode neighbor) {
        if(node.getParent() != null && lineOfSight(searchArea, (GridNode)node.getParent(), neighbor)) {
            int parentNeighborDistance = distanceBetweenNodes((GridNode)node.getParent(), neighbor);

            if(node.getParent().getG() + parentNeighborDistance < neighbor.getG()) {
                neighbor.setParent(node.getParent());
                neighbor.setG(node.getParent().getG() + parentNeighborDistance);
            }
        }
        else {
            int nodeNeighborDistance = distanceBetweenNodes(node, neighbor);
            if(node.getG() + nodeNeighborDistance < neighbor.getG()) {
                neighbor.setParent(node);
                neighbor.setG(node.getG() + nodeNeighborDistance);
            }
        }
    }

    /**
     * Returns true if the two nodes are within line of sight of one another, false otherwise.
     *
     * @param searchArea    a 2D list holding all of the GridNodes that describe the space
     * @param a             first node
     * @param b             second node
     *
     * @return              Returns a boolean for whether or not the two nodes are in line of sight
     *                      of one another.
     */
    private boolean lineOfSight(List<List<GridNode>> searchArea, GridNode a, GridNode b) {
        int xA = a.getLocation().getX();
        int yA = a.getLocation().getY();
        int xB = b.getLocation().getX();
        int yB = b.getLocation().getY();

        int rise = yB - yA;
        int run = xB - xA;

        if(run == 0) {
            if(yB < yA) {
                int temp = yB;
                yB = yA;
                yA = temp;
            }
            for(int y = yA; y < yB + 1; y++) {
                if(!searchArea.get(y).get(xA).isTraversable()) {
                    return false;
                }
            }
        }
        else {
            float slope = (float) rise/run;
            int adjust = 1;
            if(slope < 0) {
                adjust = -1;
            }
            int offset = 0;
            // For when run is greater than rise, else when rise is greater than run.
            if(slope <= 1 && slope >= -1) {
                int delta = Math.abs(rise) * 2;
                int threshold = Math.abs(run);
                int thresholdInc = Math.abs(run) * 2;
                int y = yA;
                // Used to swap endpoints so that the increment is always in the same direction.
                if(xB < xA) {
                    int temp = xB;
                    xB = xA;
                    xA = temp;
                    y = yB;
                }
                for(int x = xA; x < xB; x++) {
                    if(!searchArea.get(y).get(x).isTraversable()) {
                        return false;
                    }
                    offset += delta;
                    if(offset >= threshold) {
                        y += adjust;
                        threshold += thresholdInc;
                    }
                }
            }
            else {
                int delta = Math.abs(run) * 2;
                int threshold = Math.abs(rise);
                int thresholdInc = Math.abs(rise) * 2;
                int x = xA;
                if(yB < yA) {
                    int temp = yB;
                    yB = yA;
                    yA = temp;
                    x = xB;
                }
                for(int y = yA; y < yB + 1; y++) {
                    if(!searchArea.get(y).get(x).isTraversable()) {
                        return false;
                    }
                    offset += delta;
                    if(offset >= threshold) {
                        x += adjust;
                        threshold += thresholdInc;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the approximate distance between two nodes in the search area.
     *
     * @param a                 first node
     * @param b                 second node
     * @return                  an int representing the approximate distance between the two nodes
     */
    @Override
    protected int distanceBetweenNodes(GridNode a, GridNode b) {
        int xDelta = b.getLocation().getX() - a.getLocation().getX();
        int yDelta = b.getLocation().getY() - a.getLocation().getY();
        // Calculate absolute value because distance is always positive.
        if(xDelta < 0) {
            xDelta = -xDelta;
        }
        if(yDelta < 0) {
            yDelta = -yDelta;
        }
        return (int) (10 * Math.sqrt(xDelta + yDelta));
    }

    /**
     * Calculates the line of sight for every node to every other node and prints
     * out the results.
     *
     * @param searchArea a 2D list holding all of the GridNodes that describe the space
     */
    public void printAllLOSNodeCombinations(List<List<GridNode>> searchArea) {
        int count = 0;
        // Go through every line of sight node combination.
        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                for(int k = 0; k < searchArea.size(); k++) {
                    for(int w = 0; w < searchArea.get(0).size(); w++) {
                        System.out.print(
                                count + ": " +
                                        "1st: (" + searchArea.get(i).get(j).getLocation().getX() +
                                        ", " + searchArea.get(i).get(j).getLocation().getY() +
                                        "), 2nd: (" +searchArea.get(k).get(w).getLocation().getX() +
                                        ", " + searchArea.get(k).get(w).getLocation().getY()
                                + ") "
                        );
                        System.out.println(
                                lineOfSight(
                                        searchArea,
                                        searchArea.get(i).get(j),
                                        searchArea.get(k).get(w)
                                )
                        );
                        count++;
                    }
                }
            }
        }
    }

    /**
     * Checks if every node in the search area is within line of sight with the given node.
     *
     * @param searchArea    a 2D list holding all of the GridNodes that describe the space
     * @param node          the node used to
     * @return A 2D list of strings where "S" represents the given node,
     *                      "V" marks that the node is within line of sight of "S",
     *                      "X" marks a boundary where sight does not pass, and
     *                      " " marks nodes that are out of sight of "S"
     */
    public List<List<String>> getVisibilityGraph(List<List<GridNode>> searchArea, GridNode node) {
        List<List<String>> visibilityGraph = new ArrayList<List<String>>();
        for(int i = 0; i < searchArea.size(); i++) {
            visibilityGraph.add(new ArrayList<String>());
        }

        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                if(i == node.getLocation().getY() && j == node.getLocation().getX()) {
                    visibilityGraph.get(i).add(j, "S");
                }
                else if(!searchArea.get(i).get(j).isTraversable()) {
                    visibilityGraph.get(i).add(j, "X");
                }
                else if(lineOfSight(searchArea, node, searchArea.get(i).get(j))) {
                    visibilityGraph.get(i).add(j, "V");
                }
                else {
                    visibilityGraph.get(i).add(j, " ");
                }
            }
        }

        return visibilityGraph;
    }
}
