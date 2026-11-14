package com.fukurou;

public class InvocationData {
    private int invocations;
    private int runtimeExceptions;
    private int userTerminations;

    public InvocationData(){
        invocations = 0;
        runtimeExceptions = 0;
        userTerminations = 0;
    }

    /** Number of invocation outcomes recorded; normal constructor exits are excluded. */
    public int getInvocations() {return invocations;}
    public int getRuntimeExceptions() {return runtimeExceptions;}
    public int getUserTerminations() {return userTerminations;}
    

    /** Increments the total count for a normal, non-constructor exit. */
    public void invocationsIncrease(){invocations++;}

    /** Records a runtime-exception exit and increments the total invocation count. */
    public void runtimeExceptionsIncrease(){
        invocationsIncrease();
        runtimeExceptions++;
    }

    /** Records a user-terminated exit and increments the total invocation count. */
    public void userTerminationsIncrease(){
        invocationsIncrease();
        userTerminations++;
    }
}
