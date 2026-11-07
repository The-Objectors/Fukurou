package com.fukurou;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LifecycleEvent {

    public enum Type { CREATE, MODIFY, DELETE }
    public enum Target { CLASS }

    private final Type type;
    private final Target target;
    private final String name;
    private final String project;
    private final String timestamp;

    public LifecycleEvent(Type type, String project, String name) {
        this.type = type;
        this.target = Target.CLASS;
        this.project = project != null ? project : "unknown";
        this.name = name;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getType()      { return type.name().toLowerCase(); }
    public String getTarget()    { return target.name().toLowerCase(); }
    public String getName()      { return name; }
    public String getProject()   { return project; }
    public String getTimestamp() { return timestamp; }
}