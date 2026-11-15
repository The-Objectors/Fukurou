package com.fukurou;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bluej.extensions2.event.CompileEvent;
import bluej.extensions2.event.CompileEvent.EventType;

class CompileHandlerTest {
    private CompileData data;
    private EventLog log;
    private CompileHandler handler;

    @BeforeEach
    void setUp() {
        data = new CompileData();
        log = new EventLog();
        handler = new CompileHandler(data, log);
    }

    @Test
    void compileStartedCountsAttemptAndLogsFiles() {
        handler.compileStarted(event(EventType.COMPILE_START_EVENT));

        assertEquals(1, data.getCompileCount());
        assertEvent(0, "start", "Example.java", null);
    }

    @Test
    void compileSucceededCountsSuccessAndLogsElapsedTime() {
        handler.compileStarted(event(EventType.COMPILE_START_EVENT));
        handler.compileSucceeded(event(EventType.COMPILE_DONE_EVENT));

        assertEquals(1, data.getCompileSuccess());
        assertEquals(1, data.getCompileCount());
        assertEquals("success", log.getEvents().get(1).getType());
        assertTrue(log.getEvents().get(1).getDetail().matches("\\d+ms"));
        assertEquals("Example.java", log.getEvents().get(1).getName());
    }

    @Test
    void compileFailedCountsFailureAndLogsElapsedTime() {
        handler.compileStarted(event(EventType.COMPILE_START_EVENT));
        handler.compileFailed(event(EventType.COMPILE_FAILED_EVENT));

        assertEquals(1, data.getCompileFail());
        assertEquals(1, data.getCompileCount());
        assertEquals("fail", log.getEvents().get(1).getType());
        assertTrue(log.getEvents().get(1).getDetail().matches("\\d+ms"));
    }

    @Test
    void compileErrorStoresMessageAndLogsLineNumber() {
        CompileEvent error = event(EventType.COMPILE_ERROR_EVENT);
        error.setErrorLineNumber(17);
        error.setErrorMessage("  cannot find symbol  ");

        handler.compileError(error);

        assertEquals(1, data.getCompileError());
        assertEquals("cannot find symbol", data.getErrorInfo().get(0));
        assertEvent(0, "error", "Example.java", "Line 17:   cannot find symbol  ");
    }

    @Test
    void compileErrorUsesFallbackForBlankMessage() {
        CompileEvent error = event(EventType.COMPILE_ERROR_EVENT);
        error.setErrorLineNumber(4);
        error.setErrorMessage("  ");

        handler.compileError(error);

        assertEquals("(no error message)", data.getErrorInfo().get(0));
        assertEvent(0, "error", "Example.java", "Line 4:   ");
    }

    @Test
    void compileWarningCountsWarningAndLogsMessage() {
        CompileEvent warning = event(EventType.COMPILE_WARNING_EVENT);
        warning.setErrorMessage("unchecked operation");

        handler.compileWarning(warning);

        assertEquals(1, data.getCompileWarn());
        assertEvent(0, "warning", "Example.java", "unchecked operation");
    }

    private CompileEvent event(EventType type) {
        return new CompileEvent(type, new File[] {new File("Example.java")});
    }

    private void assertEvent(int index, String type, String name, String detail) {
        List<TelemetryEvent> events = log.getEvents();
        assertEquals(type, events.get(index).getType());
        assertEquals("compile", events.get(index).getTarget());
        assertEquals(name, events.get(index).getName());
        assertEquals(detail, events.get(index).getDetail());
    }
}
