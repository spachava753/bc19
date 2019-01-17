package bc19;

import bc19.Func2;
import bc19.FuncBoolean;
import bc19.StateAction1;

public class DynamicTriggerBehaviour<S, T> extends TriggerBehaviour<S, T> {

    private final Func2<Object[], S> destination;
    private final StateAction1<Object[]> action;

    public DynamicTriggerBehaviour(T trigger, Func2<Object[], S> destination, FuncBoolean guard, StateAction1<Object[]> action) {
        super(trigger, guard);
        assert destination != null : "destination is null";
        this.destination = destination;
        this.action = action;
    }
    
    @Override
    public void performAction(Object[] args) {
    	action.doIt(args);
    }

    @Override
    public S transitionsTo(S source, Object[] args) {
        return destination.call(args);
    }
}
