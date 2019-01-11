package bc19;

public class Castle extends RobotType{

    private static final int[][] choices = {{0,-1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};

    private boolean verticalSymetry;
    private boolean initialized = false;

    public Castle(BCAbstractRobot robot) {
        super(robot);
        for (int i = 0; i < robot.getKarboniteMap().length; i++){
            for (int x = 0; x < robot.getKarboniteMap()[0].length; x++) {
                if (robot.getKarboniteMap()[i][x]){
                    robot.log("i: " + i + ", x: " + x);
                }
            }
        }
    }

    private void initialize() {

        int mapSize = robot.getPassableMap().length;

        if (robot.me.team == 0) {
            // Red Team
            robot.log("Castle on red team");
        } else {
            // Blue Team
            robot.log("Castle on blue team");
        }

        robot.log("My location is => x:"+robot.me.x + " y:"+robot.me.y);

        verticalSymetry = isVerticallySymmetric();

        if (verticalSymetry) {
            robot.log("Believe map is vertically symmetric");
            robot.log("Believe enemy Castle is at position => x:"+(mapSize - 1 - robot.me.x)+" y:"+robot.me.y);
        } else {
            robot.log("Believe map is horizontally symmetric");
            robot.log("Believe enemy Castle is at position => x:"+robot.me.x+" y:"+(mapSize - 1 -robot.me.y));
        }


    }

    private boolean isVerticallySymmetric() {

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

    @Override
    public Action turn() {
        Action action = null;
        if(!initialized){
            initialize();
            initialized = true;
        }

        Random random = new Random();
        int[] choice = choices[random.nextInt(choices.length)];
        robot.log("BUILDING NEW UNIT");
        //action = robot.buildUnit(Constants.PILGRIM_UNIT, choice[0], choice[1]);
        return action;
    }
}