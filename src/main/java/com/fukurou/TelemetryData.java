package com.fukurou;

public class TelemetryData {
    private final CompileData compileData;
    private final LifecycleData lifecycleData;

    public TelemetryData(){
        compileData = new CompileData();
        lifecycleData = new LifecycleData();    
    }

    //Getter
    public CompileData getCompileData(){return compileData;}
    public LifecycleData getLifecycleData(){return lifecycleData;}
}
