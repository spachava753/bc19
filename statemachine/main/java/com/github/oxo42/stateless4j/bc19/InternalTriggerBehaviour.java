package bc19;

import bc19.FuncBoolean;
import bc19.StateAction;

public class InternalTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {
    private final StateAction stateAction;

    public InternalTriggerBehaviour(T trigger, FuncBoolean guard, StateAction stateAction) {
        super(trigger, guard);
        this.stateAction = stateAction;
    }
    
    @Override
    public void performAction(Object[] args) {
        stateAction.doIt();
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public S transitionsTo(S source, Object[] args) {
        return source;
    }
}
