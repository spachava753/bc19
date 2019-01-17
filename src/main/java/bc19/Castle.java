package bc19;

import java.util.List;

public class Castle extends RobotType {

    private int pilgrimsBuilt;
    private int crusadersBuilt;
    private int numOfDeposits;
    private boolean buildNextPilgrim = true;

    public Castle(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public void initialize() {
        super.initialize();
        CrusaderConstants.KARB_CONSTRUCTION_COST = robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE;
        CrusaderConstants.FUEL_CONSTRUCTION_COST = robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_FUEL;
        numOfDeposits = getDeposits(getFullMap()).size();
    }


    @Override
    public Action takeTurn() {
        Action action = null;


        int numOfPilgrimsHealthyAndRefining = 0;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team == robot.me.team) {
                if (visibleRobot.unit == Constants.PILGRIM_UNIT && visibleRobot.castle_talk == CastleTalkConstants.PILGRIM_REFINERY_AVAILABLE) {
                    numOfPilgrimsHealthyAndRefining++;
                }
            }
        }

        if (numOfPilgrimsHealthyAndRefining > 0 && numOfPilgrimsHealthyAndRefining == pilgrimsBuilt) {
            buildNextPilgrim = true;
        }

        //prioritize building crusaders if there is an enemy within our vision radius
        Robot enemyRobot = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobot = visibleRobot;
                break;
            }
        }


        // check if a deposit is in one of our adjacent squares
        List<int[]> tileDir = Util.getAdjacentTilesWithDeposits(robot, getFullMap());


        // defensive measures
        if (enemyRobot != null) {
            //robot.log("FOUND AN ENEMY ROBOT");
            if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {
                int[] goalDir = Util.getDir(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
                action = build(Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                if (action == null) {
                    //robot.log("COULDN'T BUILD CRUSADER");
                }
            }
        } else if (!tileDir.isEmpty() && (tileDir.size() > pilgrimsBuilt)) {
            //robot.log("ONE OF THE ADJACENT TILES HAS A DEPOSIT");

            for (int[] direction : tileDir) {
                action = build(Constants.PILGRIM_UNIT, direction[0], direction[1]);
                if (action != null) {
                    pilgrimsBuilt++;
                    break;
                }
            }
        } else if (numOfDeposits > pilgrimsBuilt) {
            if (buildNextPilgrim) {
                // number of times to retry building
                robot.log("BUILDING NEW PILGRIM");
                int[] randDir = Util.getRandomDir();
                action = tryAction(20, () -> build(Constants.PILGRIM_UNIT, randDir[0], randDir[1]));
                if (action != null) {
                    buildNextPilgrim = false;
                    pilgrimsBuilt++;
                }
            }
        } else {
            if (crusadersBuilt > 20) {
                action = null;
            } else if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {

                int[] randDir = Util.getRandomDir();
                action = tryAction(20, () -> build(Constants.CRUSADER_UNIT, randDir[0], randDir[1]));

                if (action == null) {
                    //robot.log("COULDN'T BUILD CRUSADER");
                } else
                    crusadersBuilt++;
            }
        }

        return action;
    }
}