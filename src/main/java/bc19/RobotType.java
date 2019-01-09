package bc19;

public abstract class RobotType {

    protected BCAbstractRobot robot;

    public RobotType(BCAbstractRobot robot) {
        this.robot = robot;
    }

    public abstract Action turn();
}