package com.fukurou;

import java.util.ArrayList;

public class CompileData {
    private int compileCount;
    private int compileSuccess;
    private int compileFail;
    private int compileError;
    private int compileWarn;
    private ArrayList<String> errorInfo;

    public CompileData(){
        compileCount = 0;
        compileSuccess = 0;
        compileFail = 0;
        compileError = 0;
        compileWarn = 0;
        errorInfo = new ArrayList<>();
    }

    //Getters
    public int getCompileCount(){return compileCount;}
    public int getCompileSuccess(){return compileSuccess;}
    public int getCompileFail(){return compileFail;}
    public int getCompileError(){return compileError;}
    public int getCompileWarn(){return compileWarn;}
    public ArrayList<String> getErrorInfo(){return errorInfo;}

    //Count incrementors
    public void countIncrease(){this.compileCount++;}
    public void successIncrease(){this.compileSuccess++;}
    public void failIncrease(){this.compileFail++;}
    public void errorIncrease(){this.compileError++;}
    public void warnIncrease(){this.compileWarn++;}
    public void addErrorInfo(String e){errorInfo.add(e);}
}
