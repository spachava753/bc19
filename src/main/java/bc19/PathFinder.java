package bc19;

import java.util.LinkedList;
import java.util.List;

public class PathFinder extends AStar {
    private boolean[][] map;
    private Node goal;

    public PathFinder(boolean[][] map, Node node) {
        this.map = map;
        goal = node;
    }

    protected boolean isGoal(Node node) {
        return (node.x == goal.x) && (node.y == goal.x);
    }

    protected Double g(Node from, Node to) {

        if (from.x == to.x && from.y == to.y)
            return 0.0;

        if (map[to.y][to.x] == true)
            return 1.0;

        return Double.MAX_VALUE;
    }

    protected Double h(Node from, Node to) {
        return new Double(Math.abs(map[0].length - 1 - to.x) + Math.abs(map.length - 1 - to.y));
    }

    protected List<Node> generateSuccessors(Node node) {
        List<Node> ret = new LinkedList<Node>();
        int x = node.x;
        int y = node.y;
        if (y < map.length - 1 && map[y + 1][x] == true)
            ret.add(new Node(x, y + 1));

        if (x < map[0].length - 1 && map[y][x + 1] == true)
            ret.add(new Node(x + 1, y));

        return ret;
    }


    public static void main(String[] args) {
        boolean[][] map = new boolean[][]{
                {true, true, true, true, true, true, true, true, true},
                {false, false, true, false, false, false, false, false, true},
                {true, true, true, true, false, true, true, false, true},
                {true, true, true, true, true, true, true, false, false},
                {true, false, false, false, false, true, false, false, false},
                {true, true, true, true, false, true, true, true, true},
                {true, true, true, true, false, true, false, false, true},
                {true, true, true, true, false, true, false, false, true},
                {true, true, true, true, false, true, false, false, true},
                {true, true, true, true, false, true, false, false, false},
                {true, true, true, true, false, true, true, true, true},
        };
        PathFinder pf = new PathFinder(map, new Node(map.length - 1, map[0].length - 1));

        long begin = System.currentTimeMillis();

        List<Node> nodes = pf.compute(new Node(0, 0));

        long end = System.currentTimeMillis();


    }


}
