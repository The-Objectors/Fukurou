package com.fukurou.telemetry;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A single timestamped telemetry event (compile, class lifecycle, project, or execution).
 * All events share one shape so they can be stored in a single ordered {@link EventLog}.
 * Fields that are {@code null} are left out of the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "target", "name", "project", "detail", "timestamp"})
public class TelemetryEvent {

    /** What happened. */
    public enum Type { CREATE, MODIFY, DELETE, OPEN, CLOSE, START, SUCCESS, FAIL, ERROR, WARNING }

    /** What the event is about. */
    public enum Target { CLASS, PROJECT, COMPILE, EXECUTE }

    /** What happened. */
    private final Type type;
    /** What the event is about. */
    private final Target target;
    /** Class, file or project name the event refers to; may be {@code null}. */
    private final String name;
    /** Project name; may be {@code null}. */
    private final String project;
    /** Extra information; may be {@code null}. */
    private final String detail;
    /** Time the event object was created. */
    private final Instant timestamp;

    /**
     * Creates an event timestamped with the current time.
     *
     * @param target  what the event is about
     * @param type    what happened
     * @param project the project name, or {@code null}
     * @param name    the class, file or project name the event refers to, or {@code null}
     * @param detail  extra information, or {@code null}
     */
    public TelemetryEvent(Target target, Type type, String project, String name, String detail) {
        this.target = target;
        this.type = type;
        this.project = project;
        this.name = name;
        this.detail = detail;
        this.timestamp = Instant.now();
    }

    /** @return the event type in lower case, e.g. {@code "success"} */
    public String getType()      { return type.name().toLowerCase(); }

    /** @return the event target in lower case, e.g. {@code "compile"} */
    public String getTarget()    { return target.name().toLowerCase(); }

    /** @return the name the event refers to, or {@code null} */
    public String getName()      { return name; }

    /** @return the project name, or {@code null} */
    public String getProject()   { return project; }

    /** @return the extra information, or {@code null} */
    public String getDetail()    { return detail; }

    /** @return the creation time as an ISO-8601 UTC string */
    public String getTimestamp() { return timestamp.toString(); }
}
