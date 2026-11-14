package com.fukurou;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class ReportRenderer {
    private static final float MARGIN_X = 50f;
    private static final float TOP_Y = 750f;
    private static final float BOTTOM_Y = 50f;
    private final PDRectangle pageSize = PDRectangle.LETTER;

    public void render(PDDocument doc, List<ReportBlock> blocks) throws IOException{
        float contentWidth = pageSize.getWidth() - (2 * MARGIN_X);

        PDPage page = newPage(doc);
        PDPageContentStream content = new PDPageContentStream(doc, page);
        float y = TOP_Y;

        try {
            for(ReportBlock block : blocks){
                float h = block.height(contentWidth);
                
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

    private PDPage newPage(PDDocument doc){
        PDPage p = new PDPage(pageSize);
        doc.addPage(p);
        return p;
    }
}
