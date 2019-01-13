package bc19;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Castle extends RobotType {

    private int[][] fullMap;
    private int pilgrimsBuilt;
    private int crusadersBuilt;
    private int numOfDeposits;

    public Castle(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        robot.log(String.valueOf(fullMap));
        for(int[] col: fullMap){
            for(int tile: col){
                if(tile == Util.FUEL || tile == Util.KARBONITE) {
                    numOfDeposits++;
                }
            }
        }
    }


    @Override
    public Action turn() {
        Action action = null;

        // check if a deposit is in one of our adjacent squares
        List<int[]> tileDir = Util.getAdjacentTilesWithDeposits(robot, fullMap);
        if (!tileDir.isEmpty() && (tileDir.size() > pilgrimsBuilt)) {
            //robot.log("ONE OF THE ADJACENT TILES HAS A DEPOSIT");

            for (int[] direction : tileDir) {
                action = buildUnit(robot, Constants.PILGRIM_UNIT, direction[0], direction[1]);
                if (action != null) {
                    pilgrimsBuilt++;
                    break;
                }
            }
        } else if(numOfDeposits > pilgrimsBuilt) {
            // number of times to retry building
            for (int i = 0; i < 20 || action == null; i++) {
                int[] randDir = Util.getRandomDir();
                action = buildUnit(robot, Constants.PILGRIM_UNIT, randDir[0], randDir[1]);
            }

            if(action != null)
                pilgrimsBuilt++;
        }else {
            if (crusadersBuilt > 20) {
                action = null;
            } else if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {
                // number of times to retry building
                for (int i = 0; i < 20 || action == null; i++) {
                    int[] randDir = Util.getRandomDir();
                    action = buildUnit(robot, Constants.CRUSADER_UNIT, randDir[0], randDir[1]);
                }

                if (action == null)
                    robot.log("COULDN'T BUILD CRUSADER");
                else
                    crusadersBuilt++;
            }
        }

        return action;
    }

    private Action buildUnit(BCAbstractRobot robot, int unit, int dx, int dy) {
        int x = robot.me.x + dx;
        int y = robot.me.y + dy;
        // check if that specific tile is occupied
        int[][] visibleMap = robot.getVisibleRobotMap();
        if (visibleMap[y][x] == 0) {

            //robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
            return robot.buildUnit(unit, dx, dy);
        } else
            return null;
    }
}