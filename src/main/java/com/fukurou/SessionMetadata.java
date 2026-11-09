package com.fukurou;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "studentId",
    "startedAtUtc",
    "endedAtUtc",
    "totalDurationMillis",
    "totalCompilationAttempts"
})


public class SessionMetadata {
    private final String studentId = "REPLACE_WITH_STUDENT_ID";
    private final Instant startTime = Instant.now();
    private final CompileData compileData;
    private final long ACTIVE_GAP_LIMIT_MILLIS = Duration.ofMinutes(5).toMillis();
    private String endedAtUtc;
    private long totalDuration;
    private long activeTimeMillis;
    private long idleTimeMillis;
    private boolean finished;


    public SessionMetadata(CompileData compileData){
        this.compileData = compileData;
    }

    public void updateSnapshot(EventLog eventLog) {
        if (!finished) {
            calculateMetrics(eventLog, Instant.now());
        }
    }

    public void finish(EventLog eventLog) {
        if (!finished) {
            calculateMetrics(eventLog, Instant.now());
            finished = true;
        }
    }

    private void calculateMetrics(EventLog eventLog, Instant endTime) {
        endedAtUtc = endTime.toString();
        totalDuration = Duration.between(startTime, endTime).toMillis();

        List<Instant> eventTimes = new ArrayList<>();
        for (TelemetryEvent event : eventLog.getEvents()) {
            Instant eventTime = Instant.parse(event.getTimestamp());
            if (!eventTime.isBefore(startTime) && !eventTime.isAfter(endTime)) {
                eventTimes.add(eventTime);
            }
        }
        Collections.sort(eventTimes);

        long activeMillis = 0;
        Instant previousTime = startTime;
        for (Instant eventTime : eventTimes) {
            long gapMillis = Duration.between(previousTime, eventTime).toMillis();
            activeMillis += Math.min(gapMillis, ACTIVE_GAP_LIMIT_MILLIS);
            previousTime = eventTime;
        }

        long finalGapMillis = Duration.between(previousTime, endTime).toMillis();
        activeMillis += Math.min(finalGapMillis, ACTIVE_GAP_LIMIT_MILLIS);

        activeTimeMillis = Math.min(activeMillis, totalDuration);
        idleTimeMillis = totalDuration - activeTimeMillis;
    }
    public String getStudentId(){return studentId;}
    public String getStartedAtUtc(){return startTime.toString();}
    public String getEndedAtUtc(){return endedAtUtc;}
    public long getTotalDuration(){return totalDuration;}
    public long getActiveTimeMillis(){return activeTimeMillis;}
    public long getIdleTimeMillis(){return idleTimeMillis;}
    
    @JsonProperty("totalCompilationAttempts")
    public int getTotalCompilationAttempts(){
        return compileData.getCompileCount();
    }

}
