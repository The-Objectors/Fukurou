package com.fukurou;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "studentId",
    "startedAtUtc",
    "endedAtUtc",
    "totalCompilationAttempts"
})


public class SessionMetadata {
    private final String studentId = "REPLACE_WITH_STUDENT_ID";
    private final Instant startTime = Instant.now();
    private final CompileData compileData;
    private String endedAtUtc;
    private boolean finished;


    public SessionMetadata(CompileData compileData){
        this.compileData = compileData;
    }

    public void updateSnapshot(EventLog eventLog) {
        if (!finished) {
            calculateMetrics(Instant.now());
        }
    }

    public void finish(EventLog eventLog) {
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
    
    @JsonProperty("totalCompilationAttempts")
    public int getTotalCompilationAttempts(){
        return compileData.getCompileCount();
    }

}
