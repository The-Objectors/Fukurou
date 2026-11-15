package com.fukurou.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fukurou.telemetry.TelemetryData;

class OutputPathsTest {

    // Characters forbidden in Windows file names; none may appear in a label.
    private static final String ILLEGAL = "<>:\"/\\|?*";

    private static void assertNoIllegalChars(String fileName) {
        for (int i = 0; i < ILLEGAL.length(); i++) {
            char bad = ILLEGAL.charAt(i);
            assertFalse(fileName.indexOf(bad) >= 0,
                "file name '" + fileName + "' contains illegal char '" + bad + "'");
        }
    }

    @Test
    void sessionLabelStripsColonsAndDotsFromTimestamp() {
        String label = OutputPaths.sessionLabel("MyProject", "2026-11-15T14:30:05.123Z");

        assertNoIllegalChars(label);
        assertFalse(label.contains(":"), "colon must be sanitised for Windows");
        assertFalse(label.contains("."), "dot from millis must be sanitised");
        assertTrue(label.startsWith("MyProject_"));
    }

    @Test
    void sessionLabelRemovesSpacesAndSlashesInProjectName() {
        String label = OutputPaths.sessionLabel("My Project/v2", "2026-01-01T00:00:00Z");

        assertNoIllegalChars(label);
        assertTrue(label.startsWith("My_Project_v2_"), "got: " + label);
    }

    @Test
    void sessionLabelFallsBackToSessionWhenNameMissing() {
        assertTrue(OutputPaths.sessionLabel(null, "2026-01-01T00:00:00Z").startsWith("session_"));
        assertTrue(OutputPaths.sessionLabel("", "2026-01-01T00:00:00Z").startsWith("session_"));
    }

    @Test
    void jsonAndPdfInProjectDirWithSafeSessionNames(@TempDir File projectDir) throws IOException {
        TelemetryData data = new TelemetryData();
        data.getLifecycleData().setProjectDir(projectDir);
        data.getLifecycleData().setCurrentProjectName("Demo Project");

        File json = OutputPaths.jsonFile(data);
        File pdf = OutputPaths.pdfFile(data);

        // Correct directory
        assertEquals(projectDir.getAbsolutePath(), json.getParentFile().getAbsolutePath());
        assertEquals(projectDir.getAbsolutePath(), pdf.getParentFile().getAbsolutePath());

        // Correct, windows-safe, session-specific names
        assertNoIllegalChars(json.getName());
        assertNoIllegalChars(pdf.getName());
        assertTrue(json.getName().startsWith("Demo_Project_"), "got: " + json.getName());
        assertTrue(json.getName().endsWith(".json"));
        assertTrue(pdf.getName().endsWith(".pdf"));

        // no overwrite across sessions
        String jsonBase = json.getName().substring(0, json.getName().length() - ".json".length());
        String pdfBase = pdf.getName().substring(0, pdf.getName().length() - ".pdf".length());
        assertEquals(jsonBase, pdfBase);
    }

    @Test
    void fallsBackToProjectFolderNameWhenProjectNameMissing(@TempDir File projectDir) throws IOException {
        TelemetryData data = new TelemetryData();
        data.getLifecycleData().setProjectDir(projectDir);
        // If no project name set derive the label from the directory name

        File json = OutputPaths.jsonFile(data);

        assertEquals(projectDir.getAbsolutePath(), json.getParentFile().getAbsolutePath());
        assertTrue(json.getName().startsWith(projectDir.getName() + "_"), "got: " + json.getName());
    }
}
