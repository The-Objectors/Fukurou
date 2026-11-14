package com.fukurou;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "studentId",
    "startedAtUtc",
    "endedAtUtc"
})

public class SessionMetadata {
    private final String studentId = "REPLACE_WITH_STUDENT_ID";
    private final Instant startTime = Instant.now();
    private String endedAtUtc;
    private boolean finished;

    public void updateSnapshot() {
        if (!finished) {
            calculateMetrics(Instant.now());
        }
    }

    public void finish() {
        if (!finished) {
            calculateMetrics(Instant.now());
            finished = true;
        }
    }

    private void calculateMetrics(Instant endTime) {
        endedAtUtc = endTime.toString();
    }

    public String getStudentId(){return studentId;}
    public String getStartedAtUtc(){return startTime.toString();}
    public String getEndedAtUtc(){return endedAtUtc;}

}
