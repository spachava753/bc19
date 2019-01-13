package bc19;

/*
 *
 * The current pathfinder is taking longer than 200 seconds to process (I think), so we cant use it. At all. Need to find a more time efficient algorithm.
 *
 */
public class Pilgrim extends RobotType {

    private int[][] fullMap;
    private int[] refinery;
    private boolean builtChurch = false;

    public Pilgrim(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team == robot.me.team) {
                if (visibleRobot.unit == Constants.CASTLE_UNIT || visibleRobot.unit == Constants.CHURCH_UNIT) {
                    refinery = new int[2];
                    refinery[0] = visibleRobot.x;
                    refinery[1] = visibleRobot.y;
                    break;
                }
            }
        }
    }

    @Override
    public Action turn() {
        Action action = null;

        if (robot.me.karbonite == PilgrimConstants.KARB_CARRYING_CAPACITY || robot.me.fuel == PilgrimConstants.FUEL_CARRYING_CAPACITY) {
            robot.log("FULL OF RESOURCES");
            if (refinery != null) {
                int dx = refinery[0] - robot.me.x;
                int dy = refinery[1] - robot.me.y;
                action = robot.give(dx, dy, robot.me.karbonite, robot.me.fuel);
            } else {
                robot.log("refinery is null");
            }
        } else if (fullMap[robot.me.y][robot.me.x] == Util.KARBONITE || fullMap[robot.me.y][robot.me.x] == Util.FUEL) {
            // each pilgrim that mines is responsible for building at least one church, to protect the resource

            if (!builtChurch && (robot.karbonite > ChurchConstants.KARB_CONSTRUCTION_COST && robot.fuel > ChurchConstants.FUEL_CONSTRUCTION_COST)) {
                // build a church somewhere
                for (int i = 0; i < 8 || action == null; i++) {
                    int[] randDir = Util.getRandomDir();
                    action = build(robot, Constants.CHURCH_UNIT, randDir[0], randDir[1]);
                }

                if (action != null)
                    builtChurch = true;
            } else {
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

    private Action build(BCAbstractRobot robot, int churchUnit, int dx, int dy) {
        // do some validations here

        // check if off the map
        if (robot.me.x + dx < 0 || robot.me.y < 0) {
            return null;
        } else {
            return robot.buildUnit(Constants.CHURCH_UNIT, dx, dy);
        }
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