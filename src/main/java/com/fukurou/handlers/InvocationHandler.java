package com.fukurou.handlers;

import com.fukurou.telemetry.EventLog;
import com.fukurou.telemetry.InvocationData;
import com.fukurou.telemetry.TelemetryEvent.Target;
import com.fukurou.telemetry.TelemetryEvent.Type;

import bluej.extensions2.BObject;
import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

/**
 * Observer of BlueJ method/constructor invocation results (Observer pattern):
 * registered via {@code addInvocationFinishedListener}, it classifies each exit
 * (normal, exception, user-terminated) into {@link InvocationData} and {@link EventLog}.
 */
public class InvocationHandler implements InvocationFinishedListener{

    /** Invocation counters updated by this handler. */
    private final InvocationData data;
    /** Event log that receives a {@link Target#EXECUTE} entry for each counted invocation. */
    private final EventLog log;

    /**
     * Creates a handler that writes to the given invocation data and event log.
     *
     * @param data the invocation counters to update
     * @param log  the event log to record invocation events in
     */
    public InvocationHandler(InvocationData data, EventLog log){
        this.data = data;
        this.log = log;
    }

    /**
     * Classifies a finished invocation by its exit type:
     * <ul>
     *   <li>normal exit: counted as an invocation and logged as {@link Type#SUCCESS},
     *       unless the result is a {@link BObject} (treated as a constructor call and ignored);</li>
     *   <li>exception exit: counted as a runtime exception and logged as {@link Type#FAIL};</li>
     *   <li>terminated exit: counted as a user termination and logged as {@link Type#FAIL}.</li>
     * </ul>
     *
     * @param ife the BlueJ invocation-finished event
     */
    @Override
    public void invocationFinished(InvocationFinishedEvent ife) {
        String className = ife.getClassName();
        Object result = safeResult(ife);
        boolean isConstructor = result instanceof BObject;

        switch (ife.getEventType()) {
            case NORMAL_EXIT:
                if (!isConstructor) {
                    data.invocationsIncrease();
                    log.record(Target.EXECUTE, Type.SUCCESS, null, className, null);
                }
                break;
            case EXCEPTION_EXIT:
                data.runtimeExceptionsIncrease();
                log.record(Target.EXECUTE, Type.FAIL, null, className, null);
                break;
            case TERMINATED_EXIT:
                data.userTerminationsIncrease();
                log.record(Target.EXECUTE, Type.FAIL, null, className, null);
                break;
            default:
                break;
        }
    }

    /**
     * Returns the invocation result without propagating runtime exceptions.
     *
     * @param ife the BlueJ invocation-finished event
     * @return the result, or {@code null} if reading it threw a {@link RuntimeException}
     */
    private static Object safeResult(InvocationFinishedEvent ife) {
        try {
            return ife.getResult();
        } catch (RuntimeException e) {
            return null;
        }
    }

}
