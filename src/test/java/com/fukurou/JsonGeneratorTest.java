package com.fukurou;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.io.TempDir;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonGeneratorTest {
    @TempDir
    Path tempDir;

    @Test
    void generatesValidJsonFile() throws Exception {
        String originalHome = System.getProperty("user.home");

        // this checks if the json generator saves as a json file 
        try {
            System.setProperty("user.home", tempDir.toString());

            TelemetryData data = new TelemetryData();
            java.io.File file = new JsonGenerator().generate(data);

            assertTrue(Files.exists(file.toPath()), "The JSON file should exist");
            assertTrue(Files.size(file.toPath()) > 0, "The JSON file should not be empty");

            JsonNode savedJson = new ObjectMapper().readTree(file);
            assertNotNull(savedJson, "The saved file should contain valid JSON");
        } 
        finally{
            if(originalHome == null){
                System.clearProperty("user.home");
            }
            else{
                System.setProperty("user.home", originalHome);
            }
        }
    }
}
