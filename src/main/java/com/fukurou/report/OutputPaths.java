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
    /** Name of the fallback folder created under the user's home directory. */
    private static final String FALLBACK_DIR = "fukurou";

    /** Not instantiable; all methods are static. */
    private OutputPaths() {
    }

    /**
     * Path of the JSON output file for this session.
     *
     * @param data the session telemetry
     * @return the {@code .json} file in the output directory
     * @throws IOException if the fallback directory is needed and cannot be created
     */
    static File jsonFile(TelemetryData data) throws IOException {
        return new File(outputDirectory(data), label(data) + ".json");
    }

    /**
     * Path of the PDF output file for this session.
     *
     * @param data the session telemetry
     * @return the {@code .pdf} file in the output directory
     * @throws IOException if the fallback directory is needed and cannot be created
     */
    static File pdfFile(TelemetryData data) throws IOException {
        return new File(outputDirectory(data), label(data) + ".pdf");
    }

    /**
     * Builds a file-name-safe label of the form {@code <name>_<time>}.
     * In the name, characters other than letters, digits, {@code .}, {@code _} and
     * {@code -} become {@code _}; in the time, characters other than letters,
     * digits and {@code -} become {@code -}.
     *
     * @param projectName the project name; {@code "session"} is used if {@code null} or empty
     * @param startedUtc  the session start time; may be {@code null}
     * @return the label, or just the name part if the start time is {@code null} or empty
     */
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

    /**
     * Session label for the given telemetry. Uses the current project name, or the
     * project directory's name if no project name is set, plus the session start time.
     *
     * @param data the session telemetry
     * @return the label from {@link #sessionLabel(String, String)}
     */
    private static String label(TelemetryData data) {
        LifecycleData lifecycle = data.getLifecycleData();
        String name = lifecycle.getCurrentProjectName();
        File dir = lifecycle.getProjectDir();
        if ((name == null || name.isEmpty()) && dir != null) {
            name = dir.getName();
        }
        return sessionLabel(name, data.getSessionMetadata().getStartedAtUtc());
    }

    /**
     * The project directory if it is set and exists; otherwise the fallback
     * folder under the user's home directory, created if needed.
     *
     * @param data the session telemetry
     * @return the directory to write output to
     * @throws IOException if the fallback directory does not exist and cannot be created
     */
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
