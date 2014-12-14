import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.lang.Math;


public class ThetaStar extends AbstractPathfinder<GridNode, List<List<GridNode>>> {
    /**
     * Class constructor.
     */
    public ThetaStar() {
        
    }
    
    /**
     * Returns a List representing the path between two nodes in a two-dimensional search space.
     * @param searchArea the 2D collection of nodes that comprises the
     *                   traversal area in the form of a List of Lists
     * @param start      the start node for the path search and must inherit from Node
     * @param dest       the destination node for the path search and must inherit from Node
     * @return           the List of nodes in the path starting from the start node and
     *                   ending with the dest node
     * @see              java.util.List
     * @see              java.util.HashSet
     * @see              java.util.PriorityQueue
     * @see              #reconstructPath(GridNode, GridNode)
     * @see              #computeBestPath(GridNode, GridNode, java.util.List)
     */
    public List<GridNode> findPath(List<List<GridNode>> searchArea, GridNode start, GridNode dest) {
        HashSet<GridNode> closedSet = new HashSet<GridNode>();
        PriorityQueue<GridNode> openQueue = new PriorityQueue<GridNode>();
        
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
                                searchArea.get(node.index()/searchArea.get(0).size() + i).
                                           get(node.index()%searchArea.get(0).size() + j);
                             
                            // If the neighbor can be walked through and has not been visited
                            // directly, then check to see if the neighbor's values can be updated.
                            if(!closedSet.contains(neighbor) && neighbor.walkable()) {
                                // If the neighbor has not been added to the priority queue,
                                // then set its parent node to null and its G value to "infinity".
                                if(!openQueue.contains(neighbor)) {
                                    neighbor.setParent(null);
                                    neighbor.setG(Double.POSITIVE_INFINITY);
                                }

                                double oldG = neighbor.g();
                                // Determine which of the two paths are the best option
                                computeBestPath(node, neighbor, searchArea);
                                if(neighbor.g() < oldG) {
                                    // Set the h value for the neighbor being added to the
                                    // open queue. NOTE: Do not need to compute h value for
                                    // every node, just the ones added to the open queue.
                                    if(neighbor.h() == 0) {
                                        neighbor.setH(getManhattanDistance(neighbor, dest, searchArea.get(0).size()));
                                    }
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
     * @param node          the current node being visited in the path search
     * @param neighbor      a neighbor of node
     * @param searchArea    the 2D collection of nodes that comprises the
     *                      traversal area in the form of a List of Lists
     * @return              void
     * @see                 java.util.List
     * @see                 #lineOfSight(java.util.List, GridNode, GridNode)
     * @see                 #distanceBetweenNodes(GridNode, GridNode, int)
     */
    private void computeBestPath(GridNode node, GridNode neighbor, List<List<GridNode>> searchArea) {
        if(node.parent() != null && lineOfSight(searchArea, (GridNode)node.parent(), neighbor)) {
            int parentNeighborDistance = distanceBetweenNodes((GridNode)node.parent(), neighbor,
                    searchArea.get(0).size());

            if(node.parent().g() + parentNeighborDistance < neighbor.g()) {
                neighbor.setParent(node.parent());
                neighbor.setG(node.parent().g() + parentNeighborDistance);
            }
        }
        else {
            int nodeNeighborDistance = distanceBetweenNodes(node, neighbor, searchArea.get(0).size());
            if(node.g() + nodeNeighborDistance < neighbor.g()) {
                neighbor.setParent(node);
                neighbor.setG(node.g() + nodeNeighborDistance);
            }
        }
    }

    /**
     * Returns the approximate distance between two nodes in the search area.
     * @param a                 first node
     * @param b                 second node
     * @param searchAreaNumCols the number of columns in the search area used to convert the index
     *                          held in each node to an x, y coordinate within the grid
     * @return                  an int representing the approximate distance between the two nodes
     * @see                     java.lang.Math
     */
    private int distanceBetweenNodes(GridNode a, GridNode b, int searchAreaNumCols) {
        //
        int xDelta = b.index()%searchAreaNumCols - a.index()%searchAreaNumCols;
        int yDelta = b.index()/searchAreaNumCols - a.index()/searchAreaNumCols;
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
     * Returns the distance between two nodes using the Manhattan method of adding up the
     * x distance and the y distance together.
     * @param a                 first node
     * @param b                 second node
     * @param searchAreaNumCols the number of columns in the search area used to convert the index
     *                          held in each node to an x, y coordinate within the grid
     * @return                  an int representing the Manhattan distance between the two nodes
     */
    private int getManhattanDistance(GridNode a, GridNode b, int searchAreaNumCols) {
        int xDelta = b.index()%searchAreaNumCols - a.index()%searchAreaNumCols;
        int yDelta = b.index()/searchAreaNumCols - a.index()/searchAreaNumCols;
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
     * Returns true if the two nodes are within line of sight of one another, false otherwise.
     * @param searchArea    the 2D collection of nodes that comprises the
     *                      traversal area in the form of a List of Lists
     * @param a             first node
     * @param b             second node
     *
     * @return              Returns a boolean for whether or not the two nodes are in line of sight
     *                      of one another.
     */
    private boolean lineOfSight(List<List<GridNode>> searchArea, GridNode a, GridNode b) {
        int xA = a.index()%searchArea.get(0).size();
        int yA = a.index()/searchArea.get(0).size();
        int xB = b.index()%searchArea.get(0).size();
        int yB = b.index()/searchArea.get(0).size();

        int rise = yB - yA;
        int run = xB - xA;

        if(run == 0) {
            if(yB < yA) {
                int temp = yB;
                yB = yA;
                yA = yB;
            }
            for(int y = yA; y < yB + 1; y++) {
                if(!searchArea.get(y).get(xA).walkable()) {
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
                    if(!searchArea.get(y).get(x).walkable()) {
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
                    if(!searchArea.get(y).get(x).walkable()) {
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
     * Reconstructs the path by traversing from the destination node back through each parent
     * node until the start node is reached.
     * @param start         the node used as the starting point for the path search
     * @param dest          the destination node that ends the path search
     * @return              Returns a List of GridNodes, which are the path with the start node
     *                      at the head of the List and the dest node at the tail
     */
    private List<GridNode> reconstructPath(GridNode start, GridNode dest) {
        List<GridNode> path = new ArrayList<GridNode>();
        GridNode node = dest;
        
        while(true) {
            path.add(0, node);
            
            if(node == start) {
                break;
            }
            
            node = (GridNode) node.parent();
        }   
        return path;
    }

    public void printPath(List<GridNode> path, List<List<GridNode>> searchArea) {
        if(path == null) {
            System.out.println("No path.");
        }
        else {
            char[][] printedPath = new char[searchArea.size()][searchArea.get(0).size()];
            for(int i = 0; i < searchArea.size(); i++) {
                for(int j = 0; j < searchArea.get(0).size(); j++) {
                    if(searchArea.get(i).get(j).walkable()) {
                        printedPath[i][j] = 'O';
                    }
                    else {
                        printedPath[i][j] = 'X';
                    }
                }
            }

            for(int i = 0; i < path.size(); i++) {
                if(i == 0) {
                    printedPath[path.get(i).index()/searchArea.get(0).size()][path.get(i).index%searchArea.get(0).size()] = 'S';
                }
                else if(i == path.size() - 1) {
                    printedPath[path.get(i).index()/searchArea.get(0).size()][path.get(i).index%searchArea.get(0).size()] = 'E';
                }
                else {
                    printedPath[path.get(i).index()/searchArea.get(0).size()][path.get(i).index%searchArea.get(0).size()] = 'P';
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

    public void printSearchArea(List<List<GridNode>> searchArea) {
        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                if(searchArea.get(i).get(j).walkable()) {
                    System.out.print('O');
                }
                else {
                    System.out.print('X');
                }
            }
            System.out.println();
        }
    }

    public void printAllLOSNodeCombinations(List<List<GridNode>> searchArea) {
        int count = 0;
        // Go through every line of sight node combination.
        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                for(int k = 0; k < searchArea.size(); k++) {
                    for(int w = 0; w < searchArea.get(0).size(); w++) {
                        System.out.print(count + ": " + "1st: " + searchArea.get(i).get(j).index() + ", 2nd: " + searchArea.get(k).get(w).index() + " ");
                        System.out.println(lineOfSight(searchArea, searchArea.get(i).get(j), searchArea.get(k).get(w)));
                        count++;
                    }
                }
            }
        }
    }

    public List<List<String>> getVisibilityGraph(List<List<GridNode>> searchArea, GridNode node) {
        List<List<String>> visibilityGraph = new ArrayList<List<String>>();
        for(int i = 0; i < searchArea.size(); i++) {
            visibilityGraph.add(new ArrayList<String>());
        }

        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                if(i == node.index()/searchArea.get(0).size() && j == node.index()%searchArea.get(0).size()) {
                    visibilityGraph.get(i).add(j, "S");
                }
                else if(!searchArea.get(i).get(j).walkable()) {
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

    public static void main(String[] args) {
        GridNode n0 = new GridNode(0, true);
        GridNode n1 = new GridNode(1, true);
        GridNode n2 = new GridNode(2, true);
        GridNode n3 = new GridNode(3, true);
        GridNode n4 = new GridNode(4, false);
        GridNode n5 = new GridNode(5, false);
        GridNode n6 = new GridNode(6, false);
        GridNode n7 = new GridNode(7, true);
        GridNode n8 = new GridNode(8, true);
        GridNode n9 = new GridNode(9, true);
        GridNode n10 = new GridNode(10, false);
        GridNode n11 = new GridNode(11, true);
        GridNode n12 = new GridNode(12, true);
        GridNode n13 = new GridNode(13, true);
        GridNode n14 = new GridNode(14, true);
        GridNode n15 = new GridNode(15, true);
        GridNode n16 = new GridNode(16, true);
        GridNode n17 = new GridNode(17, true);
        GridNode n18 = new GridNode(18, true);
        GridNode n19 = new GridNode(19, false);
        GridNode n20 = new GridNode(20, false);
        GridNode n21 = new GridNode(21, false);
        GridNode n22 = new GridNode(22, false);
        GridNode n23 = new GridNode(23, true);
        GridNode n24 = new GridNode(24, true);
        GridNode n25 = new GridNode(25, true);
        GridNode n26 = new GridNode(26, false);
        GridNode n27 = new GridNode(27, true);
        GridNode n28 = new GridNode(28, true);
        GridNode n29 = new GridNode(29, true);
        GridNode n30 = new GridNode(30, true);
        GridNode n31 = new GridNode(31, true);
        GridNode n32 = new GridNode(32, true);
        GridNode n33 = new GridNode(33, true);
        GridNode n34 = new GridNode(34, true);
        GridNode n35 = new GridNode(35, false);
        GridNode n36 = new GridNode(36, false);
        GridNode n37 = new GridNode(37, false);
        GridNode n38 = new GridNode(38, false);
        GridNode n39 = new GridNode(39, true);
        GridNode n40 = new GridNode(40, true);
        GridNode n41 = new GridNode(41, true);
        GridNode n42 = new GridNode(42, false);
        GridNode n43 = new GridNode(43, true);
        GridNode n44 = new GridNode(44, true);
        GridNode n45 = new GridNode(45, true);
        GridNode n46 = new GridNode(46, true);
        GridNode n47 = new GridNode(47, true);
        GridNode n48 = new GridNode(48, true);
        List<GridNode> L0 = new ArrayList<GridNode>(7);
        List<GridNode> L1 = new ArrayList<GridNode>(7);
        List<GridNode> L2 = new ArrayList<GridNode>(7);
        List<GridNode> L3 = new ArrayList<GridNode>(7);
        List<GridNode> L4 = new ArrayList<GridNode>(7);
        List<GridNode> L5 = new ArrayList<GridNode>(7);
        List<GridNode> L6 = new ArrayList<GridNode>(7);

        L0.add(n0);  L0.add(n1);  L0.add(n2);  L0.add(n3);  L0.add(n4);  L0.add(n5);  L0.add(n6);
        L1.add(n7);  L1.add(n8);  L1.add(n9);  L1.add(n10); L1.add(n11); L1.add(n12); L1.add(n13);
        L2.add(n14); L2.add(n15); L2.add(n16);  L2.add(n17); L2.add(n18); L2.add(n19); L2.add(n20);
        L3.add(n21); L3.add(n22); L3.add(n23);  L3.add(n24); L3.add(n25); L3.add(n26); L3.add(n27);
        L4.add(n28); L4.add(n29); L4.add(n30);  L4.add(n31); L4.add(n32); L4.add(n33); L4.add(n34);
        L5.add(n35); L5.add(n36); L5.add(n37);  L5.add(n38); L5.add(n39); L5.add(n40); L5.add(n41);
        L6.add(n42); L6.add(n43); L6.add(n44);  L6.add(n45); L6.add(n46); L6.add(n47); L6.add(n48);

        List<List<GridNode>> searchArea = new ArrayList<List<GridNode>>(7);
        searchArea.add(L0);
        searchArea.add(L1);
        searchArea.add(L2);
        searchArea.add(L3);
        searchArea.add(L4);
        searchArea.add(L5);
        searchArea.add(L6);

        ThetaStar thetaStar = new ThetaStar();
        //List<GridNode> path = thetaStar.findPath(searchArea, n13, n43);
        //List<GridNode> path = thetaStar.findPath(searchArea, n19, n43);
        List<GridNode> path = thetaStar.findPath(searchArea, n13, n7);

        thetaStar.printPath(path, searchArea);

        System.out.println();

        List<List<String>> visibilityGraph = thetaStar.getVisibilityGraph(searchArea, n13);
        for(int i = 0; i < visibilityGraph.size(); i++) {
            for (int j = 0; j < visibilityGraph.get(0).size(); j++) {
                System.out.print(visibilityGraph.get(i).get(j));
            }
            System.out.println();
        }

        thetaStar.printAllLOSNodeCombinations(searchArea);

        //thetaStar.lineOfSight(n13, n7, searchArea);
        //thetaStar.lineOfSight(n13, n25, searchArea);
        thetaStar.lineOfSight(searchArea, n13, n17);
        //thetaStar.lineOfSight(searchArea, n20, n43);

    }
}
