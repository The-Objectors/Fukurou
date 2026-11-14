package com.fukurou;

import java.util.List;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import java.io.IOException;

public class RowBlock implements ReportBlock{
    private final List<ReportBlock> children;
    private final float gap;

    public RowBlock(float gap, ReportBlock... kids){
        this.gap = gap;
        this.children = Arrays.asList(kids);
    }

    private float childWidth(float w){
        return (w - gap * (children.size() - 1)) / children.size();
    }

    @Override 
    public float height(float w) throws IOException{
        float cw = childWidth(w); 
        float max = 0;
        
        for(ReportBlock block : children){
            max = Math.max(max, block.height(cw));
        }
        
        return max;
    }

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
