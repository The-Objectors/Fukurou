package com.fukurou;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"compile", "lifecycle", "events"})
public class TelemetryData {
    private final CompileData compileData;
    private final LifecycleData lifecycleData;
    private final EventLog eventLog;

    public TelemetryData(){
        compileData = new CompileData();
        lifecycleData = new LifecycleData();
        eventLog = new EventLog();
    }

    @JsonProperty("compile")
    public CompileData getCompileData(){return compileData;}

    @JsonProperty("lifecycle")
    public LifecycleData getLifecycleData(){return lifecycleData;}

    @JsonProperty("events")
    public EventLog getEventLog(){return eventLog;}
}
