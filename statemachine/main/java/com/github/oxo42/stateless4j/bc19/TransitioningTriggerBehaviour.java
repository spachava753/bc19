package bc19;

public class TransitioningTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {

    private final S destination;
    private final StateAction stateAction;

    public TransitioningTriggerBehaviour(T trigger, S destination, FuncBoolean guard, StateAction stateAction) {
        super(trigger, guard);
        this.destination = destination;
        this.stateAction = stateAction;
    }
    
    @Override
    public void performAction(Object[] args) {
        stateAction.doIt();
    }

    @Override
    public S transitionsTo(S source, Object[] args) {
        return destination;
    }
}
