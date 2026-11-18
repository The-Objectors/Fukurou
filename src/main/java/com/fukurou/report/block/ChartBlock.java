package com.fukurou.report.block;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.JFreeChart;

/** Leaf {@link ReportBlock} that draws a JFreeChart as an image (Composite pattern). */
public class ChartBlock implements ReportBlock{
    /** The chart to draw. */
    private final JFreeChart chart;
    /** Drawn width of the chart in points. */
    private final float width;
    /** Drawn height of the chart in points. */
    private final float chartHeight;

    /**
     * Creates a chart block of fixed size.
     *
     * @param chart       the chart to draw
     * @param width       drawn width in points
     * @param chartHeight drawn height in points
     */
    public ChartBlock(JFreeChart chart, float width, float chartHeight){
        this.chart = chart;
        this.width = width;
        this.chartHeight = chartHeight;
    }

    /**
     * The chart height plus 20 points of space below it.
     *
     * @param w the available width (not used; the chart has a fixed size)
     * @return the block height in points
     */
    @Override
    public float height(float w){
        return chartHeight + 20;
    }

    /**
     * Renders the chart to an image at twice the drawn size and draws it with its
     * top-left corner at ({@code x}, {@code yTop}) at the fixed width and height.
     *
     * @param doc  the document the image is added to
     * @param c    the content stream to draw on
     * @param x    left edge in points
     * @param yTop top edge in points
     * @param w    the available width (not used; the chart has a fixed size)
     * @throws IOException if the image cannot be created or drawn
     */
    @Override
    public void render(PDDocument doc, PDPageContentStream c,
        float x, float yTop, float w) throws IOException{
        BufferedImage img = chart.createBufferedImage((int)(width*2), (int)(chartHeight*2));
        PDImageXObject pd = LosslessFactory.createFromImage(doc, img);
        c.drawImage(pd, x, yTop - chartHeight, width, chartHeight);
    }
}
