package bc19;

public class Castle extends RobotType{

    private static final int[][] choices = {{0,-1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};

    @Override
    public Action turn(BCAbstractRobot robot) {
        Random random = new Random();
        int[] choice = choices[random.nextInt(choices.length)];
        robot.log("BUILDING NEW UNIT");
        return robot.buildUnit(Constants.PILGRIM_UNIT, choice[0], choice[1]);
    }
}