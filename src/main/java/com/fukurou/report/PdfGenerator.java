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

    /** Creates a PDF generator. */
    public PdfGenerator(){
    }



    /**
     * Builds the report blocks, lays them out with {@link ReportRenderer}, saves the
     * PDF to the path given by {@link OutputPaths} (replacing any existing file there)
     * and prints the file's path to standard output.
     *
     * @param data the telemetry to report on
     * @return the PDF file that was written
     * @throws IOException if the output directory cannot be created, a block does not
     *                     fit on a page, or the file cannot be written
     */
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
