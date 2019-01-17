package bc19;

import java.util.LinkedList;
import java.util.List;

public class Pilgrim extends RobotType {

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
    }

    @Override
    public void initialize() {
        super.initialize();
        deposits = Util.getDeposits(getFullMap());
        occupiedNodes = new LinkedList();
    }

    @Override
    public void initTakeTurn() {
        super.initTakeTurn();
        if (refineryAvailable) {
            robot.castleTalk(CastleTalkConstants.PILGRIM_REFINERY_AVAILABLE);
        }


        // decide where we need to go, if we need to go somewhere
        if ((goalNode == null) || (robot.getVisibleRobotMap()[goalNode.y][goalNode.x] != 0 && robot.getVisibleRobotMap()[goalNode.y][goalNode.x] != -1)) {
            Log.i("DECIDING PILGRIM GOAL");
            // if the previous goal is not null make sure that we add it to the occupied node list
            if (goalNode != null)
                occupiedNodes.add(goalNode);

            calcNewPathMap();
            calcGoal();
        } else {
            Log.i("GOAL NODE HAS ALREADY BEEN DEFINED");
            Log.i("GOAL NODE X: " + goalNode.x);
            Log.i("GOAL NODE Y: " + goalNode.y);

        }
    }

    @Override
    public Action takeTurn() {
        Action action = null;


        // if we are full of resources, give it to a castle or church
        if (robot.me.karbonite == robot.SPECS.UNITS[robot.SPECS.PILGRIM].KARBONITE_CAPACITY || robot.me.fuel == robot.SPECS.UNITS[robot.SPECS.PILGRIM].FUEL_CAPACITY) {
            Log.i("FULL OF RESOURCES");
            // each pilgrim that mines is responsible for building at least one church, to protect the resource
            if (!builtRefinery && !refineryAvailable && (robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CHURCH].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CHURCH].CONSTRUCTION_FUEL)) {
                // build a church somewhere
                int[] randDir = Util.getRandomDir();
                for (int i = 0; i < 8 || action == null; i++) {
                    randDir = Util.getRandomDir();
                    action = build(Constants.CHURCH_UNIT, randDir[0], randDir[1]);
                }

                if (action != null) {
                    Log.i("BUILDING A NEW CHURCH");
                    builtRefinery = true;
                }
            }

            if (refinery != null && Math.floor(Util.findDistance(refinery[0], refinery[1], robot.me.x, robot.me.y)) == 1) {
                int dx = refinery[0] - robot.me.x;
                int dy = refinery[1] - robot.me.y;
                action = robot.give(dx, dy, robot.me.karbonite, robot.me.fuel);
            } else {
                Log.i("refinery is null");
                Log.i("DISCOVERING NEW REFINERIES");
                if (discoverRefineries() != null) {
                    refinery = discoverRefineries();
                    refineryAvailable = true;
                }
            }
            // if we are on a deposit, build a church so we cash in our resources
        } else if (getFullMap()[robot.me.y][robot.me.x] == Util.KARBONITE || getFullMap()[robot.me.y][robot.me.x] == Util.FUEL) {

            if (refinery == null)
                refinery = discoverRefineries();
            else
                refineryAvailable = true;

            if (action == null) {
                Log.i("MINING RESOURCES");
                action = robot.mine();
            }
        } else {
            Log.i("MOVING TOWARD ANOTHER DEPOSIT");
            if (pathNodes != null) {
                Log.i("NODES PATH SIZE: " + pathNodes.size());
                Node nextNode = pathNodes.get(0);
                int[] dir = Util.getDir(robot.me.x, robot.me.y, nextNode.x, nextNode.y);
                action = move(dir[0], dir[1]);
                if (action != null) {
                    pathNodes.remove(0);
                } else {
                    Log.i("MOVE ACTION WAS NULL");
                    Log.i("CALCULATING NEW PATH");
                    // if the previous goal is not null make sure that we add it to the occupied node list
                    if (goalNode != null && !occupiedNodes.contains(goalNode))
                        occupiedNodes.add(goalNode);

                    goalNode = null;

                    action = tryAction(20, () -> {
                        int[] goalDir = Util.getRandomDir();
                        return move(goalDir[0], goalDir[1]);
                    });
                    //calcGoal();
                    //calcNewPathMap();
                }
            } else {
                Log.i("NODES PATH IS NULL");
            }
        }

        return action;
    }

    private void calcGoal() {
        Log.i("CALC NEW GOAL");
        // set a new goal

        Node minDistNode = null;

        for (Node node : deposits) {
            if (!occupiedNodes.contains(node)) {
                if (minDistNode == null) {
                    minDistNode = node;
                } else {
                    double prevDist = Util.findDistance(robot.me.x, robot.me.y, minDistNode.x, minDistNode.y);
                    double newDist = Util.findDistance(robot.me.x, robot.me.y, node.x, node.y);
                    if (newDist < prevDist) {
                        minDistNode = node;
                    }
                }
            }
        }

        if (minDistNode != null) {
            goalNode = minDistNode;
            pf = new PathFinder(pathMap, goalNode, robot);
            Log.i("CREATING A NEW PATH");
            pathNodes = pf.compute(new Node(robot.me.x, robot.me.y));
            Log.i("PATH NODES: " + pathNodes);
        } else {
            Log.i("ALL DEPOSITS ARE OCCUPIED");
            Log.i("RESETTING ...");
            occupiedNodes.removeAll(occupiedNodes);
        }
    }

    private void calcNewPathMap() {
        int[][] visibleRobotMap = robot.getVisibleRobotMap();
        pathMap = new int[getFullMap().length][getFullMap().length];
        for (int y = 0; y < getFullMap().length; y++) {
            for (int x = 0; x < getFullMap().length; x++) {
                if ((visibleRobotMap[y][x] == 0 || visibleRobotMap[y][x] == -1) && getFullMap()[y][x] != Util.NONE) {
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

}