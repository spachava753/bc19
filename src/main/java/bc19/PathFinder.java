package bc19;

import java.util.LinkedList;
import java.util.List;

public class PathFinder extends AStar<Node> {
    private int[][] map;
    private Node goal;
    private BCAbstractRobot robot;

    public PathFinder(int[][] map, Node goal, BCAbstractRobot robot) {
        this.map = map;
        this.goal = goal;
        this.robot = robot;
        //RobotUtil.log("Received map: " + map);
        //RobotUtil.log("Received goal: " + goal);
        //RobotUtil.log("Received robot: " + robot);
    }

    public Node getGoal() {
        return goal;
    }

    public void setGoal(Node goal) {
        this.goal = goal;
    }

    protected boolean isGoal(Node node) {
        return (node.x == goal.x) && (node.y == goal.y);
    }

    protected Double g(Node from, Node to) {

        //check to make sure there isn't a robot in the way
        /*int[][] visibleRobotsMap = robot.getVisibleRobotMap();
        if(visibleRobotsMap[to.y][to.x] != 0){
            return Double.MAX_VALUE;
        }*/

        if (from.x == to.x && from.y == to.y)
            return 0.0;

        if (map[to.y][to.x] == 1)
            return 1.0;

        return Double.MAX_VALUE;
    }

    protected Double h(Node from, Node to) {
        /* Use the Manhattan distance heuristic.  */
        //return new Double(Math.abs(map[0].length - 1 - to.x) + Math.abs(map.length - 1 - to.y));

        /* Use the Euclidean distance heuristic. (I think that is what it is called?) */
        return new Double(RobotUtil.findDistance(from.x, from.y, to.x, to.y));
    }

    protected List<Node> generateSuccessors(Node node) {
        List<Node> ret = new LinkedList<Node>();
        int x = node.x;
        int y = node.y;
        if (y < map.length - 1 && map[y + 1][x] == 1)
            ret.add(new Node(x, y + 1));

        if (y < map.length - 1 && x < map[0].length - 1 && map[y + 1][x + 1] == 1)
            ret.add(new Node(x + 1, y + 1));

        if (x < map[0].length - 1 && map[y][x + 1] == 1)
            ret.add(new Node(x + 1, y));

        if (x < map.length - 1 && y > 0 && map[y - 1][x + 1] == 1)
            ret.add(new Node(x + 1, y - 1));

        if (y > 0 && map[y - 1][x] == 1)
            ret.add(new Node(x, y - 1));

        if (y > 0 && x > 0 && map[y - 1][x - 1] == 1)
            ret.add(new Node(x - 1, y - 1));

        if (x > 0 && map[y][x - 1] == 1)
            ret.add(new Node(x - 1, y));

        if (x > 0 && y < map[0].length - 1 && map[y + 1][x - 1] == 1)
            ret.add(new Node(x - 1, y + 1));

        return ret;
    }


    /*
    public static void main(String[] args) {
        int[][] map = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 0, 1, 1, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1, 0, 0, 0},
                {1, 1, 1, 1, 0, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 1, 0, 0, 1},
                {1, 1, 1, 1, 0, 1, 0, 0, 1},
                {1, 1, 1, 1, 0, 1, 0, 0, 1},
                {1, 1, 1, 1, 0, 1, 0, 0, 0},
                {1, 1, 1, 1, 0, 1, 1, 1, 1},
        };
        PathFinder pf = new PathFinder(map, new Node(map.length, map[0].length), null);

        System.out.println("Find a path from the top left corner to the right bottom one.");

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++)
                System.out.print(map[i][j] + " ");
            System.out.println();
        }

        long begin = System.currentTimeMillis();

        List<Node> nodes = pf.compute(new Node(0, 0));

        long end = System.currentTimeMillis();


        System.out.println("Time = " + (end - begin) + " ms");
        System.out.println("Expanded = " + pf.getExpandedCounter());
        System.out.println("Cost = " + pf.getCost());

        if (nodes == null)
            System.out.println("No path");
        else {
            System.out.print("Path = ");
            for (Node n : nodes)
                System.out.print(n);
            System.out.println();
        }
    }
    */

}
