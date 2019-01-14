package bc19;

public class Castle extends RobotType {

    private int[][] fullMap;
    private int pilgrimsBuilt;
    private int crusadersBuilt;
    private int numOfDeposits;

    public Castle(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        //robot.log(String.valueOf(fullMap));
        for (int[] col : fullMap) {
            for (int tile : col) {
                if (tile == Util.FUEL || tile == Util.KARBONITE) {
                    numOfDeposits++;
                }
            }
        }

        initialize();
    }

    private void initialize() {
        CrusaderConstants.KARB_CONSTRUCTION_COST = robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE;
        CrusaderConstants.FUEL_CONSTRUCTION_COST = robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_FUEL;
    }


    @Override
    public Action turn() {
        Action action = null;

        //delete later after testing pathfinding code
        if (pilgrimsBuilt < 1) {
            int[] randDir = Util.SOUTH;
            action = buildUnit(robot, Constants.PILGRIM_UNIT, randDir[0], randDir[1]);
            if (action != null) {
                pilgrimsBuilt++;
            }
        }

        /*
        //prioritize building crusaders if there is an enemy within our vision radius
        Robot enemyRobot = null;
        for(Robot visibleRobot: robot.getVisibleRobots()){
            if(visibleRobot.team != robot.me.team){
                enemyRobot = visibleRobot;
                break;
            }
        }


        // check if a deposit is in one of our adjacent squares
        List<int[]> tileDir = Util.getAdjacentTilesWithDeposits(robot, fullMap);


        // defensive measures
        if(enemyRobot != null){
            //robot.log("FOUND AN ENEMY ROBOT");
            if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {
                int[] goalDir = Util.getDir(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
                action = buildUnit(robot, Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                if (action == null) {
                    //robot.log("COULDN'T BUILD CRUSADER");
                }
            }
        } else if (!tileDir.isEmpty() && (tileDir.size() > pilgrimsBuilt)) {
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
            //robot.log("NOW BUILDING PILGRIMS FOR FAR AWAY DEPOSITS");
            for (int i = 0; i < 20; i++) {
                int[] randDir = Util.getRandomDir();
                action = buildUnit(robot, Constants.PILGRIM_UNIT, randDir[0], randDir[1]);
                if(action != null){
                    pilgrimsBuilt++;
                    break;
                }
            }
        } else {
            if (crusadersBuilt > 20) {
                action = null;
            } else if (robot.karbonite > CrusaderConstants.KARB_CONSTRUCTION_COST && robot.fuel > CrusaderConstants.FUEL_CONSTRUCTION_COST) {
                // number of times to retry building
                for (int i = 0; i < 20 || action == null; i++) {
                    int[] randDir = Util.getRandomDir();
                    action = buildUnit(robot, Constants.CRUSADER_UNIT, randDir[0], randDir[1]);
                }

                if (action == null){
                    //robot.log("COULDN'T BUILD CRUSADER");
                }
                else
                    crusadersBuilt++;
            }
        }
        */

        return action;
    }

    private Action buildUnit(BCAbstractRobot robot, int unit, int dx, int dy) {
        //robot.log("BUILDING A NEW UNIT");
        int x = robot.me.x + dx;
        int y = robot.me.y + dy;
        // check if that specific tile is occupied
        int[][] visibleMap = robot.getVisibleRobotMap();
        if (visibleMap[y][x] != 0) {
            //robot.log("GIVEN TILE IS OCCUPIED");
            return null;
        }

        //robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
        return robot.buildUnit(unit, dx, dy);
    }
}