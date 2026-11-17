package com.fukurou.telemetry;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Single list of every telemetry event.
 * Shared by all handlers so the list order matches the order events occurred.
 * Methods are synchronized because BlueJ may deliver events on different threads.
 */
public class EventLog {

    /** Recorded events, in the order they were added. */
    private final List<TelemetryEvent> events = new ArrayList<>();

    /**
     * Appends an event to the log.
     *
     * @param event the event to add
     */
    public synchronized void record(TelemetryEvent event) {
        events.add(event);
    }

    /**
     * Creates a {@link TelemetryEvent} timestamped now and appends it to the log.
     *
     * @param target what the event is about
     * @param type   what happened
     * @param project the project name, or {@code null}
     * @param name   the class, file or project name the event refers to, or {@code null}
     * @param detail extra information, or {@code null}
     */
    public void record(TelemetryEvent.Target target, TelemetryEvent.Type type,
                       String project, String name, String detail) {
        record(new TelemetryEvent(target, type, project, name, detail));
    }

    /**
     * Returns a copy of the recorded events. Jackson serialises the log as this list.
     *
     * @return a new list containing the events in the order they were recorded
     */
    @JsonValue
    public synchronized List<TelemetryEvent> getEvents() {
        return new ArrayList<>(events);
    }
}
