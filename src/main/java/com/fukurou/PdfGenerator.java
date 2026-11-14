package com.fukurou;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfGenerator implements Generator{

    @Override
    public File generate(TelemetryData data) throws IOException {
        File output = OutputPaths.pdfFile();

        try (PDDocument document = new PDDocument()) {
            List<ReportBlock> blocks = new ReportBuilder().build(data);
            new ReportRenderer().render(document, blocks);
            document.save(output);
        }

        System.out.println("PDF report saved to: " + output.getAbsolutePath());
        return output;
    }
}
