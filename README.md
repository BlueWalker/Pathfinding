Pathfinding
===

Java implementations of both A\* and Theta\* pathfinding algorithms.

Adding to your Java project
---

This library is availiable through JCenter. Simply add it to your dependencies.

```GRADLE
repositories {
    jcenter()
}

dependencies {
    compile 'blue.walker:Trilateration:0.1.+'
}

```

Usage
---
```JAVA
List<List<GridNode>> searchArea = ...;
// Start and End nodes must both be inside the searchArea and must be transversable
GridNode start = ...;
GridNode end = ...;

GridAstar aStarPathfinder = new GridAStar();
GridAstar thetaStarPathfinder = new GridAStar();

List<GridNode> aStarPath = aStarPathfinder.findPath(searchArea, start, end);
List<GridNode> thetaStarPath = thetaStarPathfinder.findPath(searchArea, start, end);

```
