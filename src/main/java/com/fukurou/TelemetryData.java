package com.fukurou;

public class TelemetryData {
    private CompileData compileData;

    public TelemetryData(){
        compileData = new CompileData();
    }

    //Getter
    public CompileData getCompileData(){return compileData;}
}
