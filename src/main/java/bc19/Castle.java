package bc19;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Castle extends RobotType {

    private int pilgrimsBuilt;
    private int crusadersBuilt;
    private int numOfDeposits;

    private boolean builtChainPilgrim = false;

    private List<Robot> pilgrimRobots;
    private List<Robot> crusaderRobots;
    private List<Robot> prophetRobots;
    private List<Robot> castleRobots;
    private List<Robot> churchRobots;

    private FiniteStateMachine castleStateMachine;
    private int[] enemyRobotLoc = null;
    private Action action;

    public Castle(BCAbstractRobot robot) {
        super(robot);
    }

    @Override
    public void initialize() {
        super.initialize();
        setminKarbStockpile(50);
        setminFuelStockpile(100);
        numOfDeposits = getDeposits(getFullMap()).size() / 2;

        State idle = new State("idle");
        State defenseMode = new State("defenseMode");
        State buildMode = new State("buildMode");

        Set<State> states = new HashSet<>();
        states.add(idle);
        states.add(defenseMode);
        states.add(buildMode);

        EventHandler enemyEventHandler = event -> {
            Log.d("ENEMY DETECTED");
            if (enemyRobotLoc != null) {
                if(canBuildUnitWithResources(robot.SPECS.PROPHET)){
                    int[] goalDir = RobotUtil.getDir(robot.me.x, robot.me.y, enemyRobotLoc[0], enemyRobotLoc[1]);
                    action = build(robot.SPECS.PROPHET, goalDir[0], goalDir[1]);

                    if (action == null){
                        Log.i("COULDN'T BUILD PROPHET");
                        // try to attack
                        action = robot.attack(enemyRobotLoc[0] - robot.me.x, enemyRobotLoc[1] - robot.me.y);
                    } else {
                        Log.i("BUILT PROPHET");
                        crusadersBuilt++;
                    }
                } else {
                    Log.i("NOT ENOUGH RESOURCES TO BUILD PROPHETS, ATTACKING DIRECTLY");
                    int dx = enemyRobotLoc[0] - robot.me.x;
                    int dy = enemyRobotLoc[1] - robot.me.y;
                    action = robot.attack(dx, dy);
                }
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

                    return;
                }
            }

            if(robot.karbonite > getminKarbStockpile() && robot.fuel > getminFuelStockpile()){
                // occasionally spawn a few crusaders and prophets with some pilgrims just in case some die in battle
                if (robot.me.turn % 90 == 0 && canBuildUnitWithResources(robot.SPECS.PROPHET)){
                    Log.i("BUILDING NEW PROPHET");
                    action = tryAction(20, () -> {
                        int[] randDir = RobotUtil.getRandomDir();
                        return build(robot.SPECS.PROPHET, randDir[0], randDir[1]);
                    });
                } else if(robot.me.turn % 80 == 0 && canBuildUnitWithResources(robot.SPECS.CRUSADER)){
                    Log.i("BUILDING NEW CRUSADER");
                    action = tryAction(20, () -> {
                        int[] randDir = RobotUtil.getRandomDir();
                        return build(robot.SPECS.CRUSADER, randDir[0], randDir[1]);
                    });
                } else if(robot.me.turn % 100 == 0 && canBuildUnitWithResources(robot.SPECS.PILGRIM)){
                    Log.i("BUILDING NEW PILGRIM");
                    action = tryAction(20, () -> {
                        int[] randDir = RobotUtil.getRandomDir();
                        return build(robot.SPECS.PILGRIM, randDir[0], randDir[1]);
                    });
                }
            }
        };

        Transition idleToDefenceMode = new TransitionBuilder()
                .name("idleToDefenceMode")
                .sourceState(idle)
                .eventType(EnemyDetected.class)
                .eventHandler(enemyEventHandler)
                .targetState(defenseMode)
                .build();

        Transition idleToBuildMode = new TransitionBuilder()
                .name("idleToBuildMode")
                .sourceState(idle)
                .eventType(EnemyMissing.class)
                .eventHandler(buildEventHandler)
                .targetState(buildMode)
                .build();

        Transition buildToDefenceMode = new TransitionBuilder()
                .name("buildToDefenceMode")
                .sourceState(buildMode)
                .eventType(EnemyDetected.class)
                .eventHandler(enemyEventHandler)
                .targetState(defenseMode)
                .build();

        Transition enemyLocked = new TransitionBuilder()
                .name("enemyLocked")
                .sourceState(defenseMode)
                .eventType(EnemyDetected.class)
                .eventHandler(enemyEventHandler)
                .targetState(defenseMode)
                .build();

        Transition defenceToBuildMode = new TransitionBuilder()
                .name("defenceToBuildMode")
                .sourceState(defenseMode)
                .eventType(EnemyMissing.class)
                .eventHandler(buildEventHandler)
                .targetState(buildMode)
                .build();

        Transition continueBuilding = new TransitionBuilder()
                .name("continueBuilding")
                .sourceState(buildMode)
                .eventType(EnemyMissing.class)
                .eventHandler(buildEventHandler)
                .targetState(buildMode)
                .build();

        castleStateMachine = new FiniteStateMachineBuilder(states, idle)
                .registerTransition(idleToDefenceMode)
                .registerTransition(buildToDefenceMode)
                .registerTransition(idleToBuildMode)
                .registerTransition(continueBuilding)
                .registerTransition(enemyLocked)
                .registerTransition(defenceToBuildMode)
                .build();
    }

    @Override
    public void initTakeTurn() {
        super.initTakeTurn();
        Log.i("NUM OF DEPOSITS: ", numOfDeposits);
        enemyRobotLoc = null;
        action = null;


        pilgrimRobots = new ArrayList<>(50);
        crusaderRobots = new ArrayList<>(50);
        castleRobots = new ArrayList<>(50);
        churchRobots = new ArrayList<>(50);
        prophetRobots = new ArrayList<>(50);
        for(int i = 0; i < 4096; i++){
            if(i == robot.id)
                continue;
            Robot retrievedRobot = robot.getRobot(i);
            if(retrievedRobot != null){
                Log.d("Added robot", i, "to the list");
                if(retrievedRobot.unit == robot.SPECS.PILGRIM){
                    pilgrimRobots.add(retrievedRobot);
                } else if(retrievedRobot.unit == robot.SPECS.CRUSADER){
                    crusaderRobots.add(retrievedRobot);
                } else if(retrievedRobot.unit == robot.SPECS.PROPHET){
                    prophetRobots.add(retrievedRobot);
                } else if(retrievedRobot.unit == robot.SPECS.CASTLE){
                    castleRobots.add(retrievedRobot);
                } else if(retrievedRobot.unit == robot.SPECS.CHURCH){
                    churchRobots.add(retrievedRobot);
                }
            }
        }

        Log.i("PILGRIMS BUILT:", pilgrimRobots.size());
        Log.i("CRUSADERS BUILT:", crusaderRobots.size());
        Log.i("PROPHETS BUILT:", prophetRobots.size());
        Log.i("CHURCHES BUILT:", churchRobots.size());
        Log.i("NUM OF CASTLES:", castleRobots.size());
    }

    @Override
    public Action takeTurn() {
        /*
        //prioritize building crusaders if there is an enemy within our vision radius
        Robot enemyRobot = null;
        for (Robot visibleRobot : robot.getVisibleRobots()) {
            if (visibleRobot.team != robot.me.team) {
                enemyRobot = visibleRobot;
                break;
            }
        }


        // check if a deposit is in one of our adjacent squares
        List<int[]> tileDir = RobotUtil.getAdjacentTilesWithDeposits(robot, getFullMap());


        // defensive measures
        if (enemyRobot != null) {
            //Log.i("FOUND AN ENEMY ROBOT");
            if (robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_FUEL) {
                int[] goalDir = RobotUtil.getDir(robot.me.x, robot.me.y, enemyRobot.x, enemyRobot.y);
                action = build(Constants.CRUSADER_UNIT, goalDir[0], goalDir[1]);

                if (action == null) {
                    //Log.i("COULDN'T BUILD CRUSADER");
                }
            }
        } else if (!tileDir.isEmpty() && (tileDir.size() > pilgrimsBuilt)) {
            //Log.i("ONE OF THE ADJACENT TILES HAS A DEPOSIT");

            for (int[] direction : tileDir) {
                action = build(robot.SPECS.PILGRIM, direction[0], direction[1]);
                if (action != null) {
                    pilgrimsBuilt++;
                    break;
                }
            }
        } else if (numOfDeposits > pilgrimsBuilt && canBuildUnitWithResources(robot.SPECS.PILGRIM)) {
            // number of times to retry building
            Log.i("BUILDING NEW PILGRIM");
            action = tryAction(20, () -> {
                int[] randDir = RobotUtil.getRandomDir();
                return build(robot.SPECS.PILGRIM, randDir[0], randDir[1]);
            });
            if (action != null) {
                pilgrimsBuilt++;
            }
        } else {
            if (crusadersBuilt > 20) {
                action = null;
            } else if (robot.karbonite > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[robot.SPECS.CRUSADER].CONSTRUCTION_KARBONITE) {
                action = tryAction(20, () -> {
                    int[] randDir = RobotUtil.getRandomDir();
                    return build(robot.SPECS.CRUSADER, randDir[0], randDir[1]);
                });

                if (action == null) {
                    Log.i("COULDN'T BUILD CRUSADER");
                } else {
                    crusadersBuilt++;
                }
            }
        }

        return action;
        */

        // check if enemies have been spotted
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
                Log.d("FIRING ENEMY DETECTED EVENT");
                castleStateMachine.fire(new EnemyDetected());
            } catch (FiniteStateMachineException e) {
                Log.e(e);
            }
        } else {
            try {
                Log.d("FIRING ENEMY MISSING EVENT");
                castleStateMachine.fire(new EnemyMissing());
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