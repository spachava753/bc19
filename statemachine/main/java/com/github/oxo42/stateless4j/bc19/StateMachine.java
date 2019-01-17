package bc19;

import java.util.ArrayList;
import java.util.List;

/**
 * Models behaviour as transitions between a finite set of states
 *
 * @param <S> The type used to represent the states
 * @param <T> The type used to represent the triggers that cause state transitions
 */
public class StateMachine<S, T> {
    
    public static final String TRIGGER_IS_NULL = "trigger is null";
    protected final StateMachineConfig<S, T> config;
    protected final Func<S> stateAccessor;
    protected final StateAction1<S> stateMutator;
    //private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean shouldLog = true;

    protected StateAction3<S, T, Object[]> unhandledTriggerAction = new StateAction3<S, T, Object[]>() {
        @Override
        public void doIt(S state, T trigger, Object[] args) {
            throw new IllegalStateException(
                    String.format(
                            "No valid leaving transitions are permitted from state '%s' for trigger '%s'. Consider ignoring the trigger.",
                            state, trigger)
            );
        }
        
    };
    
    /**
     * Construct a state machine
     *
     * @param initialState The initial state
     */
    public StateMachine(S initialState) {
        this(initialState, new StateMachineConfig<S, T>());
    }
    
    /**
     * Construct a state machine
     *
     * @param initialState The initial state
     * @param config       State machine configuration
     */
    public StateMachine(S initialState, StateMachineConfig<S, T> config) {
        this.config = config;
        final StateReference<S, T> reference = new StateReference<>();
        reference.setState(initialState);
        stateAccessor = new Func<S>() {
            @Override
            public S call() {
                return reference.getState();
            }
        };
        stateMutator = new StateAction1<S>() {
            @Override
            public void doIt(S s) {
                reference.setState(s);
            }
        };
        if (config.isEntryActionOfInitialStateEnabled()) {
            Transition<S, T> initialTransition = new Transition(initialState, initialState, null);
            getCurrentRepresentation().enter(initialTransition);
        }
    }
    
    /**
     * Construct a state machine with external state storage.
     *
     * @param initialState  The initial state
     * @param stateAccessor State accessor
     * @param stateMutator  State mutator
     */
    public StateMachine(S initialState, Func<S> stateAccessor, StateAction1<S> stateMutator, StateMachineConfig<S, T> config) {
        this.config = config;
        this.stateAccessor = stateAccessor;
        this.stateMutator = stateMutator;
        stateMutator.doIt(initialState);
    }
    
    public StateConfiguration<S, T> configure(S state) {
        return config.configure(state);
    }
    
    public StateMachineConfig<S, T> configuration() {
        return config;
    }
    
    /**
     * The current state
     *
     * @return The current state
     */
    public S getState() {
        return stateAccessor.call();
    }
    
    private void setState(S value) {
        stateMutator.doIt(value);
    }
    
    public boolean getShouldLog() {
        return shouldLog;
    }
    
    public void setShouldLog(boolean enabled) {
        shouldLog = enabled;
    }
    
    protected void log(T trigger, Object... args) {
        Log.i("Firing " + trigger, args);
    }
    
    /**
     * The currently-permissible trigger values
     *
     * @return The currently-permissible trigger values
     */
    public List<T> getPermittedTriggers() {
        return getCurrentRepresentation().getPermittedTriggers();
    }
    
