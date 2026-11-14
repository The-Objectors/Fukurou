package com.fukurou;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ReportBuilder {
    private static final PDFont BODY_FONT    = PDType1Font.HELVETICA;
    private static final PDFont HEADING_FONT  = PDType1Font.HELVETICA_BOLD;
    private static final float  BODY_SIZE     = 10f;
    private static final float  HEADING_SIZE  = 16f;
    private static final float  BODY_LEADING  = 15f;
    private static final float  HEADING_LEADING = 24f;
    
    public List<ReportBlock> build(TelemetryData data){
        List<ReportBlock> blocks = new ArrayList<>();
        EditorData ed = data.getEditorData();
        CompileData cd = data.getCompileData();

        //Page 1 
        blocks.add(heading("Fukurou Telemetry Report"));
        blocks.add(text(sessionHeader(data)));
        blocks.add(text(kpiStrip(ed, cd)));

        blocks.add(new RowBlock(20f, new ChartBlock(Charts.activeIdleDonut(ed), 240, 200), new ChartBlock(Charts.compileOutcome(cd), 240, 200)));

        blocks.add(heading("Compilation error frequency"));
        blocks.add(new ChartBlock(Charts.errorBar(cd.getErrorInfo()), 500, 220));

        //Page 2
        blocks.add(heading("Class event timeline"));
        blocks.add(new ChartBlock(Charts.classTimeline(data.getEventLog()), 500, 260));

        blocks.add(heading("Compilation error detail"));
        blocks.addAll(errorTable(cd.getErrorInfo()));

        return blocks;
    }

    private ReportBlock heading(String s){
        return new TextBlock(s, HEADING_FONT, HEADING_SIZE, HEADING_LEADING);
    }

    private ReportBlock text(String s){
        return new TextBlock(s, BODY_FONT, BODY_SIZE, BODY_LEADING);
    }

    private String sessionHeader(TelemetryData data){
        SessionMetadata s = data.getSessionMetadata();
        EditorData ed = data.getEditorData();
        StringBuilder sb = new StringBuilder();
        
        sb.append("Student ID: ").append(s.getStudentId()).append('\n');
        sb.append("Started (UTC): ").append(s.getStartedAtUtc()).append('\n');
        sb.append("Ended (UTC): ").append(s.getEndedAtUtc()).append('\n');
        sb.append("Session duration: ").append(formatDuration(ed.getTotalDuration()));
        
        return sb.toString();
    }

    private String kpiStrip(EditorData ed, CompileData cd) {
        StringBuilder sb = new StringBuilder();
        sb.append("Active typing: ").append(formatDuration(ed.getActiveTypingMs()))
          .append("   |   Idle: ").append(formatDuration(ed.getTotalIdleMs())).append('\n');
        sb.append("Keystrokes: ").append(ed.getKeystrokes())
          .append("   |   Keystrokes/min: ").append(formatDouble(ed.getKeystrokesPerMinute()))
          .append('\n');
        sb.append("Compile attempts: ").append(cd.getCompileCount())
          .append("   |   Success rate: ").append(successRate(cd));
        return sb.toString();
    }
    
    private List<ReportBlock> errorTable(List<String> errors) {
        List<ReportBlock> rows = new ArrayList<>();
        if (errors == null || errors.isEmpty()) {
            rows.add(text("No compilation errors recorded."));
            return rows;
        }
        
        Map<String, Integer> freq = new LinkedHashMap<>();
        for (String e : errors) {
            freq.merge(e, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            rows.add(text(entry.getValue() + " occurrence(s): " + entry.getKey()));
        }
        return rows;
    }

     private String successRate(CompileData cd) {
        int total = cd.getCompileCount();
        if (total == 0) return "n/a";
        return Math.round(100.0 * cd.getCompileSuccess() / total) + "%";
    }

    private String formatDouble(Double d) {
        return d == null ? "n/a" : String.format("%.1f", d);
    }

    private String formatDuration(long ms) {
        long totalSec = ms / 1000;
        long h = totalSec / 3600, m = (totalSec % 3600) / 60, sec = totalSec % 60;
        if (h > 0) return String.format("%dh %dm %ds", h, m, sec);
        if (m > 0) return String.format("%dm %ds", m, sec);
        return sec + "s";
    }
}
