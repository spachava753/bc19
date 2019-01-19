package bc19;


import java.util.ArrayList;
import java.util.List;

public abstract class RobotType {

    protected BCAbstractRobot robot;

    private boolean initialized = false;

    private int[][] fullMap;

    public RobotType(BCAbstractRobot robot) {
        this.robot = robot;
    }

    public final Action turn() {
        if (!initialized) {
            initialize();
            initialized = true;
        }

        initTakeTurn();

        return takeTurn();
    }

    public void initialize() {

    }

    public void initTakeTurn() {

    }

    public abstract Action takeTurn();

    protected final int[] getDir(Node start, Node dest) {
        int[] newDir = {dest.x - start.x, dest.y - dest.y};

        if (newDir[0] < 0) {
            newDir[0] = -1;
        } else if (newDir[0] > 0) {
            newDir[0] = 1;
        }

        if (newDir[1] < 0) {
            newDir[1] = -1;
        } else if (newDir[1] > 0) {
            newDir[1] = 1;
        }

        return newDir;
    }

    public List<Node> getDeposits(int[][] fullMap) {
        List<Node> deposits = new ArrayList<>();

        for (int mapY = 0; mapY < fullMap.length; mapY++) {
            for (int mapX = 0; mapX < fullMap.length; mapX++) {
                // check if the node is occupied
                Node node = new Node(mapX, mapY);
                if (fullMap[mapY][mapX] == RobotUtil.KARBONITE || fullMap[mapY][mapX] == RobotUtil.FUEL) {
                    deposits.add(node);
                }
            }
        }

        return deposits;
    }

    public static List<Node> getAdjacentTilesWithDeposits(BCAbstractRobot robot, int[][] fullMap) {
        //Log.i("inside RobotUtil.getAdjacentTilesWithDeposits()");
        List<Node> depositTileDirections = new ArrayList<>();
        for (int[] direction : RobotUtil.DIRECTIONS) {
            int checkX = robot.me.x + direction[0];
            int checkY = robot.me.y + direction[1];
            //Log.i("checkX is " + checkX);
            //Log.i("checkY is " + checkY);
            // check to make sure that the value doesn't exceed the boundaries of the map
            if (checkY < 0 || checkX < 0) {
                // don't check a direction outside of the boundary
                //Log.i("can't check a direction outside of boundary -> continuing");
                continue;
            }
            switch (fullMap[checkY][checkX]) {
                case RobotUtil.FUEL:
                case RobotUtil.KARBONITE:
                    //Log.i("found a karbonite deposit");
                    depositTileDirections.add(new Node(direction[0], direction[1]));
                    break;
                default:
                    //Log.i("did not find a deposit");
                    break;
            }
        }

        //Log.i("found a total of " + depositTileDirections.size() + " in the adjacent tiles");

        return depositTileDirections;
    }

    public boolean isVerticallySymmetric() {

        boolean[][] passableMap = robot.getPassableMap();
        int mapSize = passableMap.length;

        for (int i = 0; i < mapSize / 2; i++) {
            for (int j = 0; j < mapSize / 2; j++) {
                if (passableMap[j][i] != passableMap[mapSize - 1 - j][i]) {
                    // Has to be vertical
                    return true;
                }
                if (passableMap[j][i] != passableMap[j][mapSize - 1 - i]) {
                    // Has to be horizontal
                    return false;
                }
            }
        }

        return true;

    }

    private int[][] aggregateMap() {
        int[][] fullMap = new int[robot.map.length][robot.map[0].length];
        for (int y = 0; y < robot.map.length; y++) {
            for (int x = 0; x < robot.map[0].length; x++) {
                if (robot.karboniteMap[y][x])
                    fullMap[y][x] = RobotUtil.KARBONITE;
                else if (robot.fuelMap[y][x])
                    fullMap[y][x] = RobotUtil.FUEL;
                else if (robot.getPassableMap()[y][x])
                    fullMap[y][x] = RobotUtil.TERRAIN;
                else
                    fullMap[y][x] = RobotUtil.NONE;
            }
        }

        return fullMap;
    }

    public int[][] getFullMap() {

        // we only initialize fullMap if we need it
        if (fullMap == null)
            fullMap = aggregateMap();

        return fullMap;
    }

    protected Action build(int unit, int dx, int dy) {
        Action result = null;
        // check if this unit can build or not
        if (canBuild()) {
            if (canBuildUnit(unit)) {
                int x = robot.me.x + dx;
                int y = robot.me.y + dy;
                // do some validations here


                Node node = new Node(x, y);

                // do some validations here

                if (checkIfSpaceIsPassable(node) && !checkIfSpaceIsOccupied(node)) {
                    Log.i("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
                    result = robot.buildUnit(unit, dx, dy);
                }

            }
        }

        return result;
    }

    private boolean canBuild() {
        return robot.me.unit == robot.SPECS.CASTLE || robot.me.unit == robot.SPECS.CHURCH || robot.me.unit == robot.SPECS.PILGRIM;
    }

    private boolean canBuildUnit(int unit) {
        // if it is a pilgrim, it can build only churches
        if (robot.me.unit == robot.SPECS.PILGRIM && unit == robot.SPECS.CHURCH) {
            return true;
        }

        // if it is a church, it can build anything but castles
        if (robot.me.unit == robot.SPECS.CHURCH && unit != robot.SPECS.CASTLE) {
            return true;
        }

        // if it is a castles, it can build anything but churches
        if (robot.me.unit == robot.SPECS.CASTLE && unit != robot.SPECS.CHURCH) {
            return true;
        }

        return false;
    }

    protected Action move(int dx, int dy) {
        Action result = null;
        if (robot.me.unit != robot.SPECS.CASTLE || robot.me.unit != robot.SPECS.CHURCH) {
            int newX = robot.me.x + dx;
            int newY = robot.me.y + dy;

            Node node = new Node(newX, newY);

            // do some validations here

            if (checkIfSpaceIsPassable(node) && !checkIfSpaceIsOccupied(node)) {
                result = robot.move(dx, dy);
            }
        }

        return result;
    }

    public boolean checkIfSpaceIsOccupied(Node node) {
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.x == node.x && visibleRobot.y == node.y) {
                Log.i("A ROBOT OCCUPIES THE SPACE THAT WE ARE TRYING TO USE.");
                return true;
            }
        }

        return false;
    }

    public boolean checkIfSpaceIsPassable(Node node) {
        return checkIfOnMap(node) && getFullMap()[node.y][node.x] != RobotUtil.NONE;
    }

    public boolean checkIfOnMap(Node node) {
        if (node.x < 0 || node.y < 0) {
            return false;
        }

        if (node.x >= getFullMap().length || node.y >= getFullMap()[0].length) {
            return false;
        }

        return true;
    }

    protected Action tryAction(int numOfTimesToTry, DoAction doAction){
        Action action = null;
        for(int i = 0; i < numOfTimesToTry && action == null; i++){
            action = doAction.doAction();
        }
        return action;
    }

    public interface DoAction {

        Action doAction();
    }

}