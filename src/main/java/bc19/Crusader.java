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
            double distToEnemy = RobotUtil.findDistance(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
            Log.d("DISTANCE TO ENEMY IS " + distToEnemy);
            Log.d("ATTACK RADIUS ARRAY " + robot.SPECS.UNITS[robot.SPECS.CRUSADER].ATTACK_RADIUS);
            Log.d("ATTACK RADIUS " + robot.SPECS.UNITS[robot.SPECS.CRUSADER].ATTACK_RADIUS[1]);
            Log.d("ATTACK RADIUS SQRT " + Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].ATTACK_RADIUS[1]));
            if (distToEnemy >= Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].ATTACK_RADIUS[1])) {
                // move toward the enemy
                int dx = enemyRobot.x - robot.me.x;
                int dy = enemyRobot.y - robot.me.y;

                dx = (int) (dx - Math.signum(dx)*1);
                dy = (int) (dy - Math.signum(dy)*1);

                Log.i("MOVING TOWARD THE ENEMY");
                if (Math.abs(dx) > Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED)) {
                    dx = (int) (Math.floor(Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED)) * Math.signum(dx));
                }

                if (Math.abs(dy) > Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED)) {
                    dy = (int) (Math.floor(Math.sqrt(robot.SPECS.UNITS[robot.SPECS.CRUSADER].SPEED)) * Math.signum(dy));
                }

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
                int[] goalDir = RobotUtil.getRandomDir();
                return move(goalDir[0], goalDir[1]);
            });
        }

        return action;
    }
}