package walker.blue.path.lib;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class FloorSequencer {
    /**
     * A list of the 2D search areas that make up each floor.
     * The 0th index represents the bottom floor.
     */
    private GridAStar pathfinder;

    /**
     *
     */
    private List<List<List<GridNode>>> searchArea;

    /**
     *
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

    public List<GridNode> findPath(GridNode start, GridNode dest) {
        List<GridNode> path = null;
        
        // Check if the start and dest node are on the same floor. If so,
        // then simply call the pathfinder's findPath method to find the path on one floor.
        if(start.location().z() == dest.location().z()) {
            return pathfinder.findPath(searchArea.get(start.location().z()), start, dest);
        }

        List<List<GridNode>> floorSequences =
                findFloorSequences(start.location().z(), dest.location().z());

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
                                searchArea.get(floorSequences.get(i).get(j).location().z()),
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

    private List<List<GridNode>> findFloorSequences(int startFloor, int destFloor) {
        List<List<GridNode>> sequences = new ArrayList<>();

        // Go through the floor connections list pushing the nodes that are on the start floor
        // and finding all floor sequences from the pushed node
        for(FloorConnector connector : floorConnectors) {
            if(connector.location().z() == startFloor) {
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
                    if(node.location().z() == destFloor) {
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
                        if(searchArea.get(i).get(j).get(k) instanceof FloorConnector) {
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
