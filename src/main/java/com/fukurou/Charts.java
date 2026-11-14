package com.fukurou;

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

public final class Charts {

    private Charts() {}

    private static final Color BLUE   = new Color(0x0072B2);
    private static final Color GREY   = new Color(0x999999);
    private static final Color RED    = new Color(0xD55E00);
    private static final Color YELLOW = new Color(0xF0E442);
    private static final Color GREEN  = new Color(0x009E73);
    private static final Color ORANGE = new Color(0xE69F00);

    // Active typing vs idle time as a ring
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

    // Compilation outcome split (success/failed/errors/warnings) as a ring
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

    // Frequency of each distinct compilation error as a horizontal bar chart
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

    // Cumulative class count over the session as a step line
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

    private static void styleRing(RingPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setSectionDepth(0.35);
        plot.setLabelGenerator(null); 
    }

    private static Map<String, Integer> frequencies(List<String> errors) {
        Map<String, Integer> freq = new LinkedHashMap<>();
        if (errors != null) {
            for (String e : errors) {
                freq.merge(e, 1, Integer::sum);
            }
        }
        return freq;
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "...";
    }
}
