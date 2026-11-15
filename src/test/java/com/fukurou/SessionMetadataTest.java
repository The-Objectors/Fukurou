package com.fukurou;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionMetadataTest {
    private SessionMetadata metadata;

    @BeforeEach
    void setUp() {
        metadata = new SessionMetadata();
    }

    @Test
    void studentIdIsThePlaceholderConstant() {
        assertEquals("REPLACE_WITH_STUDENT_ID", metadata.getStudentId());
    }

    @Test
    void startTimeIsSetAtConstructionAndParsesAsAnInstant() {
        String started = metadata.getStartedAtUtc();
        assertNotNull(started);
        // Should be a valid ISO-8601 instant; parsing throws if not.
        assertNotNull(Instant.parse(started));
    }

    @Test
    void endedAtIsNullUntilASnapshotOrFinishOccurs() {
        assertNull(metadata.getEndedAtUtc());
    }

    @Test
    void updateSnapshotRecordsAnEndTimestamp() {
        metadata.updateSnapshot();

        String ended = metadata.getEndedAtUtc();
        assertNotNull(ended);
        assertNotNull(Instant.parse(ended));
    }

    @Test
    void finishRecordsAnEndTimestampAndPreventsFurtherSnapshots() {
        metadata.finish();
        String endedAfterFinish = metadata.getEndedAtUtc();
        assertNotNull(endedAfterFinish);

        // Once finished, updateSnapshot must not overwrite the recorded time.
        metadata.updateSnapshot();
        assertEquals(endedAfterFinish, metadata.getEndedAtUtc());
    }

    @Test
    void finishNoLongerChangesEndedAfterTheFirstCall() {
        metadata.finish();
        String firstEnd = metadata.getEndedAtUtc();

        metadata.finish();
        assertEquals(firstEnd, metadata.getEndedAtUtc());
    }

    @Test
    void endTimestampParsesWithoutThrowing() {
        metadata.updateSnapshot();
        // Guards the ISO-8601 contract of calculateMetrics()
        try {
            Instant.parse(metadata.getEndedAtUtc());
        } catch (DateTimeParseException e) {
            throw new AssertionError("endedAtUtc was not a valid instant", e);
        }
    }
}
