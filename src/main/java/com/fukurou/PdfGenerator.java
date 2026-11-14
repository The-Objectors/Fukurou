package com.fukurou;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfGenerator implements Generator{

    @Override
    public void generate(TelemetryData data) throws IOException {
        File directory = new File(System.getProperty("user.home"), "fukurou");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create directory: " + directory.getAbsolutePath());
        }

        File output = new File(directory, "Telemetry_report.pdf");

        try (PDDocument document = new PDDocument()) {
            List<ReportBlock> blocks = new ReportBuilder().build(data);
            new ReportRenderer().render(document, blocks);
            document.save(output);
        }

        System.out.println("PDF report saved to: " + output.getAbsolutePath());
    }
}
