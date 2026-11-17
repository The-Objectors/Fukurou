package com.fukurou.telemetry;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Identifying and timing information for a telemetry session.
 * The start time is set when the object is created; the end time is set by
 * {@link #updateSnapshot()} or {@link #finish()}.
 */
@JsonPropertyOrder({
    "studentId",
    "startedAtUtc",
    "endedAtUtc"
})

public class SessionMetadata {
    /** Student identifier; currently a fixed placeholder value. */
    private final String studentId = "REPLACE_WITH_STUDENT_ID";
    /** Time this object was created. */
    private final Instant startTime = Instant.now();
    /** End time as an ISO-8601 UTC string, or {@code null} until first set. */
    private String endedAtUtc;
    /** Whether {@link #finish()} has been called; once true the end time no longer changes. */
    private boolean finished;

    /** Sets the end time to now, unless the session has already been finished. */
    public void updateSnapshot() {
        if (!finished) {
            calculateMetrics(Instant.now());
        }
    }

    /** Sets the end time to now and marks the session finished. Later calls have no effect. */
    public void finish() {
        if (!finished) {
            calculateMetrics(Instant.now());
            finished = true;
        }
    }

    /**
     * Stores the given end time.
     *
     * @param endTime the end time
     */
    private void calculateMetrics(Instant endTime) {
        endedAtUtc = endTime.toString();
    }

    /** @return the student identifier */
    public String getStudentId(){return studentId;}

    /** @return the start time as an ISO-8601 UTC string */
    public String getStartedAtUtc(){return startTime.toString();}

    /** @return the end time as an ISO-8601 UTC string, or {@code null} if not yet set */
    public String getEndedAtUtc(){return endedAtUtc;}

}
