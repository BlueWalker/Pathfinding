package walker.blue.path.lib.util;

import java.util.ArrayList;
import java.util.List;

import walker.blue.path.lib.floor.FloorConnector;
import walker.blue.path.lib.node.GridNode;

/**
 * Util Class which generates a grid which can be used in testing
 */
public class GridGenerator {

    /**
     * Private constructor
     */
    private GridGenerator() {}

    /**
     * Generates a 2D grid using the values in the char array
     *
     * @param grid char values indicating the type of grid node
     * @return generated grid
     */
    public static List<List<GridNode>> gen2D(final char[][] grid) {
        return gen2D(grid, 0);
    }

    /**
     * Generates a 2D grid using the values in the char array and
     * assigns the z coordinate of every node to the given floor number.
     *
     * @param grid char values indicating the type of grid node
     * @param floor the floor number of the 2D grid
     * @return generated grid
     */
    public static List<List<GridNode>> gen2D(final char[][] grid, int floor) {
        final List<List<GridNode>> generatedGrid = new ArrayList<>();
        int y = 0;
        for (char[] xRow : grid) {
            List<GridNode> currentXRow = new ArrayList<>();
            int x = 0;
            for (char type : xRow) {
                switch(type) {
                    case 'O':
                        currentXRow.add(new GridNode(x, y, floor, true));
                        break;
                    case 'X':
                        currentXRow.add(new GridNode(x, y, floor, false));
                        break;
                    case 'E':
                        currentXRow.add(new FloorConnector(x,
                                y,
                                floor,
                                false,
                                FloorConnector.Type.ELEVATOR)
                        );
                        break;
                    case 'S':
                        currentXRow.add(new FloorConnector(x,
                                y,
                                floor,
                                false,
                                FloorConnector.Type.STAIRS)
                        );
                        break;
                    default:
                        // Unknown node type, create untraversable node
                        currentXRow.add(new GridNode(x, y, floor, false));
                        break;
                }
                x++;
            }
            generatedGrid.add(currentXRow);
            y++;
        }
        return generatedGrid;
    }

    /**
     * Generates a 3D grid using the values in the char array.
     *
     * @param grid3D char values indicating the type of grid node to generate.
     * @return generated grid
     */
    public static List<List<List<GridNode>>> gen3D(final char[][][] grid3D) {
        final List<List<List<GridNode>>> generatedGrid3D = new ArrayList<>();
        int z = 0;
        for(char[][] floor : grid3D) {
            generatedGrid3D.add(gen2D(floor, z));
            z++;
        }
        return generatedGrid3D;
    }
}