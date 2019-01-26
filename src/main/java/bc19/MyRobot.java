package bc19;

public class MyRobot extends BCAbstractRobot {

    private RobotType robotType = null;

    public Action turn() {
        Log.setRobot(this);
        Log.useTags(new String[]{"TSS"});
        Log.i("-------------------- BEGIN TURN -----------------------");
        RobotUtil.setRobot(this);
        int unit_type = me.unit;
        switch (unit_type) {
            case Constants.CASTLE_UNIT:
                Log.level(Log.I);
                Log.i("INSIDE TURN " + me.turn);
                Log.i("UNIT TYPE: Castle");
                break;
            case Constants.CHURCH_UNIT:
                Log.i("UNIT TYPE: Church");
                break;
            case Constants.PILGRIM_UNIT:
                Log.i("UNIT TYPE: Pilgrim");
                break;
            case Constants.CRUSADER_UNIT:
                Log.i("UNIT TYPE: Crusader");
                break;
            case Constants.PROPHET_UNIT:
                Log.i("UNIT TYPE: Prophet");
                break;
            case Constants.PREACHER_UNIT:
                Log.i("UNIT TYPE: Preacher");
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
        Log.i("Execution time: " + String.valueOf(System.currentTimeMillis() - currentTime));
        Log.i("GLOBAL KARB: " + robotType.robot.karbonite);
        Log.i("GLOBAL FUEL: " + robotType.robot.fuel);
        Log.i("-------------------- END TURN -----------------------");
        return action;
    }

    public Action runProphet() {
        return null;
    }

    public Action runPreacher() {
        return null;
    }

}