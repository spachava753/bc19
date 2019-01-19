package bc19;

import java.util.HashSet;
import java.util.Set;

public class Church extends RobotType {

    private FiniteStateMachine chruchStateMachine;
    private int[] enemyRobotLoc = null;
    private Action action;

    public Church(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public void initialize() {
        super.initialize();

        State idle = new State("idle");
        State defenseMode = new State("defenseMode");

        Set<State> states = new HashSet<State>();
        states.add(idle);
        states.add(defenseMode);

        Transition enemyDetection = new TransitionBuilder()
                .name("enemyDetection")
                .sourceState(idle)
                .eventType(EnemyDetected.class)
                .eventHandler(new EventHandler() {
                    @Override
                    public void handleEvent(Event event) throws Exception {
                        if (enemyRobotLoc != null && robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_FUEL) {
                            int[] goalDir = RobotUtil.getDir(robot.me.x, robot.me.y, enemyRobotLoc[0], enemyRobotLoc[1]);
                            action = build(Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                            if (action == null)
                                Log.i("COULDN'T BUILD CRUSADER");
                        }
                    }
                })
                .targetState(defenseMode)
                .build();

        Transition enemyLost = new TransitionBuilder()
                .name("enemyLost")
                .sourceState(defenseMode)
                .eventType(EnemyMissing.class)
                .eventHandler(new EventHandler() {
                    @Override
                    public void handleEvent(Event event) throws Exception {
                        Log.i("NO ENEMIES FOUND");
                    }
                })
                .targetState(idle)
                .build();

        chruchStateMachine = new FiniteStateMachineBuilder(states, idle)
                .registerTransition(enemyDetection)
                .registerTransition(enemyLost)
                .build();
    }

    @Override
    public void initTakeTurn() {
        super.initTakeTurn();
        action = null;
    }

    @Override
    public Action takeTurn() {
        Log.i("INSIDE CHURCH TURN METHOD");

        // check if enemies have been spotted
        int[] enemyRobotLoc = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobotLoc = new int[2];
                enemyRobotLoc[0] = visibleRobot.x;
                enemyRobotLoc[1] = visibleRobot.y;
            }
        }

        if (enemyRobotLoc != null) {
            // spam crusaders
            try {
                chruchStateMachine.fire(new EnemyDetected());
            } catch (FiniteStateMachineException e) {
                e.printStackTrace();
            }
        } else {
            try {
                chruchStateMachine.fire(new EnemyMissing());
            } catch (FiniteStateMachineException e) {
                e.printStackTrace();
            }
        }

        return action;
    }

    class EnemyDetected extends Event {
    }

    class EnemyMissing extends Event {
    }
}