package bc19;

import java.util.ArrayList;
import java.util.List;

public final class Util {
    public static final int KARBONITE = 0;
    public static final int FUEL = 1;
    public static final int TERRAIN = 2;
    public static final int NONE = 3;

    public static final int[] NORTH = {0, 1};
    public static final int[] SOUTH = {0, -1};
    public static final int[] EAST = {1, 0};
    public static final int[] WEST = {-1, 0};
    public static final int[] NORTHEAST = {1, 1};
    public static final int[] NORTHWEST = {-1, 1};
    public static final int[] SOUTHEAST = {1, -1};
    public static final int[] SOUTHWEST = {-1, -1};
    public static final int[][] DIRECTIONS = {NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST};


    public static int[][] aggregateMap(BCAbstractRobot robot){
        int[][] fullMap = new int[robot.map.length][robot.map[0].length];
        for(int y = 0; y < robot.map.length; y++){
            for (int x = 0; x < robot.map[0].length; x++){
                if (robot.karboniteMap[y][x])
                    fullMap[y][x] = KARBONITE;
                else if (robot.fuelMap[y][x])
                    fullMap[y][x] = FUEL;
                else if (robot.getPassableMap()[y][x])
                    fullMap[y][x] = TERRAIN;
                else
                    fullMap[y][x] = NONE;
            }
        }

        return fullMap;
    }

    public static boolean isVerticallySymmetric(BCAbstractRobot robot) {

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

    public static int[] getRandomDir(){
        Random random = new Random();
        return DIRECTIONS[random.nextInt(DIRECTIONS.length)];
    }

    public static List<int[]> getAdjacentTilesWithDeposits(BCAbstractRobot robot, int[][] fullMap){
        //robot.log("inside Util.getAdjacentTilesWithDeposits()");
        List<int[]> depositTileDirections = new ArrayList<>();
        for(int[] direction: DIRECTIONS){
            int checkX = robot.me.x + direction[0];
            int checkY = robot.me.y + direction[1];
            //robot.log("checkX is " + checkX);
            //robot.log("checkY is " + checkY);
            // check to make sure that the value doesn't exceed the boundaries of the map
            if (checkY < 0 || checkX < 0){
                // don't check a direction outside of the boundary
                //robot.log("can't check a direction outside of boundary -> continuing");
                continue;
            }
            switch (fullMap[checkY][checkX]){
                case KARBONITE:
                    //robot.log("found a karbonite deposit");
                    depositTileDirections.add(direction);
                    break;
                case FUEL:
                    //robot.log("found a fuel deposit");
                    depositTileDirections.add(direction);
                    break;
                default:
                    //robot.log("did not find a deposit");
                    break;
            }
        }

        //robot.log("found a total of " + depositTileDirections.size() + " in the adjacent tiles");

        return depositTileDirections;
    }
}
