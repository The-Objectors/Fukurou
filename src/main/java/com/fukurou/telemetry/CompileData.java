package com.fukurou.telemetry;

import java.util.ArrayList;

/**
 * Counters and error messages for BlueJ compilations in a session.
 * Updated by {@link com.fukurou.handlers.CompileHandler}.
 */
public class CompileData {
    /** Number of compiles started. */
    private int compileCount;
    /** Number of compiles that succeeded. */
    private int compileSuccess;
    /** Number of compiles that failed. */
    private int compileFail;
    /** Number of compile errors reported. */
    private int compileError;
    /** Number of compile warnings reported. */
    private int compileWarn;
    /** Compile error messages, in the order they were reported. */
    private final ArrayList<String> errorInfo;

    /** Creates compile data with all counts at zero and no error messages. */
    public CompileData(){
        compileCount = 0;
        compileSuccess = 0;
        compileFail = 0;
        compileError = 0;
        compileWarn = 0;
        errorInfo = new ArrayList<>();
    }

    //Getters

    /** @return the number of compiles started */
    public int getCompileCount(){return compileCount;}

    /** @return the number of compiles that succeeded */
    public int getCompileSuccess(){return compileSuccess;}

    /** @return the number of compiles that failed */
    public int getCompileFail(){return compileFail;}

    /** @return the number of compile errors reported */
    public int getCompileError(){return compileError;}

    /** @return the number of compile warnings reported */
    public int getCompileWarn(){return compileWarn;}

    /** @return the error messages in the order they were added (the internal list, not a copy) */
    public ArrayList<String> getErrorInfo(){return errorInfo;}

    //Count incrementors

    /** Increments the number of compiles started. */
    public void countIncrease(){this.compileCount++;}

    /** Increments the number of successful compiles. */
    public void successIncrease(){this.compileSuccess++;}

    /** Increments the number of failed compiles. */
    public void failIncrease(){this.compileFail++;}

    /** Increments the number of compile errors. */
    public void errorIncrease(){this.compileError++;}

    /** Increments the number of compile warnings. */
    public void warnIncrease(){this.compileWarn++;}

    /**
     * Adds a compile error message.
     *
     * @param e the error message
     */
    public void addErrorInfo(String e){errorInfo.add(e);}
}
