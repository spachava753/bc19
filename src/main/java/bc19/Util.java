package bc19;

import bc19.util.ArrayList;
import bc19.util.List;

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

    private static BCAbstractRobot robot;

    public static BCAbstractRobot getRobot() {
        return robot;
    }

    public static void setRobot(BCAbstractRobot bcRobot) {
        robot = bcRobot;
    }

    public static int[] getRandomDir() {
        Random random = new Random();
        return DIRECTIONS[random.nextInt(DIRECTIONS.length)];
    }

    public static List<int[]> getAdjacentTilesWithDeposits(BCAbstractRobot robot, int[][] fullMap) {
        //robot.log("inside Util.getAdjacentTilesWithDeposits()");
        List<int[]> depositTileDirections = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            int checkX = robot.me.x + direction[0];
            int checkY = robot.me.y + direction[1];
            //robot.log("checkX is " + checkX);
            //robot.log("checkY is " + checkY);
            // check to make sure that the value doesn't exceed the boundaries of the map
            if (checkY < 0 || checkX < 0) {
                // don't check a direction outside of the boundary
                //robot.log("can't check a direction outside of boundary -> continuing");
                continue;
            }
            switch (fullMap[checkY][checkX]) {
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

    public static double findDistance(Node start, Node end) {
        return findDistance(start.x, start.y, end.x, end.y);
    }

    public static double findDistance(int x1, int y1, int x2, int y2) {
        int x = x2 - x1;
        int y = y2 - y1;
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public static int[] reflectPos(int x, int y, boolean verticallySymmetric, int[][] fullMap) {
        int[] reflectedPos = new int[2];

        if (verticallySymmetric) {
            reflectedPos[0] = fullMap.length - y;
            reflectedPos[1] = y;
        } else {
            reflectedPos[0] = x;
            reflectedPos[1] = fullMap.length - y;
        }

        return reflectedPos;
    }

    public static int[] getDir(int startX, int startY, int destX, int destY) {
        int[] newDir = {destX - startX, destY - startY};

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

    public static List<Node> getDeposits(int[][] fullMap){
        List<Node> deposits = new ArrayList<>();

        for (int mapY = 0; mapY < fullMap.length; mapY++) {
            for (int mapX = 0; mapX < fullMap.length; mapX++) {
                // check if the node is occupied
                Node node = new Node(mapX, mapY);
                if (fullMap[mapY][mapX] == Util.KARBONITE || fullMap[mapY][mapX] == Util.FUEL) {
                    deposits.add(node);
                }
            }
        }

        return deposits;
    }

    public static void log(String msg){
        robot.log(msg);
    }
}
