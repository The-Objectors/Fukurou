package com.fukurou;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LifecycleDataTest {
    private static final String PROJECT = "TestProject";

    private LifecycleData data;

    @BeforeEach
    void setUp() {
        data = new LifecycleData();
    }

    @Test
    void newInstanceHasZeroedCountersAndNoCurrentProject() {
        assertEquals(0, data.getProjectsOpened());
        assertEquals(0, data.getProjectsClosed());
        assertEquals(0, data.getClassCreates());
        assertEquals(0, data.getClassModifies());
        assertEquals(0, data.getClassDeletes());
        assertEquals(0, data.getTotalMethodDefinitions());
        assertNull(data.getCurrentProjectName());
        assertTrue(data.getClassModifiedPerProject().isEmpty());
    }

    @Test
    void projectOpenAndCloseIncreaseTheirCounters() {
        data.projectOpened();
        data.projectOpened();
        data.projectClosed();

        assertEquals(2, data.getProjectsOpened());
        assertEquals(1, data.getProjectsClosed());
    }

    @Test
    void classCreateRegistersTheClassAsKnown() {
        data.classCreate(PROJECT, "Example");

        assertEquals(1, data.getClassCreates());
        assertTrue(data.isKnownClass(PROJECT, "Example"));
        assertFalse(data.isKnownClass(PROJECT, "Other"));
    }

    @Test
    void secondClassInSameProjectReusesTheExistingSet() {
        data.classCreate(PROJECT, "First");
        data.classCreate(PROJECT, "Second");

        assertEquals(2, data.getClassCreates());
        assertTrue(data.isKnownClass(PROJECT, "First"));
        assertTrue(data.isKnownClass(PROJECT, "Second"));
    }

    @Test
    void classModifyAccumulatesPerProjectCounts() {
        data.classModify(PROJECT);
        data.classModify(PROJECT);

        assertEquals(2, data.getClassModifies());
        Map<String, Integer> perProject = data.getClassModifiedPerProject();
        assertEquals(2, perProject.get(PROJECT));
    }

    @Test
    void classDeleteRemovesAKnownClass() {
        data.classCreate(PROJECT, "Example");

        data.classDelete(PROJECT, "Example");

        assertEquals(1, data.getClassDeletes());
        assertFalse(data.isKnownClass(PROJECT, "Example"));
    }

    @Test
    void classDeleteOnUnknownProjectStillCountsWithoutError() {
        // No set exists for this project yet
        data.classDelete("NeverSeen", "Ghost");

        assertEquals(1, data.getClassDeletes());
        assertFalse(data.isKnownClass("NeverSeen", "Ghost"));
    }

    @Test
    void nullProjectNameFallsBackToUnknownKey() {
        data.classCreate(null, "Example");
        data.classModify(null);

        assertTrue(data.isKnownClass(null, "Example"));
        assertTrue(data.isKnownClass("unknown", "Example"));
        assertEquals(1, data.getClassModifiedPerProject().get("unknown"));
    }

    @Test
    void isKnownClassIsFalseWhenNoSetExistsForProject() {
        assertFalse(data.isKnownClass("NeverSeen", "Anything"));
    }

    @Test
    void addMethodCountAccumulates() {
        data.addMethodCount(3);
        data.addMethodCount(2);

        assertEquals(5, data.getTotalMethodDefinitions());
    }

    @Test
    void currentProjectNameRoundTrips() {
        data.setCurrentProjectName(PROJECT);
        assertEquals(PROJECT, data.getCurrentProjectName());
    }
}
