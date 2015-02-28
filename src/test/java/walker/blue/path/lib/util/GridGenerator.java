package walker.blue.path.lib.util;

import java.util.ArrayList;
import java.util.List;

import walker.blue.path.lib.GridNode;

/**
 * Util Class which generates a grid which can be used in testing
 */
public class GridGenerator {

    /**
     * Private constructor
     */
    private GridGenerator() {}

    /**
     * Generates a 2D grid using the values in the boolean array
     *
     * @param bools boolean vales indicating whether each node is walkable
     * @return generated grid
     */
    public static List<List<GridNode>> gen2D(final boolean[][] bools) {
        final List<List<GridNode>> generatedGrid = new ArrayList<>();
        int y = 0;
        for (boolean[] xRow : bools) {
            List<GridNode> currentXRow = new ArrayList<>();
            int x = 0;
            for (boolean bool : xRow) {
                currentXRow.add(new GridNode(x, y, 0, bool));
                x++;
            }
            generatedGrid.add(currentXRow);
            y++;
        }
        return generatedGrid;
    }

    /**
     * TODO implement this
     */
    public static List<List<GridNode>> gen3D() {
        return null;
    }
}