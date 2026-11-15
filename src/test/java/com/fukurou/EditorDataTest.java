package com.fukurou;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class EditorDataTest {

    // Tolerance for floating-point comparisons on cadence metrics
    private static final double EPS = 1e-9;

    @Test
    void defaultConstructorUsesDocumentedThresh() {
        EditorData data = new EditorData();
        assertEquals(2000, data.getPauseThreshold());
        assertEquals(15_000, data.getIdleThresholdMs());
    }

    @Test
    void customThresholdsAreStored() {
        EditorData data = new EditorData(100, 1000);
        assertEquals(100, data.getPauseThreshold());
        assertEquals(1000, data.getIdleThresholdMs());
    }

    @Test
    void nonPositivePauseThresholdIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new EditorData(0, 1000));
        assertThrows(IllegalArgumentException.class, () -> new EditorData(-5, 1000));
    }

    @Test
    void idleThresholdNotGreaterThanPauseIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new EditorData(100, 100));
        assertThrows(IllegalArgumentException.class, () -> new EditorData(100, 50));
    }

    @Test
    void newInstanceReportsNoActivity() {
        EditorData data = new EditorData(100, 1000);

        assertEquals(0, data.getKeystrokes());
        assertEquals(0, data.getPauses());
        assertEquals(0, data.getEditSessions());
        assertEquals(0, data.getIdlePeriods());
        assertEquals(0, data.getTotalIdleMs());
        assertEquals(0, data.getActiveTypingMs());
        assertEquals(0, data.getBurstCount());
        assertEquals(0, data.getTotalDuration());

        assertNull(data.getKeystrokesPerMinute());
        assertNull(data.getMeanIntervalMs());
        assertNull(data.getMedianIntervalMs());
        assertNull(data.getIntervalCv());
        assertNull(data.getMeanBurstLength());
    }

    @Test
    void singleKeystrokeOpensASessionAndAnOpenBurst() {
        EditorData data = new EditorData(100, 1000);

        data.recordKeystroke(0);

        assertEquals(1, data.getKeystrokes());
        assertEquals(1, data.getEditSessions());
        assertEquals(0, data.getActiveTypingMs());
        
        // No intervals yet so rate/mean/median/cv should be undefined
        assertNull(data.getKeystrokesPerMinute());
        assertNull(data.getMeanIntervalMs());
        assertNull(data.getIntervalCv());
        
        // Burst length of 1 should count towards burst totals
        assertEquals(1, data.getBurstCount());
        assertEquals(1.0, data.getMeanBurstLength(), EPS);
    }

    @Test
    void cvRequiresAtLeastTwoIntervals() {
        EditorData data = new EditorData(100, 1000);

        data.recordKeystroke(0);
        data.recordKeystroke(50); // one interval only (gap 50ms)

        // Rate should be defined with 1 interval.
        assertEquals(1200.0, data.getKeystrokesPerMinute(), EPS);
        
        // CV needs 2 intervals so it remains null
        assertNull(data.getIntervalCv());
    }

    private EditorData timeline() {
        EditorData data = new EditorData(100, 1000);
        data.recordKeystroke(0);
        data.recordKeystroke(50);
        data.recordKeystroke(100);
        data.recordKeystroke(250);
        data.recordKeystroke(2000);
        data.recordKeystroke(2050);
        return data;
    }

    @Test
    void timelineCountsRawEvents() {
        EditorData data = timeline();

        assertEquals(6, data.getKeystrokes());
        assertEquals(2, data.getEditSessions()); // first key + the idle-gap key
        assertEquals(1, data.getPauses());
        assertEquals(1, data.getIdlePeriods());
        assertEquals(1750, data.getTotalIdleMs());
    }

    @Test
    void timelineComputesTypingTotals() {
        EditorData data = timeline();

        // Idle gaps are excluded from active typing time.
        assertEquals(300, data.getActiveTypingMs());
        assertEquals(2050, data.getTotalDuration()); // 300 active + 1750 idle
    }

    @Test
    void timelineComputesCadenceMetrics() {
        EditorData data = timeline();

        // 4 intervals over 300ms of active typing = 4 / (300/60000) = 800 keystrokes/min
        assertEquals(800.0, data.getKeystrokesPerMinute(), EPS);
        // mean interval = 300 / 4 = 75
        assertEquals(75.0, data.getMeanIntervalMs(), EPS);
        // standard deviation from 75 over [50,50,150,50], sd = 50, cv = 50/75
        assertEquals(50.0 / 75.0, data.getIntervalCv(), EPS);
    }

    @Test
    void timelineComputesBurstMetrics() {
        EditorData data = timeline();

        // burstLengths [3,1] plus the still-open burst (2) so 3 bursts total
        assertEquals(3, data.getBurstCount());
        // (3 + 1 + 2) / 3 = 2.0
        assertEquals(2.0, data.getMeanBurstLength(), EPS);
    }

    @Test
    void medianWithEvenNumberOfIntervals() {
        // sorted intervals = [50,50,50,150]
        EditorData data = timeline();
        assertEquals(50.0, data.getMedianIntervalMs(), EPS);
    }

    @Test
    void medianWithOddNumberOfIntervals() {
        // intervals = [10,20,30]
        EditorData data = new EditorData(100, 1000);
        data.recordKeystroke(0);
        data.recordKeystroke(10);
        data.recordKeystroke(30);
        data.recordKeystroke(60);

        // sorted = [10,20,30]
        assertEquals(20.0, data.getMedianIntervalMs(), EPS);
    }
}
