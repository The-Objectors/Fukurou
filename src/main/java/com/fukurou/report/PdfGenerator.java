package com.fukurou.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.fukurou.report.block.ReportBlock;
import com.fukurou.telemetry.TelemetryData;

/**
 * Concrete {@link Generator} strategy that renders {@link TelemetryData} to a PDF.
 * It assembles the page content with {@link ReportBuilder} (Builder) over the
 * {@link com.fukurou.report.block.ReportBlock} composite, then writes it with PDFBox.
 */
public class PdfGenerator implements Generator{

    

    @Override
    public File generate(TelemetryData data) throws IOException {
        File output = OutputPaths.pdfFile(data);

        try (PDDocument document = new PDDocument()) {
            List<ReportBlock> blocks = new ReportBuilder().build(data);
            new ReportRenderer().render(document, blocks);
            document.save(output);
        }

        System.out.println("PDF report saved to: " + output.getAbsolutePath());
        return output;
    }
}
