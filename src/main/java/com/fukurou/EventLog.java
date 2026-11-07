package com.fukurou;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Single list of every telemetry event.
 * Shared by all handlers so the list order matches the order events occurred.
 * Methods are synchronized because BlueJ may deliver events on different threads.
 */
public class EventLog {

    private final List<TelemetryEvent> events = new ArrayList<>();

    public synchronized void record(TelemetryEvent event) {
        events.add(event);
    }

    public void record(TelemetryEvent.Target target, TelemetryEvent.Type type,
                       String project, String name, String detail) {
        record(new TelemetryEvent(target, type, project, name, detail));
    }

    @JsonValue
    public synchronized List<TelemetryEvent> getEvents() {
        return new ArrayList<>(events);
    }
}
