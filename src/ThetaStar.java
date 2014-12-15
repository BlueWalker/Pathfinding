import java.util.ArrayList;
import java.util.List;
import java.lang.Math;


public class ThetaStar extends GridAStar {
    /**
     * Class constructor.
     */
    public ThetaStar(List<List<GridNode>> searchArea) {
        super(searchArea);
    }

    /**
     * Determines whether to set a neighbor's parent to the node or to the node's parent.
     * @param node          the current node being visited in the path search
     * @param neighbor      a neighbor of node
     * @return              void
     * @see                 java.util.List
     * @see                 #lineOfSight(GridNode, GridNode)
     * @see                 #distanceBetweenNodes(GridNode, GridNode)
     */
    @Override
    protected void computeBestPath(GridNode node, GridNode neighbor) {
        if(node.parent() != null && lineOfSight((GridNode)node.parent(), neighbor)) {
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
    private boolean lineOfSight(GridNode a, GridNode b) {
        int xA = a.x();
        int yA = a.y();
        int xB = b.x();
        int yB = b.y();

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
     * Returns the approximate distance between two nodes in the search area.
     * @param a                 first node
     * @param b                 second node
     * @return                  an int representing the approximate distance between the two nodes
     * @see                     java.lang.Math
     */
    @Override
    protected int distanceBetweenNodes(GridNode a, GridNode b) {
        int xDelta = b.x() - a.x();
        int yDelta = b.y() - a.y();
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
                        System.out.print(count + ": " + "1st: (" + searchArea.get(i).get(j).x() +
                                ", " + searchArea.get(i).get(j).y() + "), 2nd: (" +
                                searchArea.get(k).get(w).x() + ", " + searchArea.get(k).get(w).y()
                                + ") ");
                        System.out.println(lineOfSight(searchArea.get(i).get(j), searchArea.get(k).get(w)));
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
                if(i == node.y() && j == node.x()) {
                    visibilityGraph.get(i).add(j, "S");
                }
                else if(!searchArea.get(i).get(j).walkable()) {
                    visibilityGraph.get(i).add(j, "X");
                }
                else if(lineOfSight(node, searchArea.get(i).get(j))) {
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

        ThetaStar thetaStar = new ThetaStar(searchArea);
        //List<GridNode> path = thetaStar.findPath(searchArea, n13, n43);
        //List<GridNode> path = thetaStar.findPath(searchArea, n19, n43);
        List<GridNode> path = thetaStar.findPath(n13, n7);

        thetaStar.printPath(path);
        System.out.println("AStar:");
        GridAStar aStar = new GridAStar(searchArea);
        List<GridNode> pathAStar = aStar.findPath(n13, n7);
        aStar.printPath(pathAStar);

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
        thetaStar.lineOfSight(n13, n17);
        //thetaStar.lineOfSight(searchArea, n20, n43);

    }
}
