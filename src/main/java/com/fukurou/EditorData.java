package com.fukurou;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditorData {
    private final long pauseThresholdMs;
    private final long idleThresholdMs;

    private long keystrokes;
    private long pauses;
    private long idlePeriods;
    private long totalIdleMs;
    private long editSessions;

    private final List<Long> intervals = new ArrayList<>();
    private final List<Integer> burstLengths = new ArrayList<>();

    private int currentBurst;
    private long lastKeyTime = -1;

    public EditorData(){
        this(2000, 30_000);
    }

    public EditorData(long pauseThresholdMs, long idleThresholdMs){
        if(pauseThresholdMs <= 0 || idleThresholdMs <= pauseThresholdMs){
            throw new IllegalArgumentException("need 0 < pause threshold < idle threshold");
        }
        this.pauseThresholdMs = pauseThresholdMs;
        this.idleThresholdMs = idleThresholdMs;
    }

    public void recordKeystroke(long timeMs){
        keystrokes++;

        if(lastKeyTime < 0){
            editSessions++;
        }
        else{
            long gap = timeMs - lastKeyTime;
            if(gap >= idleThresholdMs){
                idlePeriods++;
                totalIdleMs += gap;
                editSessions++;
                closeBurst();
            }
            else{
                intervals.add(gap);
                if(gap >= pauseThresholdMs){
                    pauses++;
                    closeBurst();
                }
            }
        }
        
        currentBurst++;
        lastKeyTime = timeMs;
    }

    private void closeBurst(){
        if(currentBurst > 0){
            burstLengths.add(currentBurst);
            currentBurst = 0;
        }
    }

    public long getPauseThreshold() {return pauseThresholdMs;}
    public long getIdleThresholdMs() {return idleThresholdMs;}
    public long getKeystrokes() {return keystrokes;}
    public long getPauses() {return pauses;}
    public long getIdlePeriods() {return idlePeriods;}
    public long getTotalIdleMs() {return totalIdleMs;}
    public long getEditSessions() {return editSessions;}
    
    //Cadence Calculations
    //Jackson ignores getters that return null values when generating the JSON file 

    public long getActiveTypingMs(){
        long sum = 0;
        for(long i : intervals){
            sum += i;
        }
        return sum;
    }

    public Double getKeystrokesPerMinute(){
        long active = getActiveTypingMs();
        if(active == 0){
            return null;
        }
        else{
            return intervals.size() / (active / 60_000.0);
        }
    }

    public Double getMeanIntervalMs(){
        if(intervals.isEmpty()){return null;}
        else{
            return (getActiveTypingMs() / intervals.size()) * 1.0;
        }
    }

    public Double getMedianIntervalMs(){
        if(intervals.isEmpty()){
            return null;
        }

        List<Long> sorted = new ArrayList<>(intervals);
        Collections.sort(sorted);       
        int n = sorted.size();

        if( n % 2 == 0){
            return sorted.get(n/2) * 1.0;
        }
        else{
            return (sorted.get(n/2 - 1) + sorted.get(n/2)) / 2.0;
        }
    }

    //Coefficient of variation is used for the intervals to determine the user's typing rhythm.
    //Low CV corresponds to a steady rhythm; high shows more erratic stop-start typing.
    public Double getIntervalCv(){
        Double mean = getMeanIntervalMs();
        if(mean == null || mean == 0 || intervals.size() < 2){
            return null;
        }
        double sumSq = 0;
        for(long i : intervals){
            double d = i - mean;
            sumSq += d * d;
        }
        double sd = Math.sqrt(sumSq / (intervals.size() - 1));
        return sd/mean;
    }

    public long getBurstCount(){
        return burstLengths.size() + (currentBurst > 0 ? 1 : 0);
    }

    public Double getMeanBurstLength(){
        long count = burstLengths.size() + (currentBurst > 0 ? 1 : 0);
        if(count == 0){
            return null;
        }

        long total = currentBurst;
        for(int b : burstLengths){
            total += b;
        }
        return total / (count * 1.0); 
    }
}
