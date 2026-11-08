package com.fukurou;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A single timestamped telemetry event (compile, class lifecycle, or project).
 * All events share one shape so they can be stored in a single ordered {@link EventLog}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "target", "name", "project", "detail", "timestamp"})
public class TelemetryEvent {

    public enum Type { CREATE, MODIFY, DELETE, OPEN, CLOSE, START, SUCCESS, FAIL, ERROR, WARNING }
    public enum Target { CLASS, PROJECT, COMPILE }

    private final Type type;
    private final Target target;
    private final String name;
    private final String project;
    private final String detail;
    private final Instant timestamp;

    public TelemetryEvent(Target target, Type type, String project, String name, String detail) {
        this.target = target;
        this.type = type;
        this.project = project;
        this.name = name;
        this.detail = detail;
        this.timestamp = Instant.now();
    }

    public String getType()      { return type.name().toLowerCase(); }
    public String getTarget()    { return target.name().toLowerCase(); }
    public String getName()      { return name; }
    public String getProject()   { return project; }
    public String getDetail()    { return detail; }
    public String getTimestamp() { return timestamp.toString(); }
}
