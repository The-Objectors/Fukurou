package com.fukurou.telemetry;

/**
 * Counters for method invocations run from BlueJ in a session.
 * Updated by {@link com.fukurou.handlers.InvocationHandler}.
 */
public class InvocationData {
    /** Total number of invocations counted, including those that threw or were terminated. */
    private int invocations;
    /** Number of invocations that ended with an exception. */
    private int runtimeExceptions;
    /** Number of invocations terminated by the user. */
    private int userTerminations;

    /** Creates invocation data with all counts at zero. */
    public InvocationData(){
        invocations = 0;
        runtimeExceptions = 0;
        userTerminations = 0;
    }

    /** @return the total number of invocations counted */
    public int getInvocations() {return invocations;}

    /** @return the number of invocations that ended with an exception */
    public int getRuntimeExceptions() {return runtimeExceptions;}

    /** @return the number of invocations terminated by the user */
    public int getUserTerminations() {return userTerminations;}

    /** Increments the total invocation count. */
    public void invocationsIncrease(){invocations++;}

    /** Increments both the total invocation count and the runtime exception count. */
    public void runtimeExceptionsIncrease(){
        invocationsIncrease();
        runtimeExceptions++;
    }

    /** Increments both the total invocation count and the user termination count. */
    public void userTerminationsIncrease(){
        invocationsIncrease();
        userTerminations++;
    }
}
