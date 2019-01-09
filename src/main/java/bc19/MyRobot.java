package bc19;

public class MyRobot extends BCAbstractRobot {

    private RobotType robotType = null;

    public Action turn() {
        int unit_type = me.unit;
        log("INSIDE TURN " +  me.turn);
        log("INSIDE TURN " +  getPassableMap());
        if (robotType == null){
            switch (unit_type) {
                case Constants.CASTLE_UNIT:
                    robotType = new Castle(this);
                    break;
                case Constants.CHURCH_UNIT:
                    return runChurch();
                case Constants.PILGRIM_UNIT:
                    robotType = new Pilgrim(this);
                    break;
                case Constants.CRUSADER_UNIT:
                    return runCrusader();
                case Constants.PROPHET_UNIT:
                    return runProphet();
                case Constants.PREACHER_UNIT:
                    return runPreacher();
                default:
                    return null;
            }
        }

        return robotType.turn();
    }


    public Action runChurch() {
        return null;
    }

    public Action runCrusader() {
        return null;
    }

    public Action runProphet() {
        return null;
    }

    public Action runPreacher() {
        return null;
    }

}