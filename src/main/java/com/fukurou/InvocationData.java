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

    public int getInvocations() {return invocations;}
    public int getRuntimeExceptions() {return runtimeExceptions;}
    public int getUserTerminations() {return userTerminations;}
    

    public void invocationsIncrease(){invocations++;}
    public void runtimeExceptionsIncrease(){
        invocationsIncrease();
        runtimeExceptions++;
    }
    public void userTerminationsIncrease(){
        invocationsIncrease();
        userTerminations++;
    }
}
