package bc19;

public class Church extends RobotType {

    private static final int[][] choices = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};

    private boolean verticalSymetry;
    private boolean initialized = false;
    private int[][] fullMap;

    public Church(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        robot.log(String.valueOf(fullMap));
    }

    private void initialize() {

        int mapSize = robot.getPassableMap().length;

        if (robot.me.team == 0) {
            // Red Team
            //robot.log("Castle on red team");
        } else {
            // Blue Team
            //robot.log("Castle on blue team");
        }

        //robot.log("My location is => x:"+robot.me.x + " y:"+robot.me.y);

        if (verticalSymetry) {
            //robot.log("Believe map is vertically symmetric");
            //robot.log("Believe enemy Castle is at position => x:"+(mapSize - 1 - robot.me.x)+" y:"+robot.me.y);
        } else {
            //robot.log("Believe map is horizontally symmetric");
            //robot.log("Believe enemy Castle is at position => x:"+robot.me.x+" y:"+(mapSize - 1 -robot.me.y));
        }


    }

    @Override
    public Action turn() {
        Action action = null;
        robot.log("INSIDE CHURCH TURN METHOD");

        if (!initialized) {
            initialize();
            initialized = true;
        }


        // spam crusaders

        if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {
            // number of times to retry building
            for (int i = 0; i < 8 || action == null; i++) {
                int[] randDir = Util.getRandomDir();
                action = buildUnit(robot, Constants.CRUSADER_UNIT, randDir[0], randDir[1]);
            }

            if (action == null)
                robot.log("COULDN'T BUILD CRUSADER");
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
        }
        else {
            robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
            return robot.buildUnit(unit, dx, dy);
        }

    }
}