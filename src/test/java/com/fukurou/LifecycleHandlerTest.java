package com.fukurou;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bluej.extensions2.BClass;
import bluej.extensions2.BMethod;
import bluej.extensions2.BPackage;
import bluej.extensions2.BProject;
import bluej.extensions2.ProjectNotOpenException;
import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.PackageEvent;

class LifecycleHandlerTest {
    private static final String PROJECT = "TestProject";

    private LifecycleData data;
    private EventLog log;
    private LifecycleHandler handler;

    @BeforeEach
    void setUp() {
        data = new LifecycleData();
        data.setCurrentProjectName(PROJECT);
        log = new EventLog();
        handler = new LifecycleHandler(data, log);
    }

    @Test
    void firstClassStateCreatesClassAndTracksMethodBaselineThenDeltas() throws Exception {
        BClass bClass = mock(BClass.class);
        when(bClass.getName()).thenReturn("Example");
        when(bClass.getDeclaredMethods()).thenReturn(
            new BMethod[2], new BMethod[3], new BMethod[1]
        );
        ClassEvent event = classEvent(bClass);

        handler.classStateChanged(event);
        assertTrue(data.isKnownClass(PROJECT, "Example"));
        handler.classStateChanged(event);
        handler.classStateChanged(event);

        assertEquals(1, data.getClassCreates());
        assertEquals(2, data.getClassModifies());
        assertEquals(3, data.getTotalMethodDefinitions());
        assertEquals(6, log.getEvents().size());
        assertEvent(0, "create", "class", "Example", null);
        assertEvent(1, "modify", "class", "Example", "baseline: 2 method(s)");
        assertEvent(3, "modify", "class", "Example", "+ 1 method(s) (total 3)");
        assertEvent(5, "modify", "class", "Example", "- 2 method(s) (total 1)");
    }

    @Test
    void renameAndRemovalUpdateCountsAndLogEvents() throws Exception {
        BClass bClass = mock(BClass.class);
        when(bClass.getName()).thenReturn("Renamed");
        ClassEvent event = classEvent(bClass);
        when(event.getOldName()).thenReturn("Original");
        data.classCreate(PROJECT, "Renamed");

        handler.classNameChanged(event);
        handler.classRemoved(event);

        assertEquals(1, data.getClassModifies());
        assertEquals(1, data.getClassDeletes());
        assertFalse(data.isKnownClass(PROJECT, "Renamed"));
        assertEvent(0, "modify", "class", "Renamed", "renamed from Original");
        assertEvent(1, "delete", "class", "Renamed", null);
    }

    @Test
    void packageOpenAndCloseUpdateProjectCountsAndLogEvents() throws Exception {
        BProject project = mock(BProject.class);
        BPackage bPackage = mock(BPackage.class);
        PackageEvent event = mock(PackageEvent.class);
        when(event.getPackage()).thenReturn(bPackage);
        when(bPackage.getProject()).thenReturn(project);
        when(project.getName()).thenReturn(PROJECT);

        handler.packageOpened(event);
        handler.packageClosing(event);

        assertEquals(1, data.getProjectsOpened());
        assertEquals(1, data.getProjectsClosed());
        assertEquals(2, log.getEvents().size());
        assertEvent(0, "open", "project", PROJECT, null);
        assertEvent(1, "close", "project", PROJECT, null);
    }

    @Test
    void unresolvedProjectUsesUnknownFallback() throws Exception {
        data.setCurrentProjectName(null);
        BClass bClass = mock(BClass.class);
        when(bClass.getName()).thenReturn("Example");
        when(bClass.getPackage()).thenThrow(mock(ProjectNotOpenException.class));

        handler.classStateChanged(classEvent(bClass));

        assertEquals(1, data.getClassCreates());
        assertTrue(data.isKnownClass("unknown", "Example"));
        TelemetryEvent event = log.getEvents().get(0);
        assertEquals("create", event.getType());
        assertEquals("unknown", event.getProject());
    }

    private ClassEvent classEvent(BClass bClass) {
        ClassEvent event = mock(ClassEvent.class);
        when(event.getBClass()).thenReturn(bClass);
        return event;
    }

    private void assertEvent(int index, String type, String target, String name, String detail) {
        TelemetryEvent event = log.getEvents().get(index);
        assertEquals(type, event.getType());
        assertEquals(target, event.getTarget());
        assertEquals(name, event.getName());
        assertEquals(PROJECT, event.getProject());
        assertEquals(detail, event.getDetail());
    }
}
