package com.fukurou;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryData {
    private final CompileData compileData;
    private final LifecycleData lifecycleData;

    public TelemetryData(){
        compileData = new CompileData();
        lifecycleData = new LifecycleData();    
    }

    @JsonProperty("compile")
    public CompileData getCompileData(){return compileData;}

    @JsonProperty("lifecycle")
    public LifecycleData getLifecycleData(){return lifecycleData;}
}