package bc19;

/*
 *
 * The current pathfinder is taking longer than 200 seconds to process (I think), so we cant use it. At all. Need to find a more time efficient algorithm.
 *
 */
public class Pilgrim extends RobotType {

    private int[][] fullMap;
    private int[] refinery;
    private boolean refineryAvailable = false;

    public Pilgrim(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        //refinery = discoverRefineries();
    }

    @Override
    public Action turn() {
        Action action = null;

        // if we are full of resources, give it to a castle or church
        if (robot.me.karbonite == PilgrimConstants.KARB_CARRYING_CAPACITY || robot.me.fuel == PilgrimConstants.FUEL_CARRYING_CAPACITY) {
            robot.log("FULL OF RESOURCES");
            // each pilgrim that mines is responsible for building at least one church, to protect the resource
            if (!refineryAvailable && (robot.karbonite > ChurchConstants.KARB_CONSTRUCTION_COST && robot.fuel > ChurchConstants.FUEL_CONSTRUCTION_COST)) {
                // build a church somewhere
                int[] randDir = Util.getRandomDir();
                for (int i = 0; i < 8 || action == null; i++) {
                    randDir = Util.getRandomDir();
                    action = build(robot, Constants.CHURCH_UNIT, randDir[0], randDir[1]);
                }

                if (action != null) {
                    robot.log("BUILDING A NEW CHURCH");
                }
            }

            if (refinery != null && Math.floor(Util.findDistance(refinery[0], refinery[1], robot.me.x, robot.me.y)) == 1) {
                int dx = refinery[0] - robot.me.x;
                int dy = refinery[1] - robot.me.y;
                action = robot.give(dx, dy, robot.me.karbonite, robot.me.fuel);
            } else {
                robot.log("refinery is null");
                robot.log("DISCOVERING NEW REFINERIES");
                if(discoverRefineries() != null){
                    refinery = discoverRefineries();
                    refineryAvailable = true;
                }
            }
        // if we are on a deposit, build a church so we cash in our resources
        } else if (fullMap[robot.me.y][robot.me.x] == Util.KARBONITE || fullMap[robot.me.y][robot.me.x] == Util.FUEL) {

            if(refinery == null)
                refinery = discoverRefineries();
            else
                refineryAvailable = true;

            if(action == null) {
                robot.log("MINING RESOURCES");
                action = robot.mine();
            }
        } else {
            robot.log("MOVING RANDOMLY");
            int[] randDir = Util.getRandomDir();
            action = move(robot, randDir[0], randDir[1]);
        }

        return action;
    }

    private int[] discoverRefineries(){
        int[] refineryPos = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team == robot.me.team) {
                if ((visibleRobot.unit == Constants.CASTLE_UNIT || visibleRobot.unit == Constants.CHURCH_UNIT)
                        && Math.floor(Util.findDistance(robot.me.x, robot.me.y, visibleRobot.x, visibleRobot.y)) == 1) {
                    refineryPos = new int[2];
                    refineryPos[0] = visibleRobot.x;
                    refineryPos[1] = visibleRobot.y;
                    break;
                }
            }
        }

        return refineryPos;
    }

    private Action build(BCAbstractRobot robot, int churchUnit, int dx, int dy) {
        int x = robot.me.x + dx;
        int y = robot.me.y + dy;
        // do some validations here

        // check if off the map
        int[][] visibleMap = robot.getVisibleRobotMap();
        if (visibleMap[y][x] != 0) {
            return null;
        } else if (x < 0 || y < 0) {
            return null;
        }

        robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
        return robot.buildUnit(Constants.CHURCH_UNIT, dx, dy);
    }

    private Action move(BCAbstractRobot robot, int x, int y) {
        // do some validations here

        //check if going of  the map
        if (robot.me.x + x < 0 || robot.me.y + y < 0)
            return null;
        else {
            return robot.move(x, y);
        }
    }
}