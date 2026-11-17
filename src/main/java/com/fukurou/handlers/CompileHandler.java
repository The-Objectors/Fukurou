package com.fukurou.handlers;

import java.io.File;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.fukurou.telemetry.CompileData;
import com.fukurou.telemetry.EventLog;
import com.fukurou.telemetry.TelemetryEvent.Target;
import com.fukurou.telemetry.TelemetryEvent.Type;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

/**
 * Observer of BlueJ compilation events (Observer pattern): registered via
 * {@code addCompileListener}, it updates {@link CompileData} and {@link EventLog}
 * as compiles start, succeed, fail, error or warn.
 */
public class CompileHandler implements CompileListener{

    /** Time the most recent compile started; {@code null} until the first compile. */
    private LocalTime startTime;
    /** Duration in milliseconds of the most recently finished compile. */
    private long timeTaken;
    /** Compile counters and error messages updated by this handler. */
    private final CompileData compileData;
    /** Event log that receives a {@link Target#COMPILE} entry for each callback. */
    private final EventLog log;

    /**
     * Creates a handler that writes to the given compile data and event log.
     *
     * @param compileData the compile counters to update
     * @param log         the event log to record compile events in
     */
    public CompileHandler(CompileData compileData, EventLog log){
        this.compileData = compileData;
        this.log = log;
    }


    /**
     * Notes the start time, increments the compile count and records a
     * {@link Type#START} event naming the files being compiled.
     *
     * @param event the BlueJ compile event
     */
    @Override
    public void compileStarted(CompileEvent event){
        startTime = LocalTime.now();
        compileData.countIncrease();
        log.record(Target.COMPILE, Type.START, null, fileNames(event), null);
    }

    /**
     * Increments the success count and records a {@link Type#SUCCESS} event
     * with the time elapsed since the compile started.
     *
     * @param event the BlueJ compile event
     */
    @Override
    public void compileSucceeded(CompileEvent event){
        timeTaken = timeBetween(startTime);
        compileData.successIncrease();
        log.record(Target.COMPILE, Type.SUCCESS, null, fileNames(event), timeTaken + "ms");
    }

    /**
     * Increments the failure count and records a {@link Type#FAIL} event
     * with the time elapsed since the compile started.
     *
     * @param event the BlueJ compile event
     */
    @Override
    public void compileFailed(CompileEvent event){
        timeTaken = timeBetween(startTime);
        compileData.failIncrease();
        log.record(Target.COMPILE, Type.FAIL, null, fileNames(event), timeTaken + "ms");
    }

    /**
     * Increments the error count, stores the trimmed error message in
     * {@link CompileData} (or {@code "(no error message)"} if it is blank) and
     * records a {@link Type#ERROR} event with the line number and message.
     *
     * @param event the BlueJ compile event carrying the error details
     */
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

    /**
     * Increments the warning count and records a {@link Type#WARNING} event
     * with the warning message.
     *
     * @param event the BlueJ compile event carrying the warning details
     */
    @Override
    public void compileWarning(CompileEvent event){
        compileData.warnIncrease();
        log.record(Target.COMPILE, Type.WARNING, null, fileNames(event), event.getErrorMessage());
    }

    /**
     * Milliseconds from {@code start} to now.
     *
     * @param start the start time, may be {@code null}
     * @return elapsed milliseconds, or 0 if {@code start} is {@code null}
     */
    private long timeBetween(LocalTime start){
        if (start == null) return 0;
        LocalTime end = LocalTime.now();
        return ChronoUnit.MILLIS.between(start, end);
    }

    /**
     * Comma-separated names of the files in a compile event.
     *
     * @param event the BlueJ compile event
     * @return the file names, or {@code null} if the event has no files
     */
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
