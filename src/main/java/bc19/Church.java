package bc19;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Church extends RobotType {

    private FiniteStateMachine chruchStateMachine;
    private int[] enemyRobotLoc = null;
    private Action action;

    private boolean builtChainPilgrim;

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

        EventHandler handleEnemy = event -> {
            Log.d("ENEMY DETECTED");
            if (enemyRobotLoc != null) {
                if (canBuildUnitWithResources(robot.SPECS.CRUSADER)) {
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

        EventHandler buildEventHandler = event -> {
            Log.i("NO ENEMIES FOUND");

            List<int[]> tileDir = RobotUtil.getAdjacentTilesWithDeposits(robot, getFullMap());
            Log.d("TILE_DIR SIZE", tileDir.size());
            if (!tileDir.isEmpty()) {
                Log.i("ONE OF THE ADJACENT TILES HAS A DEPOSIT");

                for (int[] direction : tileDir) {
                    action = build(robot.SPECS.PILGRIM, direction[0], direction[1]);
                    if(action != null){
                        return;
                    }
                }

            }

            if(!builtChainPilgrim){
                if (canBuildUnitWithResources(robot.SPECS.PILGRIM) && robot.karbonite > getminKarbStockpile()
                        && robot.fuel > getminFuelStockpile()) {

                    Log.i("BUILDING NEW PILGRIM");
                    action = tryAction(20, () -> {
                        int[] randDir = RobotUtil.getRandomDir();
                        return build(robot.SPECS.PILGRIM, randDir[0], randDir[1]);
                    });

                    if(action != null){
                        builtChainPilgrim = true;
                    }
                }
            }
        };

        Transition enemyDetection = new TransitionBuilder()
                .name("enemyDetection")
                .sourceState(idle)
                .eventType(EnemyDetected.class)
                .eventHandler(handleEnemy)
                .targetState(defenseMode)
                .build();

        Transition enemyLocked = new TransitionBuilder()
                .name("enemyLocked")
                .sourceState(defenseMode)
                .eventType(EnemyDetected.class)
                .eventHandler(handleEnemy)
                .targetState(defenseMode)
                .build();

        Transition enemyLost = new TransitionBuilder()
                .name("enemyLost")
                .sourceState(defenseMode)
                .eventType(EnemyMissing.class)
                .eventHandler(buildEventHandler)
                .targetState(idle)
                .build();

        Transition enemyNotFound = new TransitionBuilder()
                .name("enemyNotFound")
                .sourceState(idle)
                .eventType(EnemyMissing.class)
                .eventHandler(buildEventHandler)
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