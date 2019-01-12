package bc19;

import java.util.List;

public class Castle extends RobotType{

    private static final int[][] choices = {{0,-1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};

    private boolean verticalSymetry;
    private boolean initialized = false;
    private int[][] fullMap;

    public Castle(BCAbstractRobot robot) {
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
        if(!initialized){
            initialize();
            initialized = true;
        }

        // check if a deposit is in one of our adjacent squares
        List<int[]> tileDir = Util.getAdjacentTilesWithDeposits(robot, fullMap);
        if(!tileDir.isEmpty()){
            //robot.log("ONE OF THE ADJACENT TILES HAS A DEPOSIT");

            for(int[] direction: tileDir){
                action = buildUnit(robot, Constants.PILGRIM_UNIT, direction[0], direction[1]);
                if(action != null)
                    break;
            }
        } else {
            //robot.log("BUILDING NEW UNIT IN A RANDOM DIRECTION");
            int[] dir = Util.getRandomDir();
            action = buildUnit(robot, Constants.PILGRIM_UNIT, dir[0], dir[1]);
        }

        return action;
    }

    private Action buildUnit(BCAbstractRobot robot, int unit, int dx, int dy){
        int x = robot.me.x + dx;
        int y = robot.me.y + dy;
        // check if that specific tile is occupied
        int[][] visibleMap = robot.getVisibleRobotMap();
        if(visibleMap[y][x] == 0){

            //robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
            return robot.buildUnit(Constants.PILGRIM_UNIT, dx, dy);
        } else
            return null;
    }
}