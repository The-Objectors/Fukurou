package com.fukurou;

import java.io.File;
import java.io.IOException;

final class OutputPaths {
    private static final String DIRECTORY_NAME = "fukurou";

    private OutputPaths() {
    }

    static File jsonFile() throws IOException {
        return new File(outputDirectory(), "telemetry_data.json");
    }

    static File pdfFile() throws IOException {
        return new File(outputDirectory(), "Telemetry_report.pdf");
    }

    private static File outputDirectory() throws IOException {
        File directory = new File(System.getProperty("user.home"), DIRECTORY_NAME);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create directory: " + directory.getAbsolutePath());
        }
        return directory;
    }
}
