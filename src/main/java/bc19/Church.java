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

        setminKarbStockpile(50);
        setminFuelStockpile(100);

        State idle = new State("idle");
        State defenseMode = new State("defenseMode");

        Set<State> states = new HashSet<State>();
        states.add(idle);
        states.add(defenseMode);

        EventHandler handEnemy = event -> {
            Log.d("ENEMY DETECTED");
            if (enemyRobotLoc != null) {
                if(canBuildUnitWithResources(robot.SPECS.CRUSADER)){
                    int[] goalDir = RobotUtil.getDir(robot.me.x, robot.me.y, enemyRobotLoc[0], enemyRobotLoc[1]);
                    action = build(robot.SPECS.PROPHET, goalDir[0], goalDir[1]);

                    if (action == null)
                        Log.d("COULDN'T BUILD PROPHET");
                    else
                        Log.d("BUILT PROPHET");
                } else {
                    Log.d("NOT ENOUGH RESOURCES TO BUILD PROPHET");
                }
            } else {
                Log.d("ENEMYROBOTLOC IS NULL");
            }
        };

        Transition enemyDetection = new TransitionBuilder()
                .name("enemyDetection")
                .sourceState(idle)
                .eventType(EnemyDetected.class)
                .eventHandler(handEnemy)
                .targetState(defenseMode)
                .build();

        Transition enemyLocked = new TransitionBuilder()
                .name("enemyLocked")
                .sourceState(defenseMode)
                .eventType(EnemyDetected.class)
                .eventHandler(handEnemy)
                .targetState(defenseMode)
                .build();

        Transition enemyLost = new TransitionBuilder()
                .name("enemyLost")
                .sourceState(defenseMode)
                .eventType(EnemyMissing.class)
                .eventHandler(event -> Log.i("NO ENEMIES FOUND"))
                .targetState(idle)
                .build();

        Transition enemyNotFound = new TransitionBuilder()
                .name("enemyNotFound")
                .sourceState(idle)
                .eventType(EnemyMissing.class)
                .eventHandler(event -> Log.i("NO ENEMIES FOUND"))
                .targetState(idle)
                .build();

        chruchStateMachine = new FiniteStateMachineBuilder(states, idle)
                .registerTransition(enemyDetection)
                .registerTransition(enemyLocked)
                .registerTransition(enemyLost)
                .registerTransition(enemyNotFound)
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
        enemyRobotLoc = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobotLoc = new int[2];
                enemyRobotLoc[0] = visibleRobot.x;
                enemyRobotLoc[1] = visibleRobot.y;
            }
        }

        if (enemyRobotLoc != null) {
            Log.d("ENEMY LOC IS NOT NULL");
            Log.d("ENEMY LOC:", enemyRobotLoc);
            // spam crusaders
            try {
                chruchStateMachine.fire(new EnemyDetected());
            } catch (FiniteStateMachineException e) {
                Log.e(e);
            }
        } else {
            try {
                chruchStateMachine.fire(new EnemyMissing());
            } catch (FiniteStateMachineException e) {
                Log.e(e);
            }
        }

        return action;
    }

    class EnemyDetected extends Event {
    }

    class EnemyMissing extends Event {
    }
}