package bc19;

public class Crusader extends RobotType {

    public Crusader(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public void initialize(){
        super.initialize();
    }

    @Override
    public Action takeTurn() {
        Action action = null;
        Robot enemyRobot = null;

        //Log.i("TRYING TO FIND AN ENEMY ROBOT");
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobot = visibleRobot;
                //Log.i("FOUND AN ENEMY ROBOT");
                break;
            }
        }


        if (enemyRobot != null) {
            // see if we can attack him
            double distToEnemy = Util.findDistance(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
            Log.i("DISTANCE TO ENEMY IS " + distToEnemy);
            if (distToEnemy >= Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].ATTACK_RADIUS[1])) {
                // move toward the enemy
                int newX = enemyRobot.x - robot.me.x;
                int newY = enemyRobot.y - robot.me.y;
                Log.i("MOVING TOWARD THE ENEMY");
                if (newX > robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED) {
                    newX = robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED;
                }

                if (newY > robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED) {
                    newY = robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED;
                }

                int dx = newX;
                int dy = newY;

                Log.i("dx: " + dx);
                Log.i("dy: " + dy);

                action = move(dx, dy);
            } else {
                //attack the enemy
                Log.i("ATTACKING THE ENEMY");
                int dx = enemyRobot.x - robot.me.x;
                int dy = enemyRobot.y - robot.me.y;
                action = robot.attack(dx, dy);
            }
        } else {
            // move randomly
            Log.i("MOVING RANDOMLY");

            action = tryAction(20, () -> {
                int[] goalDir = Util.getRandomDir();
                return move(goalDir[0], goalDir[1]);
            });
        }

        return action;
    }
}