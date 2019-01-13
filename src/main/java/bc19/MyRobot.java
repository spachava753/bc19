package bc19;

public class MyRobot extends BCAbstractRobot {

    private RobotType robotType = null;

    public Action turn() {
        log("-------------------- BEGIN TURN -----------------------");
        int unit_type = me.unit;
        switch (unit_type) {
            case Constants.CASTLE_UNIT:
                log("INSIDE TURN " + me.turn);
                log("UNIT TYPE: Castle");
                break;
            case Constants.CHURCH_UNIT:
                log("UNIT TYPE: Church");
                break;
            case Constants.PILGRIM_UNIT:
                log("UNIT TYPE: Pilgrim");
                break;
            case Constants.CRUSADER_UNIT:
                log("UNIT TYPE: Crusader");
                break;
            case Constants.PROPHET_UNIT:
                log("UNIT TYPE: Prophet");
                break;
            case Constants.PREACHER_UNIT:
                log("UNIT TYPE: Preacher");
                break;
            default:
                return null;
        }
        long currentTime = System.currentTimeMillis();
        if (robotType == null) {
            switch (unit_type) {
                case Constants.CASTLE_UNIT:
                    robotType = new Castle(this);
                    break;
                case Constants.CHURCH_UNIT:
                    robotType = new Church(this);
                    break;
                case Constants.PILGRIM_UNIT:
                    robotType = new Pilgrim(this);
                    break;
                case Constants.CRUSADER_UNIT:
                    robotType = new Crusader(this);
                    break;
                case Constants.PROPHET_UNIT:
                    return runProphet();
                case Constants.PREACHER_UNIT:
                    return runPreacher();
                default:
                    return null;
            }
        }

        Action action = robotType.turn();
        log("Execution time: " + String.valueOf(System.currentTimeMillis() - currentTime));
        log("GLOBAL KARB: " + robotType.robot.karbonite);
        log("GLOBAL FUEL: " + robotType.robot.fuel);
        log("-------------------- END TURN -----------------------");
        return action;
    }

    public Action runProphet() {
        return null;
    }

    public Action runPreacher() {
        return null;
    }

}