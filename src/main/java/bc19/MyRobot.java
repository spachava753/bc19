package bc19;

public class MyRobot extends BCAbstractRobot {

    public Action turn() {
        int unit_type = me.unit;
        log("INSIDE TURN " +  me.turn);
        switch (unit_type) {
            case Constants.CASTLE_UNIT:
                return runCastle();
            case Constants.CHURCH_UNIT:
                return runChurch();
            case Constants.PILGRIM_UNIT:
                return runPilgrim();
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

    public Action runCastle() {
        Castle c = new Castle();
        return c.turn(this);
    }


    public Action runChurch() {
        return null;
    }

    public Action runPilgrim() {
        Pilgrim pilgrim = new Pilgrim();
        return pilgrim.turn(this);
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