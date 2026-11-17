package com.fukurou.telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Editor typing metrics calculated from keystroke timestamps.
 * <p>
 * Each gap between consecutive keystrokes is classified using two thresholds:
 * </p>
 * <ul>
 *   <li>below the pause threshold: part of the current typing burst;</li>
 *   <li>at least the pause threshold but below the idle threshold: a pause, which ends the burst;</li>
 *   <li>at least the idle threshold: an idle period, which ends the burst and starts a new edit session.</li>
 * </ul>
 * Gaps below the idle threshold are kept as intervals and used for the cadence statistics.
 * Keystrokes are supplied by {@link com.fukurou.handlers.EditorActivityHandler}.
 */
@JsonPropertyOrder({"pause_threshold_ms", "idle_threshold_ms", "keystrokes", "keystrokes/min",
"burst_count", "average_burst_length", "pauses", "edit_sessions", "idle_periods",
"total_idle_ms", "active_typing_ms", "total_duration", "typing_cadence(internal_cv)"})

public class EditorData {
    /** Minimum gap in milliseconds that counts as a pause. */
    private final long pauseThresholdMs;
    /** Minimum gap in milliseconds that counts as an idle period. */
    private final long idleThresholdMs;

    /** Number of keystrokes recorded. */
    private long keystrokes;
    /** Number of gaps classified as pauses. */
    private long pauses;
    /** Number of gaps classified as idle periods. */
    private long idlePeriods;
    /** Sum of all idle-period gaps in milliseconds. */
    private long totalIdleMs;
    /** Number of edit sessions: one for the first keystroke, plus one after each idle period. */
    private long editSessions;

    /** Gaps in milliseconds between keystrokes that were shorter than the idle threshold. */
    private final List<Long> intervals = new ArrayList<>();
    /** Keystroke counts of bursts that have ended. */
    private final List<Integer> burstLengths = new ArrayList<>();

    /** Keystrokes in the burst that is still in progress. */
    private int currentBurst;
    /** Timestamp in milliseconds of the previous keystroke, or -1 if none has been recorded. */
    private long lastKeyTime = -1;

    /** Creates editor data with a 2000 ms pause threshold and a 15000 ms idle threshold. */
    public EditorData(){
        this(2000, 15_000);
    }

    /**
     * Creates editor data with the given thresholds.
     *
     * @param pauseThresholdMs minimum gap in milliseconds that counts as a pause
     * @param idleThresholdMs  minimum gap in milliseconds that counts as an idle period
     * @throws IllegalArgumentException unless {@code 0 < pauseThresholdMs < idleThresholdMs}
     */
    public EditorData(long pauseThresholdMs, long idleThresholdMs){
        if(pauseThresholdMs <= 0 || idleThresholdMs <= pauseThresholdMs){
            throw new IllegalArgumentException("need 0 < pause threshold < idle threshold");
        }
        this.pauseThresholdMs = pauseThresholdMs;
        this.idleThresholdMs = idleThresholdMs;
    }

    /**
     * Records a keystroke and classifies the gap since the previous one as an
     * interval, a pause or an idle period. The first keystroke starts the first
     * edit session.
     *
     * @param timeMs keystroke time in milliseconds; must come from the same clock
     *               on every call and should not decrease
     */
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

    /** Stores the current burst's length, if it has any keystrokes, and starts a new burst. */
    private void closeBurst(){
        if(currentBurst > 0){
            burstLengths.add(currentBurst);
            currentBurst = 0;
        }
    }

    /** @return the pause threshold in milliseconds */
    @JsonProperty("pause_threshold_ms")
    public long getPauseThreshold() {return pauseThresholdMs;}

    /** @return the idle threshold in milliseconds */
    @JsonProperty("idle_threshold_ms")
    public long getIdleThresholdMs() {return idleThresholdMs;}

    /** @return the number of keystrokes recorded */
    @JsonProperty("keystrokes")
    public long getKeystrokes() {return keystrokes;}

    /** @return the number of gaps classified as pauses */
    @JsonProperty("pauses")
    public long getPauses() {return pauses;}

    /** @return the number of edit sessions */
    @JsonProperty("edit_sessions")
    public long getEditSessions() {return editSessions;}

    /** @return the number of gaps classified as idle periods */
    @JsonProperty("idle_periods")
    public long getIdlePeriods() {return idlePeriods;}

    /** @return the total length of all idle periods in milliseconds */
    @JsonProperty("total_idle_ms")
    public long getTotalIdleMs() {return totalIdleMs;}

    //Cadence Calculations
    //Methods returning Double return null when there is not enough data to calculate a value.

    /**
     * Sum of all recorded intervals, i.e. every gap shorter than the idle
     * threshold (including pauses).
     *
     * @return active typing time in milliseconds
     */
    @JsonProperty("active_typing_ms")
    public long getActiveTypingMs(){
        long sum = 0;
        for(long i : intervals){
            sum += i;
        }
        return sum;
    }

    /**
     * Number of recorded intervals per minute of active typing time.
     *
     * @return the typing rate, or {@code null} if active typing time is 0
     */
    @JsonProperty("keystrokes/min")
    public Double getKeystrokesPerMinute(){
        long active = getActiveTypingMs();
        if(active == 0){
            return null;
        }
        else{
            return intervals.size() / (active / 60_000.0);
        }
    }

    /**
     * Mean of the recorded intervals. The division is done in integer
     * arithmetic, so the result is rounded down to a whole millisecond.
     *
     * @return the mean interval in milliseconds, or {@code null} if there are no intervals
     */
    public Double getMeanIntervalMs(){
        if(intervals.isEmpty()){return null;}
        else{
            return (getActiveTypingMs() / intervals.size()) * 1.0;
        }
    }

    /**
     * Median of the recorded intervals; the average of the two middle values
     * when the count is even.
     *
     * @return the median interval in milliseconds, or {@code null} if there are no intervals
     */
    public Double getMedianIntervalMs(){
        if(intervals.isEmpty()){
            return null;
        }

        List<Long> sorted = new ArrayList<>(intervals);
        Collections.sort(sorted);
        int n = sorted.size();

        if( n % 2 == 0){
            return (sorted.get(n/2 - 1) + sorted.get(n/2)) / 2.0;
        }
        else{
            return sorted.get(n/2) * 1.0;
        }
    }

    /**
     * Coefficient of variation of the intervals (sample standard deviation
     * divided by {@link #getMeanIntervalMs()}), used as a measure of typing rhythm.
     * A low value indicates steady typing; a high value indicates more stop-start typing.
     *
     * @return the coefficient of variation, or {@code null} if there are fewer than
     *         two intervals or the mean is 0
     */
    @JsonProperty("typing_cadence(internal_cv)")
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

    /**
     * @return the number of typing bursts, including the one in progress
     */
    @JsonProperty("burst_count")
    public long getBurstCount(){
        return burstLengths.size() + (currentBurst > 0 ? 1 : 0);
    }

    /**
     * Mean number of keystrokes per burst, including the burst in progress.
     *
     * @return the mean burst length, or {@code null} if there are no bursts
     */
    @JsonProperty("average_burst_length")
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

    /**
     * Sum of {@link #getActiveTypingMs()} and {@link #getTotalIdleMs()}.
     *
     * @return the total time covered by recorded gaps, in milliseconds
     */
    @JsonProperty("total_duration")
    public long getTotalDuration(){
        return getActiveTypingMs() + getTotalIdleMs();
    }
}
