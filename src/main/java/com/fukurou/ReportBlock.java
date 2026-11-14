package com.fukurou;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public interface ReportBlock {
    float height(float contentWidth) throws IOException;

    void render(PDDocument doc, PDPageContentStream content,
        float x, float yTop, float contentWidth) throws IOException;
}
