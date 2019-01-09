package bc19;

public class Pilgrim extends RobotType{

    private static final int[][] choices = {{0,-1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}};

    public Pilgrim(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public Action turn() {
        Action action = null;
        Random random = new Random();
        int[] choice = choices[random.nextInt(choices.length)];
        robot.log("Trying to find nearest deposit");

        boolean[][] karbMap = robot.getKarboniteMap();
        boolean[][] fuelMap = robot.getFuelMap();

        // go to the nearest karb deposit
        // search from our current location



        action = robot.move(choice[0], choice[1]);
        return action;
    }
}