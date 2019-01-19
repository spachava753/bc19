package bc19;

import java.util.List;

public class Castle extends RobotType {

    private int pilgrimsBuilt;
    private int crusadersBuilt;
    private int numOfDeposits;

    public Castle(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public void initialize() {
        super.initialize();
        numOfDeposits = getDeposits(getFullMap()).size()/2;
    }

    @Override
    public void initTakeTurn() {
        super.initTakeTurn();
        Log.i("PILGRIMS BUILT: " + pilgrimsBuilt);
        Log.i("NUM OF DEPOSITS: " + numOfDeposits);
    }

    @Override
    public Action takeTurn() {
        Action action = null;

        //prioritize building crusaders if there is an enemy within our vision radius
        Robot enemyRobot = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobot = visibleRobot;
                break;
            }
        }


        // check if a deposit is in one of our adjacent squares
        List<int[]> tileDir = RobotUtil.getAdjacentTilesWithDeposits(robot, getFullMap());


        // defensive measures
        if (enemyRobot != null) {
            //Log.i("FOUND AN ENEMY ROBOT");
            if (robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_FUEL) {
                int[] goalDir = RobotUtil.getDir(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
                action = build(Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                if (action == null) {
                    //Log.i("COULDN'T BUILD CRUSADER");
                }
            }
        } else if (!tileDir.isEmpty() && (tileDir.size() > pilgrimsBuilt)) {
            //Log.i("ONE OF THE ADJACENT TILES HAS A DEPOSIT");

            for (int[] direction : tileDir) {
                action = build(robot.SPECS.PILGRIM, direction[0], direction[1]);
                if (action != null) {
                    pilgrimsBuilt++;
                    break;
                }
            }
        } else if (numOfDeposits > pilgrimsBuilt && canBuildUnitWithResources(robot.SPECS.PILGRIM)) {
            // number of times to retry building
            Log.i("BUILDING NEW PILGRIM");
            action = tryAction(20, () -> {
                int[] randDir = RobotUtil.getRandomDir();
                return build(robot.SPECS.PILGRIM, randDir[0], randDir[1]);
            });
            if (action != null) {
                pilgrimsBuilt++;
            }
        } else {
            if (crusadersBuilt > 20) {
                action = null;
            } else if (robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE) {
                action = tryAction(20, () -> {
                    int[] randDir = RobotUtil.getRandomDir();
                    return build(robot.SPECS.CRUSADER, randDir[0], randDir[1]);
                });

                if (action == null) {
                    Log.i("COULDN'T BUILD CRUSADER");
                } else {
                    crusadersBuilt++;
                }
            }
        }

        return action;
    }
}