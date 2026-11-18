package com.fukurou.report;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.fukurou.report.block.ReportBlock;

/**
 * Lays out {@link ReportBlock}s top to bottom on US Letter pages, starting a new
 * page when the next block does not fit in the space left on the current one.
 */
public class ReportRenderer {
    /** Left and right page margin in points. */
    private static final float MARGIN_X = 50f;
    /** Y coordinate in points where content starts on each page. */
    private static final float TOP_Y = 750f;
    /** Lowest Y coordinate in points that content may reach. */
    private static final float BOTTOM_Y = 50f;
    /** Page size used for every page. */
    private final PDRectangle pageSize = PDRectangle.LETTER;

    /** Creates a renderer that uses US Letter pages. */
    public ReportRenderer(){
    }

    /**
     * Adds pages to the document and draws the blocks on them in order.
     * A block that does not fit on the current page is moved to a new page,
     * unless the current page is still empty.
     *
     * @param doc    the document to add pages to
     * @param blocks the blocks to draw, in order
     * @throws IOException if a block is taller than the printable page height,
     *                     or if PDFBox fails to write the content
     */
    public void render(PDDocument doc, List<ReportBlock> blocks) throws IOException{
        float contentWidth = pageSize.getWidth() - (2 * MARGIN_X);

        PDPage page = newPage(doc);
        PDPageContentStream content = new PDPageContentStream(doc, page);
        float y = TOP_Y;

        try {
            for(ReportBlock block : blocks){
                float h = block.height(contentWidth);

                if (h > TOP_Y - BOTTOM_Y) {
                    throw new IOException("Report block height (" + h
                        + " pt) exceeds the printable page height ("
                        + (TOP_Y - BOTTOM_Y) + " pt)");
                }

                if(y - h < BOTTOM_Y && y < TOP_Y){
                    content.close();
                    page = newPage(doc);
                    content = new PDPageContentStream(doc, page);
                    y = TOP_Y;
                }

                block.render(doc, content, MARGIN_X, y, contentWidth);
                y -= h;
            }
        } finally{
            content.close();
        }
    }

    /**
     * Creates a page of {@link #pageSize} and adds it to the document.
     *
     * @param doc the document
     * @return the new page
     */
    private PDPage newPage(PDDocument doc){
        PDPage p = new PDPage(pageSize);
        doc.addPage(p);
        return p;
    }
}
