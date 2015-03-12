package walker.blue.path.lib;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for ThetaStar class
 */
public class ThetaStarTest {

    private ThetaStar thetaStar;

    @Before
    public void classSetup() {
        this.thetaStar = new ThetaStar();
    }

    @Test
    public void testFindPath2D() {
        char[][] searchAreaGrid = {{'O', 'O', 'O', 'O', 'X', 'X', 'X'},
                {'O', 'O', 'O', 'X', 'O', 'O', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'X', 'X'},
                {'X', 'X', 'O', 'O', 'O', 'X', 'O'},
                {'O', 'O', 'O', 'O', 'O', 'O', 'O'},
                {'X', 'X', 'X', 'X', 'O', 'O', 'O'},
                {'X', 'O', 'O', 'O', 'O', 'O', 'O'}};
        List<List<GridNode>> searchArea = GridGenerator.gen2D(searchAreaGrid);
        GridNode startNode = searchArea.get(2).get(5);
        GridNode endNode = searchArea.get(6).get(1);
        List<GridNode> expectedPath = new ArrayList<>();
        expectedPath.add(startNode);
        expectedPath.add(searchArea.get(4).get(3));
        expectedPath.add(searchArea.get(6).get(4));
        expectedPath.add(endNode);
        List<GridNode> resPath = thetaStar.findPath(searchArea, startNode, endNode);
        Assert.assertEquals(expectedPath, resPath);
    }
}