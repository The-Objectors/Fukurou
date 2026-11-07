package com.fukurou;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LifecycleEvent {

    public enum Type { CREATE, MODIFY, DELETE }
    public enum Target { PROJECT, CLASS }

    private final Type type;
    private final Target target;
    private final String name;
    private final String timestamp;

    public LifecycleEvent(Type type, Target target, String name) {
        this.type = type;
        this.target = target;
        this.name = name;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getType()      { return type.name().toLowerCase(); }
    public String getTarget()    { return target.name().toLowerCase(); }
    public String getName()      { return name; }
    public String getTimestamp() { return timestamp; }
}