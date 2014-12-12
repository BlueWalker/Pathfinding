import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;


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
     * @see              List    
     */
    public List<GridNode> findPath(List<List<GridNode>> searchArea, GridNode start, GridNode dest) {
        HashSet<GridNode> closedList = new HashSet<GridNode>();
        PriorityQueue<GridNode> openList = new PriorityQueue<GridNode>();
        
        openList.add(start);
        
        // Run while the open list is not empty (if it is, then the destination was never found)
        // and while the open list does not contain the destination node (once it has the
        // destination node the path has been found).
        while(!openList.isEmpty()) {
            
            GridNode node = openList.remove();
            
            // If the destination node has been reached, then return the reconstructed path.
            if(node == dest) {
                return reconstructPath(start, node);
            }
            
            closedList.add(node);
            
            // When diagonalCounter is even, then the neighbor is a diagonal neighbor
            int diagonalCounter = 0;
            boolean diagonalNode;
            
            // Find neighbors in the 3D array
            for(int i = -1; i < 2; i++) {
                for(int j = -1; j < 2; j++) {   
                    if(i != 0 || j != 0) {
                        try {
                            GridNode neighbor =
                                searchArea.get(node.index()/searchArea.get(0).size() + i).
                                           get(node.index()%searchArea.get(0).size() + j);
                            
                            diagonalNode = (diagonalCounter % 2 == 0);
                             
                            // If the neighbor has not been added to the list, then update it's
                            // parent, h, and g values. Otherwise, if it has been added, then check
                            // to see if taking the current path is a shorter option and update the
                            // parent, h, and g values if it is a better option.
                            if(closedList.contains(neighbor) || !neighbor.walkable()) {
                                ;// do nothing
                            }
                            else if(!openList.contains(neighbor)) {
                                // Update the parent, g, and h values of the neighbor nodes
                                neighbor.setParent(node);

                                neighbor.setG(diagonalNode ? node.g() + 14 : node.g() + 10);
                                 
                                // Use Manhattan method to set the h value of the neighbor
                                int searchAreaSize = searchArea.get(0).size();
                                neighbor.setH(10 * (dest.index()/searchAreaSize -
                                                    neighbor.index()/searchAreaSize +
                                                    dest.index()%searchAreaSize -
                                                    neighbor.index()%searchAreaSize));
                                
                                openList.add(neighbor);
                            }
                            else {
                                if(diagonalNode && (node.g() + 14) < neighbor.g()) {
                                    // If using the current path's neighbor g value is less than
                                    // the existing neighbor's g value, then change the parent to
                                    // the current node and update the g value.
                                    neighbor.setParent(node);
                                    
                                    neighbor.setG(node.g() + 14);
                                     
                                    // Must sort the priority queue because of the updated g value
                                    Arrays.sort(openList.toArray());
                                }
                                else if(node.g() + 10 < neighbor.g()) {
                                    neighbor.setParent(node);
                                         
                                    neighbor.setG(node.g() + 10);
                                     
                                    // Must sort the priority queue because of the updated g value
                                    Arrays.sort(openList.toArray());
                                }
                            }                                  
                        } // try
                        catch (IndexOutOfBoundsException e) {
                             
                        }               
                    } // if(i != 0 || j != 0)
                    diagonalCounter++;
                } // j
            } // i            
        }
        return null;
    }

    public boolean lineOfSight(GridNode node0, GridNode node1, List<List<GridNode>> searchArea) {
        int searchAreaSize = searchArea.get(0).size();
        int xNode0 = node0.index%searchAreaSize;
        int yNode0 = node0.index/searchAreaSize;
        int xNode1 = node1.index%searchAreaSize;
        int yNode1 = node1.index/searchAreaSize;

        int xDistance = xNode1 - xNode0;
        int yDistance = yNode1 - yNode0;

        int f = 0;
        int yIncrement;
        int xIncrement;

        // Determines the direction to increment the y moving from node0 to node1
        if(yDistance < 0) {
            yDistance = -yDistance;
            yIncrement = -1;
        }
        else {
            yIncrement = 1;
        }
        // Determines the direction to increment the x moving from node0 to node1
        if(xDistance < 0) {
            xDistance = -xDistance;
            xIncrement = -1;
        }
        else {
            xIncrement = 1;
        }

        int xIndex;
        int yIndex;
        if(xDistance >= yDistance) {
            while(xNode0 != xNode1) {
                f += yDistance;
                xIndex = xNode0+((xIncrement-1)/2);
                yIndex = yNode0+((yIncrement-1)/2);
                if(f >= xDistance) {
                    // Node checked is node0 unless node1 has either a smaller x index or y index than node0.
                    if(xIndex >= 0 && yIndex >= 0) {
                        if(!searchArea.get(yIndex).get(xIndex).walkable()) {
                            System.out.println("Blocked node: " + searchArea.get(yIndex).get(xIndex).index());
                            return false;
                        }
                    }
                    else {
                        System.out.println("1-1: Out of bounds returned false for [" + yIndex + "][" + xIndex + "]");
                        return false;
                    }
                    yNode0 += yIncrement;
                    f -= xDistance;
                }
                xIndex = xNode0+((xIncrement-1)/2);
                yIndex = yNode0+((yIncrement-1)/2);
                if(f != 0) {
                    if(xIndex >= 0 && yIndex >= 0) {
                        if(!searchArea.get(yIndex).get(xIndex).walkable()) {
                            System.out.println("Blocked node: " + searchArea.get(yIndex).get(xIndex).index());
                            return false;
                        }
                    }
                    else {
                        System.out.println("1-2: Out of bounds returned false for [" + yIndex + "][" + xIndex + "]");
                        return false;
                    }
                }
                if(yDistance == 0) {
                    if(!searchArea.get(yNode0).get(xIndex).walkable()) {
                        if(xIndex >= 0 && yNode0 > 0) {
                            if(!searchArea.get(yNode0 - 1).get(xIndex).walkable()) {
                                System.out.println("Blocked nodes: " + searchArea.get(yNode0).get(xIndex).index() + ", " + searchArea.get(yNode0 - 1).get(xIndex).index());
                                return false;
                            }
                        }
                        else {
                            System.out.println("1-3: Out of bounds returned false for [" + (yNode0-1) + "][" + xIndex + "]");
                            return false;
                        }
                    }
                }
                xNode0 += xIncrement;
            }
        }
        else {
            while(yNode0 != yNode1) {
                f += xDistance;
                xIndex = xNode0+((xIncrement-1)/2);
                yIndex = yNode0+((yIncrement-1)/2);
                if(f >= yDistance) {
                    if(xIndex >= 0 && yIndex >= 0) {
                        if(!searchArea.get(yIndex).get(xIndex).walkable()) {
                            System.out.println("Blocked node: " + searchArea.get(yIndex).get(xIndex).index());
                            return false;
                        }
                    }
                    else {
                        System.out.println("2-1: Out of bounds returned false for [" + yIndex + "][" + xIndex + "]");
                        return false;
                    }
                    xNode0 += xIncrement;
                    f -= yDistance;
                }
                xIndex = xNode0+((xIncrement-1)/2);
                yIndex = yNode0+((yIncrement-1)/2);
                if(f != 0) {
                    if(xIndex >= 0 && yIndex >= 0) {
                        if(!searchArea.get(yIndex).get(xIndex).walkable()) {
                            System.out.println("Blocked node: " + searchArea.get(yIndex).get(xIndex).index());
                            return false;
                        }
                    }
                    else {
                        System.out.println("2-2: Out of bounds returned false for [" + yIndex + "][" + xIndex + "]");
                        return false;
                    }
                }
                // Checking just nodes that are between the two nodes in the vertical access
                if(xDistance == 0) {
                    if(!searchArea.get(yIndex).get(xNode0).walkable()) {
                        if(yIndex >= 0 && xNode0 > 0) {
                            if(!searchArea.get(yIndex).get(xNode0-1).walkable()) {
                                System.out.println("Blocked nodes: " + searchArea.get(yIndex).get(xNode0).index() + ", " + searchArea.get(yIndex).get(xNode0-1).index());
                                return false;
                            }
                        }
                        else {
                            System.out.println("2-3: Out of bounds returned false for [" + yIndex + "][" + (xNode0-1) + "]");
                            return false;
                        }
                    }
                }
                yNode0 += yIncrement;
            }
        }
        return true;
    }

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
                    printedPath[path.get(i).index/searchArea.get(0).size()][path.get(i).index%searchArea.get(0).size()] = 'S';
                }
                else if(i == path.size() - 1) {
                    printedPath[path.get(i).index/searchArea.get(0).size()][path.get(i).index%searchArea.get(0).size()] = 'E';
                }
                else {
                    printedPath[path.get(i).index/searchArea.get(0).size()][path.get(i).index%searchArea.get(0).size()] = 'P';
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
                        if(lineOfSight(searchArea.get(i).get(j), searchArea.get(k).get(w), searchArea)) {
                            System.out.println(true);
                        }
                        count++;
                    }
                }
            }
        }
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
        List<GridNode> L0 = new ArrayList<GridNode>(4);
        List<GridNode> L1 = new ArrayList<GridNode>(4);
        List<GridNode> L2 = new ArrayList<GridNode>(4);
        List<GridNode> L3 = new ArrayList<GridNode>(4);

        L0.add(n0);  L0.add(n1);  L0.add(n2);  L0.add(n3);
        L1.add(n4);  L1.add(n5);  L1.add(n6);  L1.add(n7);
        L2.add(n8);  L2.add(n9);  L2.add(n10); L2.add(n11);
        L3.add(n12); L3.add(n13); L3.add(n14); L3.add(n15);

        List<List<GridNode>> searchArea = new ArrayList<List<GridNode>>(4);
        searchArea.add(L0);
        searchArea.add(L1);
        searchArea.add(L2);
        searchArea.add(L3);

        ThetaStar thetaStar = new ThetaStar();
        List<GridNode> path = thetaStar.findPath(searchArea, n1, n9);
        thetaStar.printPath(path, searchArea);
        System.out.println();
        thetaStar.printSearchArea(searchArea);
        thetaStar.printAllLOSNodeCombinations(searchArea);
    }
}
