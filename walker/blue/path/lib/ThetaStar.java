package walker.blue.path.lib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.lang.Math;
import java.util.PriorityQueue;


public class ThetaStar extends GridAStar {
    /**
     * Class constructor.
     */
    public ThetaStar() {

    }

    /**
     * Returns a List representing the path between two nodes in a two-dimensional search space.
     * @param start      the start node for the path search and must inherit from Node
     * @param dest       the destination node for the path search and must inherit from Node
     * @return           the List of nodes in the path starting from the start node and
     *                   ending with the dest node
     * @see              java.util.List
     * @see              java.util.HashSet
     * @see              java.util.PriorityQueue
     * @see              #reconstructPath(GridNode, GridNode)
     * @see              #computeBestPath(GridNode, GridNode)
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
                                    searchArea.get(node.location().y() + i).
                                            get(node.location().x() + j);

                            // If the neighbor can be walked through and has not been visited
                            // directly, then check to see if the neighbor's values can be updated.
                            //if(!closedSet.contains(neighbor) && neighbor.walkable()) {
                            if(!closedSet.contains(neighbor) && neighbor.walkable()) {
                                // If the neighbor has not been added to the priority queue,
                                // then set its parent node to null and its G value to "infinity".
                                if(!openQueue.contains(neighbor)) {
                                    neighbor.setParent(null);
                                    neighbor.setG(Double.POSITIVE_INFINITY);
                                    neighbor.setH(getManhattanDistance(neighbor, dest));
                                }

                                double oldG = neighbor.g();
                                // Determine which of the two paths are the best option
                                computeBestPath(searchArea, node, neighbor);
                                if(neighbor.g() < oldG) {
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
     * @return              void
     * @see                 java.util.List
     * @see                 #lineOfSight(java.util.List, GridNode, GridNode)
     * @see                 #distanceBetweenNodes(GridNode, GridNode)
     */
    protected void computeBestPath(List<List<GridNode>> searchArea, GridNode node, GridNode neighbor) {
        if(node.parent() != null && lineOfSight(searchArea, (GridNode)node.parent(), neighbor)) {
            int parentNeighborDistance = distanceBetweenNodes((GridNode)node.parent(), neighbor);

            if(node.parent().g() + parentNeighborDistance < neighbor.g()) {
                neighbor.setParent(node.parent());
                neighbor.setG(node.parent().g() + parentNeighborDistance);
            }
        }
        else {
            int nodeNeighborDistance = distanceBetweenNodes(node, neighbor);
            if(node.g() + nodeNeighborDistance < neighbor.g()) {
                neighbor.setParent(node);
                neighbor.setG(node.g() + nodeNeighborDistance);
            }
        }
    }

