package bc19;

import bc19.lang.Override;

public class Church extends RobotType {

    public Church(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public Action takeTurn() {
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
            if (robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_FUEL) {
                int[] goalDir = Util.getDir(robot.me.x, robot.me.y, enemyRobotLoc[0], enemyRobotLoc[1]);
                action = build(Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                if (action == null)
                    robot.log("COULDN'T BUILD CRUSADER");
            }
        }

        return action;
    }
}