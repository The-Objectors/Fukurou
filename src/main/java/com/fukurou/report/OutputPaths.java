package com.fukurou.report;

import java.io.File;
import java.io.IOException;

import com.fukurou.telemetry.LifecycleData;
import com.fukurou.telemetry.TelemetryData;

/**
 * Handles file locations and per-session file names for generated reports.
 *
 * <p>Keeps the {@link Generator} strategies free of any
 * path logic (Single Responsibility). Per the project rubric, output is written
 * into the active BlueJ project directory and named for the session
 * ({@code <project>_<startTimestamp>}) so that each session produces a separate,
 * non-overwritten file. When no project directory is known, it falls back to a 
 * {@code fukurou} folder under the user's home directory.
 */
final class OutputPaths {
    private static final String FALLBACK_DIR = "fukurou";

    private OutputPaths() {
    }

    static File jsonFile(TelemetryData data) throws IOException {
        return new File(outputDirectory(data), label(data) + ".json");
    }

    static File pdfFile(TelemetryData data) throws IOException {
        return new File(outputDirectory(data), label(data) + ".pdf");
    }

    static String sessionLabel(String projectName, String startedUtc) {
        String name;
        String time;
        
        if(projectName == null || projectName.isEmpty()){
            name = "session";
        } else{
            name = projectName;
        }
        if(startedUtc == null){
            time = "";
        } else{
            time = startedUtc;
        }

        String safeName = name.replaceAll("[^A-Za-z0-9._-]", "_");
        String safeTime = time.replaceAll("[^A-Za-z0-9-]", "-");
        return safeTime.isEmpty() ? safeName : safeName + "_" + safeTime;
    }

    private static String label(TelemetryData data) {
        LifecycleData lifecycle = data.getLifecycleData();
        String name = lifecycle.getCurrentProjectName();
        File dir = lifecycle.getProjectDir();
        if ((name == null || name.isEmpty()) && dir != null) {
            name = dir.getName();
        }
        return sessionLabel(name, data.getSessionMetadata().getStartedAtUtc());
    }

    private static File outputDirectory(TelemetryData data) throws IOException {
        File projectDir = data.getLifecycleData().getProjectDir();
        if (projectDir != null && projectDir.isDirectory()) {
            return projectDir;
        }
        File fallback = new File(System.getProperty("user.home"), FALLBACK_DIR);
        if (!fallback.exists() && !fallback.mkdirs()) {
            throw new IOException("Could not create directory: " + fallback.getAbsolutePath());
        }
        return fallback;
    }
}
