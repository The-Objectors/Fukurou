package com.fukurou;

import com.fukurou.TelemetryEvent.Target;
import com.fukurou.TelemetryEvent.Type;

import bluej.extensions2.BObject;
import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedListener;

public class InvocationHandler implements InvocationFinishedListener{

    private final InvocationData data;
    private final EventLog log;

    public InvocationHandler(InvocationData data, EventLog log){
        this.data = data;
        this.log = log;
    }

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

    private static Object safeResult(InvocationFinishedEvent ife) {
        try {
            return ife.getResult();
        } catch (RuntimeException e) {
            return null;
        }
    }
    
}
