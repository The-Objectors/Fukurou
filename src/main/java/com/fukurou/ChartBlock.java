package com.fukurou;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.JFreeChart;

public class ChartBlock implements ReportBlock{
    private final JFreeChart chart;
    private final float width, chartHeight;

    public ChartBlock(JFreeChart chart, float width, float chartHeight){
        this.chart = chart;
        this.width = width;
        this.chartHeight = chartHeight;
    }

    @Override
    public float height(float w){
        return chartHeight + 20;
    }

    @Override 
    public void render(PDDocument doc, PDPageContentStream c, 
        float x, float yTop, float w) throws IOException{
        BufferedImage img = chart.createBufferedImage((int)(width*2), (int)(chartHeight*2));
        PDImageXObject pd = LosslessFactory.createFromImage(doc, img);
        c.drawImage(pd, x, yTop - chartHeight, width, chartHeight); 
    }
}
