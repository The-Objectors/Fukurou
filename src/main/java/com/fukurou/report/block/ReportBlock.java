package com.fukurou.report.block;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * A rectangular piece of PDF report content that can report its height and draw itself.
 *
 * <p><b>Design pattern: Composite.</b> Leaves ({@link TextBlock}, {@link ChartBlock})
 * and composites ({@link RowBlock}) implement this same type, so
 * {@link com.fukurou.report.ReportRenderer} can measure and draw any node, leaf or
 * container through {@link #height} and {@link #render} without knowing its concrete
 * kind.
 */
public interface ReportBlock {
    /**
     * Height the block takes up when drawn at the given width.
     *
     * @param contentWidth the available width in points
     * @return the height in points
     * @throws IOException if measuring text fails
     */
    float height(float contentWidth) throws IOException;

    /**
     * Draws the block with its top-left corner at ({@code x}, {@code yTop}).
     *
     * @param doc          the document being written
     * @param content      the content stream of the current page
     * @param x            left edge in points
     * @param yTop         top edge in points (PDF coordinates, measured up from the bottom of the page)
     * @param contentWidth the available width in points
     * @throws IOException if drawing fails
     */
    void render(PDDocument doc, PDPageContentStream content,
        float x, float yTop, float contentWidth) throws IOException;
}
