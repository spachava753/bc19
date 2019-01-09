package bc19;

import java.util.List;

/*
 *
 * The current pathfinder is taking longer than 200 seconds to process (I think), so we cant use it. At all. Need to find a more time efficient algorithm.
 *
 */
public class Pilgrim extends RobotType {

    private static final int[][] choices = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};
    private List<Node> nodes;
    private int numOfTurnsToStockpile = 950;

    public Pilgrim(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public Action turn() {
        Action action = null;

        /*
        if(nodes == null && robot.me.turn > numOfTurnsToStockpile){
            boolean[][] map = robot.getPassableMap();
            PathFinder pf = new PathFinder(map, new Node(map.length - 1, map[0].length - 1));
            nodes = pf.compute(new Node(0, 0));
        }
        */

        Random random = new Random();
        int[] choice = choices[random.nextInt(choices.length)];
        action = robot.move(choice[0], choice[1]);

        //action = robot.move(nodes.get(0).x, nodes.get(0).y);
        //nodes.remove(0);
        return action;
    }
}