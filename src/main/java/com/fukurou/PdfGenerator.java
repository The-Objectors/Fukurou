package com.fukurou;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class PdfGenerator implements Generator{

    public void generate(TelemetryData data) throws IOException {
        File directory = new File(System.getProperty("user.home"), "fukurou");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create directory: " + directory.getAbsolutePath());
        }

        File output = new File(directory, "Telemetry_report.pdf");
        List<String> lines = createReportLines(data);
        List<String> wrappedLines = new ArrayList<>();

        for (String line : lines) {
            wrappedLines.addAll(wrap(line));
        }

        try (PDDocument document = new PDDocument()) {
            for (int start = 0; start < wrappedLines.size(); start += 45) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    float y = 750;
                    int end = Math.min(start + 45, wrappedLines.size());

                    for (int i = start; i < end; i++) {
                        String line = wrappedLines.get(i);
                        boolean title = i == 0;

                        content.beginText();
                        content.setFont(
                                title ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA,
                                title ? 16 : 10);
                        content.newLineAtOffset(50, y);
                        content.showText(line);
                        content.endText();

                        y -= title ? 24 : 15;
                    }
                }
            }

            document.save(output);
        }

        System.out.println("PDF report saved to: " + output.getAbsolutePath());
    }

    private List<String> createReportLines(TelemetryData data) {
        List<String> lines = new ArrayList<>();
        CompileData compile = data.getCompileData();
        SessionMetadata session = data.getSessionMetadata();

        lines.add("Fukurou Telemetry Report");
        lines.add("Student ID: " + session.getStudentId());
        lines.add("Started (UTC): " + session.getStartedAtUtc());
        lines.add("Ended (UTC): " + session.getEndedAtUtc());
        lines.add("Session duration (ms): " + session.getTotalDuration());
        lines.add("Estimated active time (ms): " + session.getActiveTimeMillis());
        lines.add("Estimated idle time (ms): " + session.getIdleTimeMillis());
        lines.add("");
        lines.add("Compilation summary");
        lines.add("Attempts: " + compile.getCompileCount());
        lines.add("Successful: " + compile.getCompileSuccess());
        lines.add("Failed: " + compile.getCompileFail());
        lines.add("Errors: " + compile.getCompileError());
        lines.add("Warnings: " + compile.getCompileWarn());
        lines.add("");
        
        lines.add("Compilation error frequency");

        List<String> errors = compile.getErrorInfo();
        List<String> reported = new ArrayList<>();

        if (errors.isEmpty()) {
            lines.add("No compilation errors recorded.");
        } else {
            for (String error : errors) {
                if (!reported.contains(error)) {
                    lines.add(Collections.frequency(errors, error)
                            + " occurrence(s): " + error);
                    reported.add(error);
                }
            }
        }
      
        lines.add("Class event timeline");
        lines.add("");
        boolean foundClassEvent = false;
        for (TelemetryEvent event : data.getEventLog().getEvents()) {
            if ("class".equals(event.getTarget())) {
                lines.add(event.getTimestamp() + " | "
                        + event.getType() + " | "
                        + event.getName() + " | project: "
                        + event.getProject());
                foundClassEvent = true;
            }
        }

        if (!foundClassEvent) {
            lines.add("No class events recorded.");
        }

        return lines;
    }

    private List<String> wrap(String text) {
        String safeText = toAscii(text);
        List<String> result = new ArrayList<>();
        final int maxCharacters = 95;

        while (safeText.length() > maxCharacters) {
            int breakAt = safeText.lastIndexOf(' ', maxCharacters);
            if (breakAt <= 0) {
                breakAt = maxCharacters;
            }

            result.add(safeText.substring(0, breakAt));
            safeText = safeText.substring(breakAt).trim();
        }

        result.add(safeText);
        return result;
    }

    private String toAscii(String text) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            result.append(c >= 32 && c <= 126 ? c : '?');
        }

        return result.toString();
    }
}