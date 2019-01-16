package bc19;

import java.util.LinkedList;
import java.util.List;

public class Pilgrim extends RobotType {

    private int[][] fullMap;
    private int[][] pathMap;
    private int[] refinery;
    private boolean refineryAvailable = false;
    private boolean builtRefinery = false;
    private PathFinder pf;
    private List<Node> pathNodes;
    private Node goalNode;
    private LinkedList<Node> occupiedNodes;
    private List<Node> deposits;

    public Pilgrim(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
        deposits = Util.getDeposits(fullMap);
        occupiedNodes = new LinkedList();
    }

    @Override
    public Action turn() {
        Action action = null;

        if(refineryAvailable){
            robot.castleTalk(CastleTalkConstants.PILGRIM_REFINERY_AVAILABLE);
        }


        // decide where we need to go, if we need to go somewhere
        if ((goalNode == null) || (robot.getVisibleRobotMap()[goalNode.y][goalNode.x] != 0 && robot.getVisibleRobotMap()[goalNode.y][goalNode.x] != -1)) {
            robot.log("DECIDING PILGRIM GOAL");
            // if the previous goal is not null make sure that we add it to the occupied node list
            if (goalNode != null)
                occupiedNodes.add(goalNode);

            calcNewPathMap();
            calcGoal();
        } else {
            robot.log("GOAL NODE HAS ALREADY BEEN DEFINED");
            robot.log("GOAL NODE X: " + goalNode.x);
            robot.log("GOAL NODE Y: " + goalNode.y);

        }


        // if we are full of resources, give it to a castle or church
        if (robot.me.karbonite == PilgrimConstants.KARB_CARRYING_CAPACITY || robot.me.fuel == PilgrimConstants.FUEL_CARRYING_CAPACITY) {
            robot.log("FULL OF RESOURCES");
            // each pilgrim that mines is responsible for building at least one church, to protect the resource
            if (!builtRefinery && !refineryAvailable && (robot.karbonite > ChurchConstants.KARB_CONSTRUCTION_COST && robot.fuel > ChurchConstants.FUEL_CONSTRUCTION_COST)) {
                // build a church somewhere
                int[] randDir = Util.getRandomDir();
                for (int i = 0; i < 8 || action == null; i++) {
                    randDir = Util.getRandomDir();
                    action = build(robot, Constants.CHURCH_UNIT, randDir[0], randDir[1]);
                }

                if (action != null) {
                    robot.log("BUILDING A NEW CHURCH");
                    builtRefinery = true;
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
            if (pathNodes != null) {
                robot.log("NODES PATH SIZE: " + pathNodes.size());
                Node nextNode = pathNodes.get(0);
                int[] dir = Util.getDir(robot.me.x, robot.me.y, nextNode.x, nextNode.y);
                action = move(robot, dir[0], dir[1]);
                if (action != null) {
                    pathNodes.remove(0);
                } else {
                    robot.log("MOVE ACTION WAS NULL");
                    robot.log("CALCULATING NEW PATH");
                    // if the previous goal is not null make sure that we add it to the occupied node list
                    if (goalNode != null && !occupiedNodes.contains(goalNode))
                        occupiedNodes.add(goalNode);

                    goalNode = null;

                    int[]randDir = Util.getRandomDir();
                    action = move(robot, randDir[0], randDir[1]);
                    //calcGoal();
                    //calcNewPathMap();
                }
            } else {
                robot.log("NODES PATH IS NULL");
            }
        }

        return action;
    }

    private void calcGoal() {
        robot.log("CALC NEW GOAL");
        // set a new goal

        /*
        definePath:
        {
            for (int mapY = 0; mapY < fullMap.length; mapY++) {
                for (int mapX = 0; mapX < fullMap.length; mapX++) {
                    // check if the node is occupied
                    Node node = new Node(mapX, mapY);
                    if (!occupiedNodes.contains(node)) {
                        if (fullMap[mapY][mapX] == Util.KARBONITE || fullMap[mapY][mapX] == Util.FUEL) {
                            goalNode = node;
                            pf = new PathFinder(pathMap, goalNode, robot);
                            robot.log("CREATING A NEW PATH");
                            pathNodes = pf.compute(new Node(robot.me.x, robot.me.y));
                            robot.log("PATH NODES: " + pathNodes);
                            break definePath;
                        }
                    }
                }
            }
        }
        */

        Node minDistNode = null;

        for(Node node: deposits){
            if(!occupiedNodes.contains(node)){
                if(minDistNode == null){
                    minDistNode = node;
                } else {
                    double prevDist = Util.findDistance(robot.me.x, robot.me.y, minDistNode.x, minDistNode.y);
                    double newDist = Util.findDistance(robot.me.x, robot.me.y, node.x, node.y);
                    if(newDist < prevDist){
                        minDistNode = node;
                    }
                }
            }
        }

        if(minDistNode != null){
            goalNode = minDistNode;
            pf = new PathFinder(pathMap, goalNode, robot);
            robot.log("CREATING A NEW PATH");
            pathNodes = pf.compute(new Node(robot.me.x, robot.me.y));
            robot.log("PATH NODES: " + pathNodes);
        }
        else{
            robot.log("ALL DEPOSITS ARE OCCUPIED");
            robot.log("RESETTING ...");
            occupiedNodes.removeAll(occupiedNodes);
        }
    }

    private void calcNewPathMap() {
        int[][] visibleRobotMap = robot.getVisibleRobotMap();
        pathMap = new int[fullMap.length][fullMap.length];
        for (int y = 0; y < fullMap.length; y++) {
            for (int x = 0; x < fullMap.length; x++) {
                if ((visibleRobotMap[y][x] == 0 || visibleRobotMap[y][x] == -1) && fullMap[y][x] != Util.NONE) {
                    pathMap[y][x] = 1;
                } else {
                    pathMap[y][x] = 0;
                }
            }
        }
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

    private Action move(BCAbstractRobot robot, int dx, int dy) {
        int newX = robot.me.x + dx;
        int newY = robot.me.y + dy;

        // do some validations here

        //check if going of  the map
        if (newX < 0 || newY < 0) {
            robot.log("ONE OF THE GIVEN PARAMETERS WAS LESS THAN ZERO");
            return null;
        }

        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.x == newX && visibleRobot.y == newY) {
                robot.log("A ROBOT OCCUPIES THE SPACE THAT WE ARE TRYING TO GET TO.");
                return null;
            }
        }

        return robot.move(dx, dy);
    }
}