package com.fukurou;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Collections;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;

class ReportRendererTest {
    @Test
    void rejectsBlockTallerThanPrintablePage() throws IOException {
        ReportBlock oversizedBlock = new ReportBlock() {
            @Override
            public float height(float contentWidth) {
                return 701f;
            }

            @Override
            public void render(PDDocument doc, PDPageContentStream content,
                    float x, float yTop, float contentWidth) {
            }
        };

        try (PDDocument document = new PDDocument()) {
            assertThrows(IOException.class,
                () -> new ReportRenderer().render(
                    document, Collections.singletonList(oversizedBlock)
                ));
        }
    }
}
