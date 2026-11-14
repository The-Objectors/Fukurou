package com.fukurou;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"session", "compile", "lifecycle", "invocations", "editor_activity", "events"})
public class TelemetryData {
    private final CompileData compileData;
    private final LifecycleData lifecycleData;
    private final InvocationData invocationData;
    private final EventLog eventLog;
    private final SessionMetadata sessionMetadata;
    private final EditorData editorData;

    public TelemetryData(){
        compileData = new CompileData();
        lifecycleData = new LifecycleData();
        invocationData = new InvocationData();
        eventLog = new EventLog();
        sessionMetadata = new SessionMetadata();
        editorData = new EditorData();
    }

    public void updateSessionSnapshot(){
        sessionMetadata.updateSnapshot();
    }

    @JsonProperty("compile")
    public CompileData getCompileData(){return compileData;}

    @JsonProperty ("session")
    public SessionMetadata getSessionMetadata(){return sessionMetadata;}

    public void finishSession(){
        sessionMetadata.finish();
    }

    @JsonProperty("lifecycle")
    public LifecycleData getLifecycleData(){return lifecycleData;}

    @JsonProperty("invocations")
    public InvocationData getInvocationData(){return invocationData;}

    @JsonProperty("editor_activity")
    public EditorData getEditorData(){return editorData;}

    @JsonProperty("events")
    public EventLog getEventLog(){return eventLog;}

}
