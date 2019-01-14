package bc19;

import java.util.LinkedList;
import java.util.List;

public class Pilgrim extends RobotType {

    private int[][] fullMap;
    private int[][] pathMap;
    private int[] refinery;
    private boolean refineryAvailable = false;
    private PathFinder pf;
    private List<Node> pathNodes;
    private Node goalNode;
    private LinkedList<Node> occupiedNodes;

    public Pilgrim(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        pathMap = new int[fullMap.length][fullMap.length];
        for(int i = 0; i < fullMap.length; i++){
            for(int x = 0; x < fullMap.length; x++){
                if(fullMap[i][x] != Util.NONE){
                    pathMap[i][x] = 1;
                } else {
                    pathMap[i][x] = 0;
                }
            }
        }
        occupiedNodes = new LinkedList();
        //refinery = discoverRefineries();
    }

    @Override
    public Action turn() {
        Action action = null;

        // decide where we need to go, if we need to go somewhere
        if((goalNode == null) || (robot.getVisibleRobotMap()[goalNode.y][goalNode.x] != 0 && robot.getVisibleRobotMap()[goalNode.y][goalNode.x] != -1)){
            robot.log("DECIDING PILGRIM GOAL");
            // if the previous goal is null make sure that we add it to the occupied node list
            if(goalNode != null)
                occupiedNodes.add(goalNode);

            // set a new goal
            definePath: {
                for(int mapY = 0; mapY < fullMap.length; mapY++){
                    for(int mapX = 0; mapX < fullMap.length; mapX++){
                        // check if the node is occupied
                        Node node = new Node(mapX, mapY);
                        if(!occupiedNodes.contains(node)){
                            if(fullMap[mapY][mapX] == Util.KARBONITE || fullMap[mapY][mapX] == Util.FUEL){
                                goalNode = node;
                                pf = new PathFinder(pathMap, goalNode);
                                robot.log("CREATING A NEW PATH");
                                pathNodes = pf.compute(new Node(robot.me.x, robot.me.y));
                                robot.log("PATH NODES: " + pathNodes);
                                break definePath;
                            }
                        }
                    }
                }
            }
        } else {
            robot.log("GOAL NODE HAS ALREADY BEEN DEFINED");
            robot.log("GOAL NODE X: " + goalNode.x);
            robot.log("GOAL NODE Y: " + goalNode.y);
        }


        // if we are full of resources, give it to a castle or church
        if (robot.me.karbonite == PilgrimConstants.KARB_CARRYING_CAPACITY || robot.me.fuel == PilgrimConstants.FUEL_CARRYING_CAPACITY) {
            robot.log("FULL OF RESOURCES");
            // each pilgrim that mines is responsible for building at least one church, to protect the resource
            if (!refineryAvailable && (robot.karbonite > ChurchConstants.KARB_CONSTRUCTION_COST && robot.fuel > ChurchConstants.FUEL_CONSTRUCTION_COST)) {
                // build a church somewhere
                int[] randDir = Util.getRandomDir();
                for (int i = 0; i < 8 || action == null; i++) {
                    randDir = Util.getRandomDir();
                    action = build(robot, Constants.CHURCH_UNIT, randDir[0], randDir[1]);
                }

                if (action != null) {
                    robot.log("BUILDING A NEW CHURCH");
                }
            }

            if (refinery != null && Math.floor(Util.findDistance(refinery[0], refinery[1], robot.me.x, robot.me.y)) == 1) {
                int dx = refinery[0] - robot.me.x;
                int dy = refinery[1] - robot.me.y;
                action = robot.give(dx, dy, robot.me.karbonite, robot.me.fuel);
            } else {
                robot.log("refinery is null");
                robot.log("DISCOVERING NEW REFINERIES");
                if (discoverRefineries() != null) {
                    refinery = discoverRefineries();
                    refineryAvailable = true;
                }
            }
            // if we are on a deposit, build a church so we cash in our resources
        } else if (fullMap[robot.me.y][robot.me.x] == Util.KARBONITE || fullMap[robot.me.y][robot.me.x] == Util.FUEL) {

            if (refinery == null)
                refinery = discoverRefineries();
            else
                refineryAvailable = true;

            if (action == null) {
                robot.log("MINING RESOURCES");
                action = robot.mine();
            }
        } else {
            robot.log("MOVING TOWARD ANOTHER DEPOSIT");
            if(pathNodes != null){
                Node nextNode = pathNodes.get(0);
                action = move(robot, nextNode.x, nextNode.y);
                if(action != null){
                    pathNodes.remove(0);
                }
            } else {
                robot.log("NODES PATH IS NULL");
            }
        }

        return action;
    }

    private int[] discoverRefineries() {
        int[] refineryPos = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team == robot.me.team) {
                if ((visibleRobot.unit == Constants.CASTLE_UNIT || visibleRobot.unit == Constants.CHURCH_UNIT)
                        && Math.floor(Util.findDistance(robot.me.x, robot.me.y, visibleRobot.x, visibleRobot.y)) == 1) {
                    refineryPos = new int[2];
                    refineryPos[0] = visibleRobot.x;
                    refineryPos[1] = visibleRobot.y;
                    break;
                }
            }
        }

        return refineryPos;
    }

    private Action build(BCAbstractRobot robot, int churchUnit, int dx, int dy) {
        int x = robot.me.x + dx;
        int y = robot.me.y + dy;
        // do some validations here

        // check if off the map
        int[][] visibleMap = robot.getVisibleRobotMap();
        if (visibleMap[y][x] != 0) {
            return null;
        } else if (x < 0 || y < 0) {
            return null;
        }

        robot.log("BUILDING A UNIT WITH COORDINATES (" + x + ", " + y + ")");
        return robot.buildUnit(Constants.CHURCH_UNIT, dx, dy);
    }

    private Action move(BCAbstractRobot robot, int x, int y) {
        int newX = robot.me.x + x;
        int newY = robot.me.y + y;

        // do some validations here

        //check if going of  the map
        if (newX < 0 || newY < 0)
            return null;

        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.x == newX && visibleRobot.y == newY) {
                return null;
            }
        }

        return robot.move(x, y);
    }
}