package walker.blue.path.lib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * This class sequences together 2D paths generated from a GridAStar
 * object to form an approximate shortest 3D path specific to
 * building navigation.
 */
public class FloorSequencer {

    /**
     * Holds the pathfinder that calculates each floor's 2D path.
     */
    private GridAStar pathfinder;

    /**
     * A list of the 2D search areas that make up each floor.
     * The 0th index represents the bottom floor.
     */
    private List<List<List<GridNode>>> searchArea;

    /**
     * List of nodes that link each floor together.
     */
    private List<FloorConnector> floorConnectors;

    /**
     * Class constructor.
     */
    public FloorSequencer(GridAStar pathfinder,
                          List<List<List<GridNode>>> searchArea,
                          List<FloorConnector> floorConnectors) {
        this.pathfinder = pathfinder;
        this.searchArea = searchArea;
        this.floorConnectors = floorConnectors;
    }

    /**
     * Calculates the approximate shortest path from the start node to the dest node
     * even when both nodes are on different floors. Once every possible sequence of
     * floor connectors to move from the start node's floor to the dest node's floor
     * is calculated, the connectors in each sequence are used as start and end nodes
     * in the 2D path search, which uses the pathfinder member object. The shortest
     * path is assumed to be the multi-floor path that is calculated the quickest due
     * to fewer nodes having to be visited. Of course, this is dependent upon CPU load.
     * Technically, a longer path could be calculated quicker than a shorter path if
     * when the shorter path was being calculated, the CPU had a greater load from
     * external applications outside of the navigation system.
     * @param start the start node
     * @param dest the end node
     * @return the path from the start to dest nodes as a List of GridNodes
     */
    public List<GridNode> findPath(GridNode start, GridNode dest) {
        List<GridNode> path = null;
        
        // Check if the start and dest node are on the same floor. If so,
        // then simply call the pathfinder's findPath method to find the path on one floor.
        if(start.getLocation().getZ() == dest.getLocation().getZ()) {
            return pathfinder.findPath(searchArea.get(start.getLocation().getZ()), start, dest);
        }

        List<List<GridNode>> floorSequences =
                findFloorSequences(start.getLocation().getZ(), dest.getLocation().getZ());

        // Remove all sequences with an odd number of elements because that represents the extra
        // path that is the longer of another path that does not take the extra node. Also, add
        // the start and dest nodes to the beginning and end of each floor sequence.
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
                List<GridNode> path2D =
                        pathfinder.findPath(
                                searchArea.get(floorSequences.get(i).get(j).getLocation().getZ()),
                                floorSequences.get(i).get(j),
                                floorSequences.get(i).get(j+1));

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
                    path = new ArrayList<>(tempPath);
                }
            }
        }
        return path;
    }

    /**
     * Generates every sequence of floor connector nodes to take going
     * from the start floor to the destination floor using the floor
     * connectors as links between each floor. It performs a depth-first
     * search through the list of floor connectors (similar to
     * how an adjacency list is traversed) to find every possible way in
     * the floor connector nodes an be followed to go from the start floor
     * to the destination floor.
     * @param startFloor the start floor
     * @param destFloor the end floor
     * @return a list of every sequence of floor connectors that can
     *          be followed to navigate from the start floor to the dest floor
     */
    private List<List<GridNode>> findFloorSequences(int startFloor, int destFloor) {
        List<List<GridNode>> sequences = new ArrayList<>();

        // Go through the floor connections list pushing the nodes that are on the start floor
        // and finding all floor sequences from the pushed node
        for(FloorConnector connector : floorConnectors) {
            if(connector.getLocation().getZ() == startFloor) {
                List<FloorConnector> sequence = new ArrayList<>();
                Deque<FloorConnector> stack = new ArrayDeque<>();
                Deque<Integer> numNeighborsPushedStack = new ArrayDeque<>();
                Deque<Integer> numPoppedStack = new ArrayDeque<>();

                boolean[] visited = new boolean[floorConnectors.size()];

                visited[connector.getIndex()] = true;
                stack.push(connector);

                int numPopped = 0;
                // Use depth-first search to find all of the possible sequences to use
                // to get from the starting floor to the destination floor.
                while(!stack.isEmpty()) {
                    FloorConnector node = stack.pop();

                    numPopped++;

                    // Place the node at the end of the current list.
                    sequence.add(node);

                    // If the current node is on the destination floor, then store the current
                    // path sequence to the list of sequences and remove it from the current path
                    // sequence to backtrack. If its not on the destination floor, then push all
                    // of the unvisited neighbors to the stack.
                    if(node.getLocation().getZ() == destFloor) {
                        sequences.add(new ArrayList<GridNode>(sequence));
                        sequence.remove(sequence.size() - 1); // Remove last element
                        visited[node.getIndex()] = false;
                    }
                    else {
                        int numNeighborsPushedCount = 0;
                        for(FloorConnector neighbor : node.getConnections()) {
                            // If the neighbor hasn't been visited (i.e. isn't on the stack),
                            // then push it to the stack and increment numNeighborsPushedCount.
                            if(!visited[neighbor.getIndex()]) {
                                visited[neighbor.getIndex()] = true;
                                stack.push(neighbor);
                                numNeighborsPushedCount++;
                            }
                        }

                        // Store the information needed to backtrack properly, which is the number
                        // of neighbors pushed to the stack and the current number of popped nodes
                        // so far. Reset the number of popped nodes to start a new count.
                        numNeighborsPushedStack.push(numNeighborsPushedCount);
                        numPoppedStack.push(numPopped);
                        numPopped = 0;
                    }

                    // If all of the neighbors have been popped off the stack, then remove the
                    // node that pushed those neighbors from the path sequence, mark it as not
                    // visited, and load the previous number of popped nodes to the numPopped
                    // variable, so that sequence backtracking is possible.
                    if(!numNeighborsPushedStack.isEmpty() &&
                            numPopped == numNeighborsPushedStack.peek()) {
                        FloorConnector removedNode = sequence.remove(sequence.size() - 1);
                        visited[removedNode.getIndex()] = false;
                        numNeighborsPushedStack.pop();
                        numPopped = numPoppedStack.pop();
                    }
                }
            }
        }
        return sequences;
    }

    /**
     * Prints the given floor sequences in an easy-to-view format.
     * @param floorSequences list of node sequences
     */
    public void printFloorSequences( List<List<GridNode>> floorSequences) {
        if(floorSequences.isEmpty()) {
            System.out.println("Not possible to go from start floor to destination floor.");
        }
        else {
            for(int i = 0; i < floorSequences.size(); i++) {
                System.out.print("Sequence " + i + ": ");
                for(int j = 0; j < floorSequences.get(i).size(); j++) {
                    System.out.print("(" + floorSequences.get(i).get(j).getLocation().getX() + ", " +
                            floorSequences.get(i).get(j).getLocation().getY() + ", " +
                            floorSequences.get(i).get(j).getLocation().getZ() + ") -> ");
                }
                System.out.println();
            }
        }
    }

    /**
     * Prints the given path over top of the given 3D search area in an
     * easy-to-view format.
     * @param searchArea the 3D list of nodes representing the building layout
     * @param path the list of nodes that make up the path
     */
    public void printPath(List<List<List<GridNode>>> searchArea, List<GridNode> path) {
        if(path == null) {
            System.out.println("No path.");
        }
        else {
            char[][][] printedPath = new char[searchArea.size()][searchArea.get(0).size()][searchArea.get(0).get(0).size()];
            for(int i = 0; i < searchArea.size(); i++) {
                for(int j = 0; j < searchArea.get(0).size(); j++) {
                    for(int k = 0; k < searchArea.get(0).get(0).size(); k++) {
                        if(searchArea.get(i).get(j).get(k) instanceof FloorConnector) {
                            printedPath[i][j][k] = 'C';
                        }
                        else if(searchArea.get(i).get(j).get(k).isTraversable()) {
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
                    printedPath[path.get(i).getLocation().getZ()]
                            [path.get(i).getLocation().getY()]
                            [path.get(i).getLocation().getX()] =
                            'S';
                }
                else if(i == path.size() - 1) {
                    printedPath[path.get(i).getLocation().getZ()]
                            [path.get(i).getLocation().getY()]
                            [path.get(i).getLocation().getX()] =
                            'E';
                }
                else {
                    printedPath[path.get(i).getLocation().getZ()]
                            [path.get(i).getLocation().getY()]
                            [path.get(i).getLocation().getX()] =
                            'P';
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
