package bc19;

/*
 *
 * The current pathfinder is taking longer than 200 seconds to process (I think), so we cant use it. At all. Need to find a more time efficient algorithm.
 *
 */
public class Crusader extends RobotType {

    private int[][] fullMap;

    public Crusader(BCAbstractRobot robot) {
        super(robot);
        fullMap = Util.aggregateMap(robot);
    }

    @Override
    public Action turn() {
        Action action = null;



        Robot enemyRobot = null;
        for(Robot visibleRobot: robot.getVisibleRobots()){
            if(visibleRobot.team != robot.me.team){
                enemyRobot = visibleRobot;
                robot.log("FOUND AN ENEMY ROBOT");
                break;
            }

        }

        if(enemyRobot != null){
            // see if we can attack him
            double distToEnemy = Util.findDistance(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
            if(distToEnemy > CrusaderConstants.MAX_ATTACK_RANGE){
                // move toward the enemy
                robot.log("MOVING TOWARD THE ENEMY");
                int dx = Math.max(enemyRobot.x-robot.me.x, CrusaderConstants.MOVEMENT_SPEED);
                int dy = Math.max(enemyRobot.y-robot.me.y, CrusaderConstants.MOVEMENT_SPEED);
                action = move(robot, dx, dy);
            } else {
                //attack the enemy
                robot.log("ATTACKING THE ENEMY");
                int dx = enemyRobot.x-robot.me.x;
                int dy = enemyRobot.y-robot.me.y;
                action = robot.attack(dx, dy);
            }
        } else {
            robot.log("MOVING RANDOMLY");
            int[] randDir = Util.getRandomDir();
            action = move(robot, randDir[0], randDir[1]);
        }

        return action;
    }

    private Action move(BCAbstractRobot robot, int x, int y) {
        // do some validations here

        //check if going of  the map
        if (robot.me.x + x < 0 || robot.me.y + y < 0)
            return null;
        else {
            return robot.move(x, y);
        }
    }
}