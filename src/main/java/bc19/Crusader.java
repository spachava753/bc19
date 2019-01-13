package bc19;

public class Crusader extends RobotType {

    private int[][] fullMap;

    public Crusader(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
    }

    private void initialize(){
        CrusaderConstants.MOVEMENT_SPEED = robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED;
        CrusaderConstants.MAX_ATTACK_RANGE = robot.SPECS.UNITS[robot.SPECS.CRUSADER].ATTACK_RADIUS[1];
    }

    @Override
    public Action turn() {
        Action action = null;
        Robot enemyRobot = null;

        robot.log("TRYING TO FIND AN ENEMY ROBOT");
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobot = visibleRobot;
                robot.log("FOUND AN ENEMY ROBOT");
                break;
            }
        }


        if (enemyRobot != null) {
            // see if we can attack him
            double distToEnemy = Util.findDistance(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
            robot.log("DISTANCE TO ENEMY IS " + distToEnemy);
            if (distToEnemy > CrusaderConstants.MAX_ATTACK_RANGE) {
                // move toward the enemy
                int newX = enemyRobot.x - robot.me.x;
                int newY = enemyRobot.y - robot.me.y;
                robot.log("MOVING TOWARD THE ENEMY");
                if (newX > CrusaderConstants.MOVEMENT_SPEED) {
                    newX = CrusaderConstants.MOVEMENT_SPEED;
                }

                if (newY > CrusaderConstants.MOVEMENT_SPEED) {
                    newY = CrusaderConstants.MOVEMENT_SPEED;
                }

                int dx = newX;
                int dy = newY;

                robot.log("dx: " + dx);
                robot.log("dy: " + dy);

                action = move(robot, dx, dy);
            } else {
                //attack the enemy
                robot.log("ATTACKING THE ENEMY");
                int dx = enemyRobot.x - robot.me.x;
                int dy = enemyRobot.y - robot.me.y;
                action = robot.attack(dx, dy);
            }
        } else {
            // move randomly
            robot.log("MOVING RANDOMLY ROBOT");
            int[] goalDir;
            goalDir = Util.getRandomDir();
            action = move(robot, goalDir[0], goalDir[1]);
        }

        return action;
    }

    private Action move(BCAbstractRobot robot, int x, int y) {
        int newX = robot.me.x + x;
        int newY = robot.me.y + y;

        // do some validations here

        //check if going of  the map
        if (newX < 0 || newY < 0)
            return null;

        // check if tile is occupied
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.x == newX && visibleRobot.y == newY) {
                return null;
            }
        }

        // check if tile is passable
        if (fullMap[newY][newX] == Util.NONE) {
            return null;
        }

        return robot.move(x, y);
    }
}