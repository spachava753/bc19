package bc19;

public class Pilgrim extends RobotType{

    private static final int[][] choices = {{0,-1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};
    private BCAbstractRobot robot = null;

    @Override
    public Action turn(BCAbstractRobot r) {
        this.robot = r;
        Random random = new Random();
        int[] choice = choices[random.nextInt(choices.length)];
        robot.log("Trying to find nearest deposit");
        return robot.move(choice[0], choice[1]);
    }
}