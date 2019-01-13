package bc19;

public class Church extends RobotType {

    private int[][] fullMap;
    public Church(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        robot.log(String.valueOf(fullMap));
    }

    @Override
    public Action turn() {
        Action action = null;
        robot.log("INSIDE CHURCH TURN METHOD");

        // check if enemies have been spotted
        int[] enemyRobotLoc = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobotLoc = new int[2];
                enemyRobotLoc[0] = visibleRobot.x;
                enemyRobotLoc[1] = visibleRobot.y;
            }
        }

        if (enemyRobotLoc != null) {
            // spam crusaders
            if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {
                int[] goalDir = Util.getDir(robot.me.x, robot.me.y, enemyRobotLoc[0], enemyRobotLoc[1]);
                action = buildUnit(robot, Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                if (action == null)
                    robot.log("COULDN'T BUILD CRUSADER");
            }
        }

        return action;
    }

    private Action buildUnit(BCAbstractRobot robot, int unit, int dx, int dy) {
        int x = robot.me.x + dx;
        int y = robot.me.y + dy;

        //robot.log("x is " + x);
        //robot.log("y is " + y);

        // check if that specific tile is occupied
        int[][] visibleMap = robot.getVisibleRobotMap();
        if (visibleMap[y][x] != 0) {
            return null;
        } else if (x < 0 || y < 0) {
            return null;
        } else if (x > fullMap.length || y > fullMap.length) {
            return null;
        } else {
            robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
            return robot.buildUnit(unit, dx, dy);
        }

    }
}