    /**
     * Returns true if the two nodes are within line of sight of one another, false otherwise.
     * @param a             first node
     * @param b             second node
     *
     * @return              Returns a boolean for whether or not the two nodes are in line of sight
     *                      of one another.
     */
    private boolean lineOfSight(List<List<GridNode>> searchArea, GridNode a, GridNode b) {
        int xA = a.location().x();
        int yA = a.location().y();
        int xB = b.location().x();
        int yB = b.location().y();

        int rise = yB - yA;
        int run = xB - xA;

        if(run == 0) {
            if(yB < yA) {
                int temp = yB;
                yB = yA;
                yA = temp;
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
     * Returns the approximate distance between two nodes in the search area.
     * @param a                 first node
     * @param b                 second node
     * @return                  an int representing the approximate distance between the two nodes
     * @see                     java.lang.Math
     */
    @Override
    protected int distanceBetweenNodes(GridNode a, GridNode b) {
        int xDelta = b.location().x() - a.location().x();
        int yDelta = b.location().y() - a.location().y();
        // Calculate absolute value because distance is always positive.
        if(xDelta < 0) {
            xDelta = -xDelta;
        }
        if(yDelta < 0) {
            yDelta = -yDelta;
        }
        return (int) (10 * Math.sqrt(xDelta + yDelta));
    }

    public void printAllLOSNodeCombinations(List<List<GridNode>> searchArea) {
        int count = 0;
        // Go through every line of sight node combination.
        for(int i = 0; i < searchArea.size(); i++) {
            for(int j = 0; j < searchArea.get(0).size(); j++) {
                for(int k = 0; k < searchArea.size(); k++) {
                    for(int w = 0; w < searchArea.get(0).size(); w++) {
                        System.out.print(count + ": " + "1st: (" + searchArea.get(i).get(j).location().x() +
                                ", " + searchArea.get(i).get(j).location().y() + "), 2nd: (" +
                                searchArea.get(k).get(w).location().x() + ", " + searchArea.get(k).get(w).location().y()
                                + ") ");
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
                if(i == node.location().y() && j == node.location().x()) {
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
        GridNode n0 = new GridNode(0, 0, 0, true);
        GridNode n1 = new GridNode(1, 0, 0, true);
        GridNode n2 = new GridNode(2, 0, 0, true);
        GridNode n3 = new GridNode(3, 0, 0, true);
        GridNode n4 = new GridNode(4, 0, 0, false);
        GridNode n5 = new GridNode(5, 0, 0, false);
        GridNode n6 = new GridNode(6, 0, 0, false);
        GridNode n7 = new GridNode(0, 1, 0, true);
        GridNode n8 = new GridNode(1, 1, 0, true);
        GridNode n9 = new GridNode(2, 1, 0, true);
        GridNode n10 = new GridNode(3, 1, 0, false);
        GridNode n11 = new GridNode(4, 1, 0, true);
        GridNode n12 = new GridNode(5, 1, 0, true);
        GridNode n13 = new GridNode(6, 1, 0, true);
        GridNode n14 = new GridNode(0, 2, 0, true);
        GridNode n15 = new GridNode(1, 2, 0, true);
        GridNode n16 = new GridNode(2, 2, 0, true);
        GridNode n17 = new GridNode(3, 2, 0, true);
        GridNode n18 = new GridNode(4, 2, 0, true);
        GridNode n19 = new GridNode(5, 2, 0, false);
        GridNode n20 = new GridNode(6, 2, 0, false);
        GridNode n21 = new GridNode(0, 3, 0, false);
        GridNode n22 = new GridNode(1, 3, 0, false);
        GridNode n23 = new GridNode(2, 3, 0, true);
        GridNode n24 = new GridNode(3, 3, 0, true);
        GridNode n25 = new GridNode(4, 3, 0, true);
        GridNode n26 = new GridNode(5, 3, 0, false);
        GridNode n27 = new GridNode(6, 3, 0, true);
        GridNode n28 = new GridNode(0, 4, 0, true);
        GridNode n29 = new GridNode(1, 4, 0, true);
        GridNode n30 = new GridNode(2, 4, 0, true);
        GridNode n31 = new GridNode(3, 4, 0, true);
        GridNode n32 = new GridNode(4, 4, 0, true);
        GridNode n33 = new GridNode(5, 4, 0, true);
        GridNode n34 = new GridNode(6, 4, 0, true);
        GridNode n35 = new GridNode(0, 5, 0, false);
        GridNode n36 = new GridNode(1, 5, 0, false);
        GridNode n37 = new GridNode(2, 5, 0, false);
        GridNode n38 = new GridNode(3, 5, 0, false);
        GridNode n39 = new GridNode(4, 5, 0, true);
        GridNode n40 = new GridNode(5, 5, 0, true);
        GridNode n41 = new GridNode(6, 5, 0, true);
        GridNode n42 = new GridNode(0, 6, 0, false);
        GridNode n43 = new GridNode(1, 6, 0, true);
        GridNode n44 = new GridNode(2, 6, 0, true);
        GridNode n45 = new GridNode(3, 6, 0, true);
        GridNode n46 = new GridNode(4, 6, 0, true);
        GridNode n47 = new GridNode(5, 6, 0, true);
        GridNode n48 = new GridNode(6, 6, 0, true);
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

        thetaStar.printPath(searchArea, path);
        System.out.println("AStar:");
        GridAStar aStar = new GridAStar();
        List<GridNode> pathAStar = aStar.findPath(searchArea, n13, n7);
        aStar.printPath(searchArea, pathAStar);

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


        /////////////////////////////////////////////////////////////////////////////////////////
        // 3D search space
        /////////////////////////////////////////////////////////////////////////////////////////
        RectCoordinates[] c = {new RectCoordinates(2, 0, 0), new RectCoordinates(0, 2, 0), new RectCoordinates(0, 2, 1),
                               new RectCoordinates(2, 2, 1), new RectCoordinates(1, 1, 1), new RectCoordinates(0, 2, 2),
                               new RectCoordinates(2, 0, 2)};
        // Floor connection lists
        List<RectCoordinates> connections0 = new ArrayList<RectCoordinates>();
        connections0.add(c[1]);
        connections0.add(c[3]);

        List<RectCoordinates> connections1 = new ArrayList<RectCoordinates>();
        connections1.add(c[0]);
        connections1.add(c[2]);
        connections1.add(c[5]);

        List<RectCoordinates> connections2 = new ArrayList<RectCoordinates>();
        connections2.add(c[1]);
        connections2.add(c[3]);
        connections2.add(c[4]);
        connections2.add(c[5]);

        List<RectCoordinates> connections3 = new ArrayList<RectCoordinates>();
        connections3.add(c[0]);
        connections3.add(c[2]);
        connections3.add(c[4]);

        List<RectCoordinates> connections4 = new ArrayList<RectCoordinates>();
        connections4.add(c[2]);
        connections4.add(c[3]);
        connections4.add(c[6]);

        List<RectCoordinates> connections5 = new ArrayList<RectCoordinates>();
        connections5.add(c[1]);
        connections5.add(c[2]);
        connections5.add(c[6]);

        List<RectCoordinates> connections6 = new ArrayList<RectCoordinates>();
        connections6.add(c[4]);
        connections6.add(c[5]);

        GridNode node1 = new GridNode(0, 0, 0, true);
        GridNode node2 = new GridNode(1, 0, 0, true);
        GridNode node3 = new FloorConnectorNode(2, 0, 0, true, 0, connections0);
        GridNode node4 = new GridNode(0, 1, 0, true);
        GridNode node5 = new GridNode(1, 1, 0, true);
        GridNode node6 = new GridNode(2, 1, 0, true);
        GridNode node7 = new FloorConnectorNode(0, 2, 0, true, 1, connections1);
        GridNode node8 = new GridNode(1, 2, 0, true);
        GridNode node9 = new GridNode(2, 2, 0, true);
        GridNode node10 = new GridNode(0, 0, 1, true);
        GridNode node11 = new GridNode(1, 0, 1, true);
        GridNode node12 = new GridNode(2, 0, 1, true);
        GridNode node13 = new GridNode(0, 1, 1, true);
        GridNode node14 = new FloorConnectorNode(1, 1, 1, true, 4, connections4);
        GridNode node15 = new GridNode(2, 1, 1, true);
        GridNode node16 = new FloorConnectorNode(0, 2, 1, true, 2, connections2);
        GridNode node17 = new GridNode(1, 2, 1, true);
        GridNode node18 = new FloorConnectorNode(2, 2, 1, true, 3, connections3);
        GridNode node19 = new GridNode(0, 0, 2, true);
        GridNode node20 = new GridNode(1, 0, 2, true);
        GridNode node21 = new FloorConnectorNode(2, 0, 2, true, 6, connections6);
        GridNode node22 = new GridNode(0, 1, 2, true);
        GridNode node23 = new GridNode(1, 1, 2, true);
        GridNode node24 = new GridNode(2, 1, 2, true);
        GridNode node25 = new FloorConnectorNode(0, 2, 2, true, 5, connections5);
        GridNode node26 = new GridNode(1, 2, 2, true);
        GridNode node27 = new GridNode(2, 2, 2, true);

        List<GridNode> list1 = new ArrayList<GridNode>(3);
        List<GridNode> list2 = new ArrayList<GridNode>(3);
        List<GridNode> list3 = new ArrayList<GridNode>(3);
        List<GridNode> list4 = new ArrayList<GridNode>(3);
        List<GridNode> list5 = new ArrayList<GridNode>(3);
        List<GridNode> list6 = new ArrayList<GridNode>(3);
        List<GridNode> list7 = new ArrayList<GridNode>(3);
        List<GridNode> list8 = new ArrayList<GridNode>(3);
        List<GridNode> list9 = new ArrayList<GridNode>(3);

        // First floor
        list1.add(node1); list1.add(node2); list1.add(node3);
        list2.add(node4); list2.add(node5); list2.add(node6);
        list3.add(node7); list3.add(node8); list3.add(node9);
        // Second floor
        list4.add(node10); list4.add(node11); list4.add(node12);
        list5.add(node13); list5.add(node14); list5.add(node15);
        list6.add(node16); list6.add(node17); list6.add(node18);
        // Third floor
        list7.add(node19); list7.add(node20); list7.add(node21);
        list8.add(node22); list8.add(node23); list8.add(node24);
        list9.add(node25); list9.add(node26); list9.add(node27);

        List<List<GridNode>> firstFloor = new ArrayList<List<GridNode>>(3);
        List<List<GridNode>> secondFloor = new ArrayList<List<GridNode>>(3);
        List<List<GridNode>> thirdFloor = new ArrayList<List<GridNode>>(3);

        firstFloor.add(list1); firstFloor.add(list2); firstFloor.add(list3);
        secondFloor.add(list4); secondFloor.add(list5); secondFloor.add(list6);
        thirdFloor.add(list7); thirdFloor.add(list8); thirdFloor.add(list9);

        List<List<List<GridNode>>> searchSpace = new ArrayList<List<List<GridNode>>>(3);

        searchSpace.add(firstFloor); searchSpace.add(secondFloor); searchSpace.add(thirdFloor);

        FloorSequencer floorSequencer = new FloorSequencer(thetaStar, searchSpace, 10, 12);

        List<GridNode> multiFloorPath = floorSequencer.findPath(node1, node27);
        floorSequencer.printPath(searchSpace, multiFloorPath);

        thetaStar.printPath(searchSpace.get(0), thetaStar.findPath(searchSpace.get(0), node1, node7));
        
        floorSequencer.printPath(searchSpace, floorSequencer.findPath(node1, node8));
        
        RectCoordinates location = new RectCoordinates(1, 1, 1);
        
        GridNode grabbedNode = floorSequencer.getNode(location);
        
        if(grabbedNode == null) {
            System.out.println("NULL node");
        }
        else {
            System.out.println("x=" + grabbedNode.location().x() + " y=" + grabbedNode.location().y() + " z=" + grabbedNode.location().z());
        }
        
    }
}
