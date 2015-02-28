package walker.blue.path.lib;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import walker.blue.path.lib.util.GridGenerator;

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
        boolean[][] searchAreaBools = {{true, true, true, true, false, false, false},
                {true, true, true, false, true, true, true},
                {true, true, true, true, true, false, false},
                {false, false, true, true, true, false, true},
                {true, true, true, true, true, true, true},
                {false, false, false, false, true, true, true},
                {false, true, true, true, true, true, true}};
        List<List<GridNode>> searchArea = GridGenerator.gen2D(searchAreaBools);
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