package com.fukurou.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fukurou.telemetry.EventLog;
import com.fukurou.telemetry.InvocationData;
import com.fukurou.telemetry.TelemetryEvent;

import bluej.extensions2.BObject;
import bluej.extensions2.event.InvocationFinishedEvent;
import bluej.extensions2.event.InvocationFinishedEvent.EventType;

class InvocationHandlerTest {
    private InvocationData data;
    private EventLog log;
    private InvocationHandler handler;

    @BeforeEach
    void setUp() {
        data = new InvocationData();
        log = new EventLog();
        handler = new InvocationHandler(data, log);
    }

    @Test
    void normalMethodExitIncrementsInvocationsAndLogsSuccess() {
        handler.invocationFinished(event(EventType.NORMAL_EXIT, null));

        assertEquals(1, data.getInvocations());
        assertEquals(0, data.getRuntimeExceptions());
        assertEquals(0, data.getUserTerminations());
        assertEvent("success");
    }

    @Test
    void normalConstructorExitIsNotCounted() {
        handler.invocationFinished(event(EventType.NORMAL_EXIT, mock(BObject.class)));

        assertEquals(0, data.getInvocations());
        assertEquals(0, log.getEvents().size());
    }

    @Test
    void exceptionExitIncrementsInvocationAndExceptionCounts() {
        handler.invocationFinished(event(EventType.EXCEPTION_EXIT, null));

        assertEquals(1, data.getInvocations());
        assertEquals(1, data.getRuntimeExceptions());
        assertEvent("fail");
    }

    @Test
    void terminatedExitIncrementsInvocationAndTerminationCounts() {
        handler.invocationFinished(event(EventType.TERMINATED_EXIT, null));

        assertEquals(1, data.getInvocations());
        assertEquals(1, data.getUserTerminations());
        assertEvent("fail");
    }

    @Test
    void resultFailureDoesNotPreventNormalExitFromBeingRecorded() {
        InvocationFinishedEvent event = mock(InvocationFinishedEvent.class);
        when(event.getClassName()).thenReturn("Example");
        when(event.getEventType()).thenReturn(EventType.NORMAL_EXIT);
        when(event.getResult()).thenThrow(new IllegalStateException("result unavailable"));

        handler.invocationFinished(event);

        assertEquals(1, data.getInvocations());
        assertEvent("success");
    }

    private InvocationFinishedEvent event(EventType type, Object result) {
        InvocationFinishedEvent event = mock(InvocationFinishedEvent.class);
        when(event.getClassName()).thenReturn("Example");
        when(event.getEventType()).thenReturn(type);
        when(event.getResult()).thenReturn(result);
        return event;
    }

    private void assertEvent(String type) {
        assertEquals(1, log.getEvents().size());
        TelemetryEvent event = log.getEvents().get(0);
        assertEquals(type, event.getType());
        assertEquals("execute", event.getTarget());
        assertEquals("Example", event.getName());
    }
}
