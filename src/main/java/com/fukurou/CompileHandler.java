package com.fukurou;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileListener;

public class CompileHandler implements CompileListener{
    
    LocalTime startTime; 
    long timeTaken;
    CompileData counts;

    public CompileHandler(CompileData counts){
        this.counts = counts;
    }
    

    @Override 
    public void compileStarted(CompileEvent event){
        startTime = LocalTime.now();
        counts.countIncrease();
        System.out.println("Compile started at time: " + startTime);
    }

    @Override 
    public void compileSucceeded(CompileEvent event){
        timeTaken = timeBetween(startTime);
        counts.successIncrease();
        System.out.println("Compile Succeeded in " + timeTaken + "s");
    }

    @Override 
    public void compileFailed(CompileEvent event){
        timeTaken = timeBetween(startTime);
        counts.failIncrease();
        System.out.println("Compile Failed in " + timeTaken + "s");
    }

    @Override 
    public void compileError(CompileEvent event){
        counts.errorIncrease();
        counts.addError("Line " + event.getErrorLineNumber());
        System.out.println("Compile Error on Line: " + event.getErrorLineNumber());
    }

    @Override
    public void compileWarning(CompileEvent event){
        counts.warnIncrease();
        System.out.println("Compile Warnings Were Found: " + event.toString());
    }

    private long timeBetween(LocalTime start){
        LocalTime end = LocalTime.now();
        return ChronoUnit.SECONDS.between(start, end);
    }
}
