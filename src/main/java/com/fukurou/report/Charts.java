package com.fukurou.report;

import java.awt.Color;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fukurou.telemetry.CompileData;
import com.fukurou.telemetry.EditorData;
import com.fukurou.telemetry.EventLog;
import com.fukurou.telemetry.TelemetryEvent;

/**
 * Static factory methods that build the JFreeChart charts used in the PDF report.
 * Each method reads from a telemetry model and returns a styled chart without a title.
 */
public final class Charts {

    /** Not instantiable; all methods are static. */
    private Charts() {}

    /** Colour for active time, error bars and the class timeline. */
    private static final Color BLUE   = new Color(0x0072B2);
    /** Colour for idle time. */
    private static final Color GREY   = new Color(0x999999);
    /** Colour for failed compiles. */
    private static final Color RED    = new Color(0xD55E00);
    /** Colour for compile warnings. */
    private static final Color YELLOW = new Color(0xF0E442);
    /** Colour for successful compiles. */
    private static final Color GREEN  = new Color(0x009E73);
    /** Colour for compile errors. */
    private static final Color ORANGE = new Color(0xE69F00);

    /**
     * Ring chart of active typing time against idle time, with a legend.
     *
     * @param ed the editor metrics; uses {@link EditorData#getActiveTypingMs()}
     *           and {@link EditorData#getTotalIdleMs()}
     * @return the chart
     */
    public static JFreeChart activeIdleDonut(EditorData ed) {
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        ds.setValue("Active", ed.getActiveTypingMs());
        ds.setValue("Idle", ed.getTotalIdleMs());

        JFreeChart chart = ChartFactory.createRingChart(null, ds, true, false, false);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionPaint("Active", BLUE);
        plot.setSectionPaint("Idle", GREY);
        styleRing(plot);
        return chart;
    }

    /**
     * Ring chart of compile success, failure, error and warning counts, with a legend.
     *
     * @param cd the compile counters
     * @return the chart
     */
    public static JFreeChart compileOutcome(CompileData cd) {
        DefaultPieDataset<String> ds = new DefaultPieDataset<>();
        ds.setValue("Success", cd.getCompileSuccess());
        ds.setValue("Failed", cd.getCompileFail());
        ds.setValue("Errors", cd.getCompileError());
        ds.setValue("Warnings", cd.getCompileWarn());

        JFreeChart chart = ChartFactory.createRingChart(null, ds, true, false, false);
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionPaint("Success", GREEN);
        plot.setSectionPaint("Failed", RED);
        plot.setSectionPaint("Errors", ORANGE);
        plot.setSectionPaint("Warnings", YELLOW);
        styleRing(plot);
        return chart;
    }

    /**
     * Horizontal bar chart of how often each distinct error message occurs,
     * sorted from most to least frequent. Labels longer than 45 characters are truncated.
     *
     * @param errors the error messages, one entry per occurrence; may be {@code null}
     * @return the chart
     */
    public static JFreeChart errorBar(List<String> errors) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        frequencies(errors).entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .forEach(e -> ds.addValue((Number) e.getValue(), "Errors", truncate(e.getKey(), 45)));

        JFreeChart chart = ChartFactory.createBarChart(
                null, "Error", "Count", ds, PlotOrientation.HORIZONTAL, false, false, false);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, BLUE);
        renderer.setShadowVisible(false);
        plot.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    /**
     * Step chart of the running class count over time. Class create events add
     * one and class delete events subtract one (never below zero); other events
     * are skipped. Time is in minutes since the first class event in the log.
     * If there are no create or delete events the chart shows a single point at (0, 0).
     *
     * @param log the event log
     * @return the chart
     */
    public static JFreeChart classTimeline(EventLog log) {
        XYSeries series = new XYSeries("Classes");
        Instant start = null;
        int count = 0;

        for (TelemetryEvent ev : log.getEvents()) {
            if (!"class".equals(ev.getTarget())) {
                continue;
            }
            Instant t = Instant.parse(ev.getTimestamp());
            if (start == null) {
                start = t;
            }
            double elapsedMin = (t.toEpochMilli() - start.toEpochMilli()) / 60000.0;

            if ("create".equals(ev.getType())) {
                count++;
            } else if ("delete".equals(ev.getType())) {
                count = Math.max(0, count - 1);
            } else {
                continue;
            }
            series.add(elapsedMin, count);
        }
        if (series.isEmpty()) {
            series.add(0.0, 0.0);
        }

        XYSeriesCollection ds = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                null, "Minutes into session", "Classes (cumulative)",
                ds, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = chart.getXYPlot();
        XYStepRenderer step = new XYStepRenderer();
        step.setSeriesPaint(0, BLUE);
        plot.setRenderer(step);
        plot.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    /**
     * Applies the shared ring-chart style: white background, no outline,
     * shadow or section labels.
     *
     * @param plot the ring plot to style
     */
    private static void styleRing(RingPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setSectionDepth(0.35);
        plot.setLabelGenerator(null);
    }

    /**
     * Counts occurrences of each distinct string.
     *
     * @param errors the strings to count; may be {@code null}
     * @return counts keyed by string, in first-seen order; empty if {@code errors} is {@code null}
     */
    private static Map<String, Integer> frequencies(List<String> errors) {
        Map<String, Integer> freq = new LinkedHashMap<>();
        if (errors != null) {
            for (String e : errors) {
                freq.merge(e, 1, Integer::sum);
            }
        }
        return freq;
    }

    /**
     * Shortens a string that is longer than {@code max} characters.
     *
     * @param s   the string
     * @param max the length above which the string is shortened
     * @return {@code s} unchanged, or its first {@code max - 1} characters followed by {@code "..."}
     */
    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "...";
    }
}