    StateRepresentation<S, T> getCurrentRepresentation() {
        StateRepresentation<S, T> representation = config.getRepresentation(getState());
        return representation == null ? new StateRepresentation<S, T>(getState()) : representation;
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked
     *
     * @param trigger The trigger to fire
     */
    public void fire(T trigger) {
        publicFire(trigger);
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param arg0    The first argument
     * @param <TArg0> Type of the first trigger argument
     */
    public <TArg0> void fire(TriggerWithParameters1<TArg0, T> trigger, TArg0 arg0) {
        assert trigger != null : TRIGGER_IS_NULL;
        publicFire(trigger.getTrigger(), arg0);
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param arg0    The first argument
     * @param arg1    The second argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     */
    public <TArg0, TArg1> void fire(TriggerWithParameters2<TArg0, TArg1, T> trigger, TArg0 arg0, TArg1 arg1) {
        assert trigger != null : TRIGGER_IS_NULL;
        publicFire(trigger.getTrigger(), arg0, arg1);
    }
    
    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked.
     *
     * @param trigger The trigger to fire
     * @param arg0    The first argument
     * @param arg1    The second argument
     * @param arg2    The third argument
     * @param <TArg0> Type of the first trigger argument
     * @param <TArg1> Type of the second trigger argument
     * @param <TArg2> Type of the third trigger argument
     */
    public <TArg0, TArg1, TArg2> void fire(TriggerWithParameters3<TArg0, TArg1, TArg2, T> trigger, TArg0 arg0, TArg1 arg1, TArg2 arg2) {
        assert trigger != null : TRIGGER_IS_NULL;
        publicFire(trigger.getTrigger(), arg0, arg1, arg2);
    }
    
    protected void publicFire(T trigger, Object... args) {
        if (shouldLog) {
            log(trigger, args);
        }
        TriggerWithParameters<T> configuration = config.getTriggerConfiguration(trigger);
        if (configuration != null) {
            configuration.validateParameters(args);
        }
        
        TriggerBehaviour<S, T> triggerBehaviour = getCurrentRepresentation().tryFindHandler(trigger);
        if (triggerBehaviour == null) {
            unhandledTriggerAction.doIt(getCurrentRepresentation().getUnderlyingState(), trigger, args);
            return;
        }
        
        if (triggerBehaviour.isInternal()) {
            triggerBehaviour.performAction(args);
        } else {
            S source = getState();
            S destination = triggerBehaviour.transitionsTo(source, args);
            Transition<S, T> transition = new Transition<>(source, destination, trigger);
            
            getCurrentRepresentation().exit(transition);
            triggerBehaviour.performAction(args);
            setState(destination);
            getCurrentRepresentation().enter(transition, args);
            if (shouldLog) {
                Log.d("Fired [{}]--{}-->[{}]",
                        source,
                        TriggerWithParameters.toString(trigger, args),
                        destination.toString());
            }
        }
    }
    
    /**
     * Override the default behaviour of throwing an exception when an unhandled trigger is fired
     *
     * @param unhandledTriggerAction An action to call when an unhandled trigger is fired
     */
    public void onUnhandledTrigger(final StateAction2<S, T> unhandledTriggerAction) {
        if (unhandledTriggerAction == null) {
            throw new IllegalStateException("unhandledTriggerAction");
        }
        this.unhandledTriggerAction = new StateAction3<S, T, Object[]>() {
            @Override
            public void doIt(S state, T trigger, Object[] arg3) {
                unhandledTriggerAction.doIt(state, trigger);
            }
        };
    }

    /**
     * Override the default behaviour of throwing an exception when an unhandled trigger is fired
     *
     * @param unhandledTriggerAction An action to call with state, trigger and params when an unhandled trigger is fired
     */
    public void onUnhandledTrigger(StateAction3<S, T, Object[]> unhandledTriggerAction) {
        if (unhandledTriggerAction == null) {
            throw new IllegalStateException("unhandledTriggerAction");
        }
        this.unhandledTriggerAction = unhandledTriggerAction;
    }

    /**
     * Determine if the state machine is in the supplied state
     *
     * @param state The state to test for
     * @return True if the current state is equal to, or a substate of, the supplied state
     */
    public boolean isInState(S state) {
        return getCurrentRepresentation().isIncludedIn(state);
    }
    
    /**
     * Returns true if {@code trigger} can be fired  in the current state
     *
     * @param trigger Trigger to test
     * @return True if the trigger can be fired, false otherwise
     */
    public boolean canFire(T trigger) {
        return getCurrentRepresentation().canHandle(trigger);
    }
    
    /**
     * A human-readable representation of the state machine
     *
     * @return A description of the current state and permitted triggers
     */
    @Override
    public String toString() {
        List<T> permittedTriggers = getPermittedTriggers();
        List<String> parameters = new ArrayList<>();
        
        for (T tTrigger : permittedTriggers) {
            parameters.add(tTrigger.toString());
        }
        
        StringBuilder params = new StringBuilder();
        String delim = "";
        for (String param : parameters) {
            params.append(delim);
            params.append(param);
            delim = ", ";
        }
        
        return String.format(
                "StateMachine {{ State = %s, PermittedTriggers = {{ %s }}}}",
                getState(),
                params.toString());
    }
}