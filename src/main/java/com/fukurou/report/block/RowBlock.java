package com.fukurou.report.block;

import java.util.List;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import java.io.IOException;

/**
 * Composite {@link ReportBlock} that lays its child blocks out side by side,
 * splitting the available width between them. Because a row is itself a
 * {@link ReportBlock}, rows can contain rows (Composite pattern).
 */
public class RowBlock implements ReportBlock{
    /** Child blocks, drawn left to right. */
    private final List<ReportBlock> children;
    /** Horizontal space between children in points. */
    private final float gap;

    /**
     * Creates a row of the given blocks.
     *
     * @param gap  horizontal space between children in points
     * @param kids the child blocks, left to right; at least one is required
     */
    public RowBlock(float gap, ReportBlock... kids){
        this.gap = gap;
        this.children = Arrays.asList(kids);
    }

    /**
     * Width given to each child: the available width minus the gaps, split equally.
     *
     * @param w the available width in points
     * @return the width of each child in points
     */
    private float childWidth(float w){
        return (w - gap * (children.size() - 1)) / children.size();
    }

    /**
     * Height of the tallest child at its share of the width.
     *
     * @param w the available width in points
     * @return the row height in points
     * @throws IOException if a child cannot be measured
     */
    @Override
    public float height(float w) throws IOException{
        float cw = childWidth(w);
        float max = 0;

        for(ReportBlock block : children){
            max = Math.max(max, block.height(cw));
        }

        return max;
    }

    /**
     * Draws each child left to right, all aligned to the same top edge.
     *
     * @param doc  the document being written
     * @param c    the content stream to draw on
     * @param x    left edge in points
     * @param yTop top edge in points
     * @param w    the available width in points
     * @throws IOException if a child cannot be drawn
     */
    @Override
    public void render(PDDocument doc, PDPageContentStream c,
        float x, float yTop, float w) throws IOException{

        float cw = childWidth(w);
        float cx = x;

        for(ReportBlock block : children){
            block.render(doc, c, cx, yTop, cw);
            cx = cx + cw + gap;
        }
    }
}
