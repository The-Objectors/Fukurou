package com.fukurou.report.block;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * 
 * <p><b>Design pattern: Composite.</b> Leaves ({@link TextBlock}, {@link ChartBlock})
 * and composites ({@link RowBlock}) implement this same type, so
 * {@link com.fukurou.report.ReportRenderer} can measure and draw any node, leaf or
 * container through {@link #height} and {@link #render} without knowing its concrete
 * kind.
 */
public interface ReportBlock {
    float height(float contentWidth) throws IOException;

    void render(PDDocument doc, PDPageContentStream content,
        float x, float yTop, float contentWidth) throws IOException;
}
