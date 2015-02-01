package walker.blue.path.lib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by Josh on 12/14/2014.
 */
public class FloorSequencer {
    /**
     * A list of the 2D search areas that make up each floor.
     * The 0th index represents the bottom floor.
     */
    private GridAStar pathfinder;

    private List<List<List<GridNode>>> searchArea;

    /**
     * Holds the total number of floor connector nodes within the search area.
     */
    private int numConnectorNodes;
    
    private double nodeDistance;
    
    /**
     * This assumes that every floor has approximately the same height.
     */
    private double floorHeight;

    /**
     * Class constructor.
     */
    public FloorSequencer(GridAStar pathfinder, List<List<List<GridNode>>> searchArea, double nodeDistance, double floorHeight) {
        this.pathfinder = pathfinder;
        this.searchArea = searchArea;
        this.nodeDistance = nodeDistance;
        this.floorHeight = floorHeight;
        
        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(i).size(); j++) {
                for(int k = 0; k < searchArea.get(i).get(j).size(); k++) {
                    GridNode gridNode = searchArea.get(i).get(j).get(k);
                    if(gridNode instanceof FloorConnectorNode) {
                        numConnectorNodes++;
                    }
                }
            }
        }
    }

    public List<GridNode> findPath(GridNode start, GridNode dest) {
        List<GridNode> path = null;
        
        // Check if the start and dest node are on the same floor. If so, then simply call the pathfinder's
        // findPath method to find the path on one floor.
        if(start.location().z() == dest.location().z()) {
            return pathfinder.findPath(searchArea.get(start.location().z()), start, dest);
        }

        List<List<GridNode>> floorSequences = findFloorSequences(start.location().z(), dest.location().z());

        // Remove all sequences with an odd number of elements because that represents the extra
        // path that is the longer of another path that does not take the extra node. Also, add
        // the start and dest nodes to the beginning and end of each floor sequence
        for(int i = 0; i < floorSequences.size(); i++) {
            if(floorSequences.get(i).size()%2 != 0) {
                floorSequences.remove(i);
            }
            else {
                floorSequences.get(i).add(0, start);
                floorSequences.get(i).add(floorSequences.get(i).size(), dest);
            }
        }

        long shortestTime = (long)Double.POSITIVE_INFINITY;
        for(int i = 0; i < floorSequences.size(); i++) {
            boolean hasPath = true;
            List<GridNode> tempPath = new ArrayList<GridNode>();
            long startTime = System.nanoTime();

            for(int j = 0; j < floorSequences.get(i).size(); j += 2) {
                List<GridNode> path2D = pathfinder.findPath(searchArea.get(floorSequences.get(i).get(j).location().z()), floorSequences.get(i).get(j), floorSequences.get(i).get(j+1));

                if(path2D == null) {
                    hasPath = false;
                    break;
                }
                else {
                    tempPath.addAll(path2D);
                }
            }
            if(hasPath) {
                long endTime = System.nanoTime();
                long currentTime = endTime - startTime;
                if(currentTime < shortestTime) {
                    path = new ArrayList<GridNode>(tempPath);
                }
            }

        }

        return path;
    }

    private List<List<GridNode>> findFloorSequences(int startFloor, int destFloor) {
        List<List<GridNode>> sequences = new ArrayList<List<GridNode>>();

        // Iterate through the starting floor checking for FloorConnectorNodes to push
        // onto the stack.
        for(int i = 0; i < searchArea.get(startFloor).size(); i++) {
            for(int j = 0; j < searchArea.get(startFloor).get(i).size(); j++) {
                GridNode gridNode = searchArea.get(startFloor).get(i).get(j);

                // A floor connector has been found on the starting floor,
                // so push it to the stack.
                if(gridNode instanceof FloorConnectorNode) {
                    List<GridNode> sequence = new ArrayList<GridNode>();
                    Deque<FloorConnectorNode> stack = new ArrayDeque<FloorConnectorNode>();
                    Deque<Integer> numNeighborsPushedStack = new ArrayDeque<Integer>();
                    Deque<Integer> numPoppedStack = new ArrayDeque<Integer>();

                    boolean[] visited = new boolean[numConnectorNodes];

                    FloorConnectorNode connectorNode = (FloorConnectorNode)gridNode;
                    visited[connectorNode.index()] = true;
                    stack.push(connectorNode);

                    int numPopped = 0;
                    // Use depth-first search to find all of the possible sequences to use
                    // to get from the starting floor to the destination floor.
                    while(!stack.isEmpty()) {
                        FloorConnectorNode node = stack.pop();

                        numPopped++;

                        // Place the node at the end of the current list.
                        sequence.add(searchArea.get(node.location().z()).
                                get(node.location().y()).get(node.location().x()));

                        // If the current node is on the destination floor, then store the current path sequence
                        // to the list of sequences and remove it from the current path sequence to backtrack.
                        // If its not on the destination floor, then push all of the unvisited neighbors to
                        // the stack.
                        if(node.location().z() == destFloor) {
                            sequences.add(new ArrayList<GridNode>(sequence));
                            sequence.remove(sequence.size() - 1); // Remove last element
                            visited[node.index()] = false;
                        }
                        else {
                            int numNeighborsPushedCount = 0;
                            for(int n = 0; n < node.connections().size(); n++) {
                                RectCoordinates neighborLoc = node.connections().get(n);
                                FloorConnectorNode neighbor = (FloorConnectorNode)searchArea.get(neighborLoc.z()).get(neighborLoc.y()).get(neighborLoc.x());

                                // If the neighbor hasn't been visited (i.e. isn't on the stack), then push it
                                // to the stack and increment numNeighborsPushedCount.
                                if(!visited[neighbor.index()]) {
                                    visited[neighbor.index()] = true;
                                    stack.push(neighbor);
                                    numNeighborsPushedCount++;
                                }

                            }
                            // Store the information needed to backtrack properly, which is the number
                            // of neighbors pushed to the stack and the current number of popped nodes so far.
                            // Reset the number of popped nodes to start a new count.
                            numNeighborsPushedStack.push(numNeighborsPushedCount);
                            numPoppedStack.push(numPopped);
                            numPopped = 0;
                        }

                        // If all of the neighbors have been popped off the stack, then remove the node that
                        // pushed those neighbors from the path sequence, mark it as not visited, and load the
                        // previous number of popped nodes to the numPopped variable, so that sequence
                        // backtracking is possible.
                        if(!numNeighborsPushedStack.isEmpty() && numPopped == numNeighborsPushedStack.peek()) {
                            FloorConnectorNode removedNode = (FloorConnectorNode)sequence.remove(sequence.size() - 1);
                            visited[removedNode.index()] = false;
                            numNeighborsPushedStack.pop();
                            numPopped = numPoppedStack.pop();
                        }

                    }
                }
            }
        }

        return sequences;
    }
    
    /**
     * Returns the node closest to the given location. This is best used when each floor has approximately
     * the same height. If floors have significant differences in height, then use the other overloaded
     * method getNode(RectCoordinates, int)
     * @param location the coordinates used to find the node that matches the same approximate location
     * @return a GridNode that is closest to the location given. If none can be found, null is returned
     */
    public GridNode getNode(RectCoordinates location) {
        int xIndex = (int)(location.y() / nodeDistance);
        int yIndex = (int)(location.x() / nodeDistance);
        int zIndex = (int)(location.z() / floorHeight);
        if(xIndex >= searchArea.get(0).get(0).size() ||
                yIndex >= searchArea.get(0).size() ||
                zIndex >= searchArea.size()) {
            return null;
        }
        return searchArea.get(zIndex).get(yIndex).get(xIndex);
    }
    
    /**
     * Returns the node closest to the given location, but does not use the z value, instead using the 
     * given floorNumber to decide the z index in the 3D grid. This is best used when the heights of 
     * floors are very different or if the z value is not completely reliable. 
     * @param location the coordinates, specifically the x and y coordinates, used to find the node
     *        that matches the same approximate location
     * @param floorNumber the value used to index the 3D grid along the z aisle
     * @return a GridNode that is closest to the location in the x and y of location and the floorNumber in the z direction
     */
    public GridNode getNode(RectCoordinates location, int floorNumber) {
        int xIndex = (int)(location.y() / nodeDistance);
        int yIndex = (int)(location.x() / nodeDistance);
        if(xIndex >= searchArea.get(0).get(0).size() ||
                yIndex >= searchArea.get(0).size() ||
                floorNumber >= searchArea.size()) {
            return null;
        }
        return searchArea.get(floorNumber).get(yIndex).get(xIndex);
    }

    public void printFloorSequences( List<List<GridNode>> floorSequences) {
        if(floorSequences.isEmpty()) {
            System.out.println("Not possible to go from start floor to destination floor.");
        }
        else {
            for(int i = 0; i < floorSequences.size(); i++) {
                System.out.print("Sequence " + i + ": ");
                for(int j = 0; j < floorSequences.get(i).size(); j++) {
                    System.out.print("(" + floorSequences.get(i).get(j).location().x() + ", " +
                            floorSequences.get(i).get(j).location().y() + ", " +
                            floorSequences.get(i).get(j).location().z() + ") -> ");
                }
                System.out.println();
            }
        }
    }

    public void printPath(List<List<List<GridNode>>> searchArea, List<GridNode> path) {
        if(path == null) {
            System.out.println("No path.");
        }
        else {
            char[][][] printedPath = new char[searchArea.size()][searchArea.get(0).size()][searchArea.get(0).get(0).size()];
            for(int i = 0; i < searchArea.size(); i++) {
                for(int j = 0; j < searchArea.get(0).size(); j++) {
                    for(int k = 0; k < searchArea.get(0).get(0).size(); k++) {
                        if(searchArea.get(i).get(j).get(k) instanceof FloorConnectorNode) {
                            printedPath[i][j][k] = 'C';
                        }
                        else if(searchArea.get(i).get(j).get(k).walkable()) {
                            printedPath[i][j][k] = 'O';
                        }
                        else {
                            printedPath[i][j][k] = 'X';
                        }
                    }
                }
            }

            for(int i = 0; i < path.size(); i++) {
                if(i == 0) {
                    printedPath[path.get(i).location().z()][path.get(i).location().y()][path.get(i).location().x()] = 'S';
                }
                else if(i == path.size() - 1) {
                    printedPath[path.get(i).location().z()][path.get(i).location().y()][path.get(i).location().x()] = 'E';
                }
                else {
                    printedPath[path.get(i).location().z()][path.get(i).location().y()][path.get(i).location().x()] = 'P';
                }
            }

            for(int i = 0; i < printedPath.length; i++) {
                System.out.println("Floor " + i + ":");
                for (int j = 0; j < printedPath[0].length; j++) {
                    for(int k = 0; k < printedPath[0][0].length; k++) {
                        System.out.print(printedPath[i][j][k]);
                    }
                    System.out.println();
                }
                System.out.println();
                System.out.println();
            }
        }
    }
}
