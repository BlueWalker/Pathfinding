package walker.blue.path.lib.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import walker.blue.path.lib.GridGenerator;
import walker.blue.path.lib.GridNode;

/**
 * Unit tests for GridGenerator class
 */
public class GridGeneratorTest {

    @Test
    public void testGridGenerate() {
        List<List<GridNode>> expectedGrid = setup2DExpectedNode();
        char[][] charGrid = {{'O', 'X', 'O', 'X', 'O', 'X'},
                {'X', 'X', 'X', 'X', 'X', 'X'},
                {'O', 'O', 'O', 'O', 'O', 'O'},
                {'X', 'X', 'X', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'X', 'X', 'X'},
                {'O', 'O', 'X', 'X', 'O', 'O'}};
        List<List<GridNode>> grid = GridGenerator.gen2D(charGrid);
        Assert.assertEquals(expectedGrid, grid);
    }

    private List<List<GridNode>> setup2DExpectedNode() {
        List<List<GridNode>> expectedGrid = new ArrayList<>();
        List<GridNode> row0 = new ArrayList<>();
        List<GridNode> row1 = new ArrayList<>();
        List<GridNode> row2 = new ArrayList<>();
        List<GridNode> row3 = new ArrayList<>();
        List<GridNode> row4 = new ArrayList<>();
        List<GridNode> row5 = new ArrayList<>();
        // First Row
        row0.add(new GridNode(0, 0, 0, true));
        row0.add(new GridNode(1, 0, 0, false));
        row0.add(new GridNode(2, 0, 0, true));
        row0.add(new GridNode(3, 0, 0, false));
        row0.add(new GridNode(4, 0, 0, true));
        row0.add(new GridNode(5, 0, 0, false));
        expectedGrid.add(row0);
        // Second Row
        row1.add(new GridNode(0, 1, 0, false));
        row1.add(new GridNode(1, 1, 0, false));
        row1.add(new GridNode(2, 1, 0, false));
        row1.add(new GridNode(3, 1, 0, false));
        row1.add(new GridNode(4, 1, 0, false));
        row1.add(new GridNode(5, 1, 0, false));
        expectedGrid.add(row1);
        // Third Row
        row2.add(new GridNode(0, 2, 0, true));
        row2.add(new GridNode(1, 2, 0, true));
        row2.add(new GridNode(2, 2, 0, true));
        row2.add(new GridNode(3, 2, 0, true));
        row2.add(new GridNode(4, 2, 0, true));
        row2.add(new GridNode(5, 2, 0, true));
        expectedGrid.add(row2);
        // Fourth Row
        row3.add(new GridNode(0, 3, 0, false));
        row3.add(new GridNode(1, 3, 0, false));
        row3.add(new GridNode(2, 3, 0, false));
        row3.add(new GridNode(3, 3, 0, true));
        row3.add(new GridNode(4, 3, 0, true));
        row3.add(new GridNode(5, 3, 0, true));
        expectedGrid.add(row3);
        // Fifth Row
        row4.add(new GridNode(0, 4, 0, true));
        row4.add(new GridNode(1, 4, 0, true));
        row4.add(new GridNode(2, 4, 0, true));
        row4.add(new GridNode(3, 4, 0, false));
        row4.add(new GridNode(4, 4, 0, false));
        row4.add(new GridNode(5, 4, 0, false));
        expectedGrid.add(row4);
        // Sixth Row
        row5.add(new GridNode(0, 5, 0, true));
        row5.add(new GridNode(1, 5, 0, true));
        row5.add(new GridNode(2, 5, 0, false));
        row5.add(new GridNode(3, 5, 0, false));
        row5.add(new GridNode(4, 5, 0, true));
        row5.add(new GridNode(5, 5, 0, true));
        expectedGrid.add(row5);

        return expectedGrid;
    }
}