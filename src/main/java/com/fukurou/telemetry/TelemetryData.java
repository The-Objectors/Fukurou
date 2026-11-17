package com.fukurou.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Aggregates the per-category telemetry models behind a single object.
 *
 * <p><b>Design pattern: Facade.</b> Handlers and generators interact with this one
 * type instead of wiring up {@link CompileData}, {@link EditorData},
 * {@link InvocationData}, {@link LifecycleData}, {@link SessionMetadata} and
 * {@link EventLog} individually. It's also the root object Jackson serialises to JSON.
 */
@JsonPropertyOrder({"session", "compile", "lifecycle", "invocations", "editor_activity", "events"})
public class TelemetryData {
    /** Compilation counters. */
    private final CompileData compileData;
    /** Project and class lifecycle counters. */
    private final LifecycleData lifecycleData;
    /** Invocation counters. */
    private final InvocationData invocationData;
    /** Ordered log of all events. */
    private final EventLog eventLog;
    /** Session identification and timing. */
    private final SessionMetadata sessionMetadata;
    /** Editor typing metrics. */
    private final EditorData editorData;

    /** Creates new, empty instances of each telemetry model; the session start time is set now. */
    public TelemetryData(){
        compileData = new CompileData();
        lifecycleData = new LifecycleData();
        invocationData = new InvocationData();
        eventLog = new EventLog();
        sessionMetadata = new SessionMetadata();
        editorData = new EditorData();
    }

    /** Sets the session end time to now. See {@link SessionMetadata#updateSnapshot()}. */
    public void updateSessionSnapshot(){
        sessionMetadata.updateSnapshot();
    }

    /** @return the compilation counters (JSON key {@code "compile"}) */
    @JsonProperty("compile")
    public CompileData getCompileData(){return compileData;}

    /** @return the session metadata (JSON key {@code "session"}) */
    @JsonProperty ("session")
    public SessionMetadata getSessionMetadata(){return sessionMetadata;}

    /** Sets the final session end time. See {@link SessionMetadata#finish()}. */
    public void finishSession(){
        sessionMetadata.finish();
    }

    /** @return the lifecycle counters (JSON key {@code "lifecycle"}) */
    @JsonProperty("lifecycle")
    public LifecycleData getLifecycleData(){return lifecycleData;}

    /** @return the invocation counters (JSON key {@code "invocations"}) */
    @JsonProperty("invocations")
    public InvocationData getInvocationData(){return invocationData;}

    /** @return the editor typing metrics (JSON key {@code "editor_activity"}) */
    @JsonProperty("editor_activity")
    public EditorData getEditorData(){return editorData;}

    /** @return the event log (JSON key {@code "events"}) */
    @JsonProperty("events")
    public EventLog getEventLog(){return eventLog;}

}
