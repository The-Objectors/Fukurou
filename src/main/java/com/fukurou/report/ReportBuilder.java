package com.fukurou.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.fukurou.report.block.ChartBlock;
import com.fukurou.report.block.ReportBlock;
import com.fukurou.report.block.RowBlock;
import com.fukurou.report.block.TextBlock;
import com.fukurou.telemetry.CompileData;
import com.fukurou.telemetry.EditorData;
import com.fukurou.telemetry.SessionMetadata;
import com.fukurou.telemetry.TelemetryData;

/**
 * Assembles the ordered list of {@link com.fukurou.report.block.ReportBlock}s that
 * make up the PDF report from a {@link TelemetryData} snapshot.
 *
 * <p><b>Design pattern: Builder.</b> It constructs a complex, multi-section document
 * step by step (headings, KPI strip, charts, tables), keeping that layout logic out
 * of {@link PdfGenerator}.
 */
public class ReportBuilder {
    /** Font for body text. */
    private static final PDFont BODY_FONT    = PDType1Font.HELVETICA;
    /** Font for headings. */
    private static final PDFont HEADING_FONT  = PDType1Font.HELVETICA_BOLD;
    /** Body text size in points. */
    private static final float  BODY_SIZE     = 10f;
    /** Heading text size in points. */
    private static final float  HEADING_SIZE  = 16f;
    /** Line spacing for body text in points. */
    private static final float  BODY_LEADING  = 15f;
    /** Line spacing for headings in points. */
    private static final float  HEADING_LEADING = 24f;

    /** Creates a report builder. */
    public ReportBuilder(){
    }

    /**
     * Builds the report blocks in order: title, session details, summary figures,
     * the active/idle and compile-outcome charts side by side, the error frequency
     * chart, the class timeline chart, and a list of error messages with their counts.
     *
     * @param data the telemetry to report on
     * @return the blocks in the order they should be rendered
     */
    public List<ReportBlock> build(TelemetryData data){
        List<ReportBlock> blocks = new ArrayList<>();
        EditorData ed = data.getEditorData();
        CompileData cd = data.getCompileData();

        //Page 1
        blocks.add(heading("Fukurou Telemetry Report"));
        blocks.add(text(sessionHeader(data)));
        blocks.add(text(kpiStrip(ed, cd)));

        blocks.add(new RowBlock(20f, new ChartBlock(Charts.activeIdleDonut(ed), 200, 200), new ChartBlock(Charts.compileOutcome(cd), 200, 200)));

        blocks.add(heading("Compilation error frequency"));
        blocks.add(new ChartBlock(Charts.errorBar(cd.getErrorInfo()), 500, 220));

        //Page 2
        blocks.add(heading("Class event timeline"));
        blocks.add(new ChartBlock(Charts.classTimeline(data.getEventLog()), 500, 260));

        blocks.add(heading("Compilation error detail"));
        blocks.addAll(errorTable(cd.getErrorInfo()));

        return blocks;
    }

    /**
     * @param s the heading text
     * @return a text block in the heading font
     */
    private ReportBlock heading(String s){
        return new TextBlock(s, HEADING_FONT, HEADING_SIZE, HEADING_LEADING);
    }

    /**
     * @param s the body text
     * @return a text block in the body font
     */
    private ReportBlock text(String s){
        return new TextBlock(s, BODY_FONT, BODY_SIZE, BODY_LEADING);
    }

    /**
     * Session details: student ID, start and end times, and session duration.
     * The duration shown is {@link EditorData#getTotalDuration()}.
     *
     * @param data the telemetry
     * @return the text, one item per line
     */
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

    /**
     * Summary figures: active and idle time, keystrokes and keystrokes per minute,
     * compile attempts and success rate.
     *
     * @param ed the editor metrics
     * @param cd the compile counters
     * @return the text, three lines
     */
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

    /**
     * One text block per distinct error message with its count, in first-seen order.
     *
     * @param errors the error messages, one entry per occurrence; may be {@code null}
     * @return the blocks, or a single "No compilation errors recorded." block if there are none
     */
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

    /**
     * Successful compiles as a percentage of compiles started.
     *
     * @param cd the compile counters
     * @return the rounded percentage, e.g. {@code "75%"}, or {@code "n/a"} if no compiles were started
     */
     private String successRate(CompileData cd) {
        int total = cd.getCompileCount();
        if (total == 0) return "n/a";
        return Math.round(100.0 * cd.getCompileSuccess() / total) + "%";
    }

    /**
     * @param d the value; may be {@code null}
     * @return the value to one decimal place, or {@code "n/a"} if {@code null}
     */
    private String formatDouble(Double d) {
        return d == null ? "n/a" : String.format("%.1f", d);
    }

    /**
     * Formats a duration as {@code "1h 2m 3s"}, {@code "2m 3s"} or {@code "3s"},
     * dropping leading zero units. Milliseconds are discarded.
     *
     * @param ms the duration in milliseconds
     * @return the formatted duration
     */
    private String formatDuration(long ms) {
        long totalSec = ms / 1000;
        long h = totalSec / 3600, m = (totalSec % 3600) / 60, sec = totalSec % 60;
        if (h > 0) return String.format("%dh %dm %ds", h, m, sec);
        if (m > 0) return String.format("%dm %ds", m, sec);
        return sec + "s";
    }
}
