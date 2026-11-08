package com.fukurou;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"compile", "lifecycle", "events"})
public class TelemetryData {
    private final CompileData compileData;
    private final LifecycleData lifecycleData;
    private final EventLog eventLog;
    private final SessionMetadata sessionMetadata;

    public TelemetryData(){
        compileData = new CompileData();
        lifecycleData = new LifecycleData();
        eventLog = new EventLog();
        sessionMetadata = new SessionMetadata(compileData);
    }

    @JsonProperty("compile")
    public CompileData getCompileData(){return compileData;}

    @JsonProperty ("session")
    public SessionMetadata getSessionMetadata(){return sessionMetadata;}

    public void finishSession(){
        sessionMetadata.finish(eventLog);
    }

    @JsonProperty("lifecycle")
    public LifecycleData getLifecycleData(){return lifecycleData;}

    @JsonProperty("events")
    public EventLog getEventLog(){return eventLog;}
}
