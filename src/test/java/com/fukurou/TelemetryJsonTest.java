package com.fukurou;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class TelemetryJsonTest {
    @Test
    void serializesTelemetryData() throws Exception {
        TelemetryData data = new TelemetryData();

        data.getEventLog().record(
            TelemetryEvent.Target.CLASS,
            TelemetryEvent.Type.CREATE,
            "MyProject",
            null,
            "Created a class"
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        JsonNode root = mapper.readTree(json);

        assertTrue(root.has("compile"));
        assertTrue(root.has("lifecycle"));
        assertTrue(root.has("invocations"));
        assertTrue(root.has("editor_activity"));
        assertTrue(root.has("events"));
        assertTrue(root.has("session"));

        JsonNode events = root.get("events");
        assertTrue(events.isArray());

        JsonNode event = events.get(0);
        assertFalse(event.has("name"));
        assertDoesNotThrow(() -> java.time.Instant.parse(
            event.get("timestamp").asText()
        ));
    }
}