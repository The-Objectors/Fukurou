package com.fukurou;

import java.io.File;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.fukurou.TelemetryEvent.Target;
import com.fukurou.TelemetryEvent.Type;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

public class CompileHandler implements CompileListener{

    LocalTime startTime;
    long timeTaken;
    CompileData compileData;
    private final EventLog log;

    public CompileHandler(CompileData compileData, EventLog log){
        this.compileData = compileData;
        this.log = log;
    }


    @Override
    public void compileStarted(CompileEvent event){
        startTime = LocalTime.now();
        compileData.countIncrease();
        log.record(Target.COMPILE, Type.START, null, fileNames(event), null);
    }

    @Override
    public void compileSucceeded(CompileEvent event){
        timeTaken = timeBetween(startTime);
        compileData.successIncrease();
        log.record(Target.COMPILE, Type.SUCCESS, null, fileNames(event), timeTaken + "ms");
    }

    @Override
    public void compileFailed(CompileEvent event){
        timeTaken = timeBetween(startTime);
        compileData.failIncrease();
        log.record(Target.COMPILE, Type.FAIL, null, fileNames(event), timeTaken + "ms");
    }

    @Override
    public void compileError(CompileEvent event){
        compileData.errorIncrease();
        String message = event.getErrorMessage();
        if (message == null || message.trim().isEmpty()) 
            message = "(no error message)";
         else 
            message = message.trim();
        compileData.addErrorInfo(message);
        log.record(Target.COMPILE, Type.ERROR, null, fileNames(event),
                "Line " + event.getErrorLineNumber() + ": " + event.getErrorMessage());
    }

    @Override
    public void compileWarning(CompileEvent event){
        compileData.warnIncrease();
        log.record(Target.COMPILE, Type.WARNING, null, fileNames(event), event.getErrorMessage());
    }

    private long timeBetween(LocalTime start){
        if (start == null) return 0;
        LocalTime end = LocalTime.now();
        return ChronoUnit.MILLIS.between(start, end);
    }

    /** Comma-separated names of the files in this compile event, or null if none. */
    private String fileNames(CompileEvent event){
        File[] files = event.getFiles();

        if (files == null || files.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        
        for (File f : files) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(f.getName());
        }

        return sb.toString();
    }
}
