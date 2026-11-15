package com.fukurou.report;

import java.util.List;
import org.junit.jupiter.api.Test;

import com.fukurou.report.block.ChartBlock;
import com.fukurou.report.block.ReportBlock;
import com.fukurou.report.block.RowBlock;
import com.fukurou.report.block.TextBlock;
import com.fukurou.telemetry.TelemetryData;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import static org.junit.jupiter.api.Assertions.*;
public class ReportBuilderTest {
    @Test
    void buildsReport() throws Exception {
        
        //creates 10 blocks for empty data and checks if the correct error message shows in the report
        List <ReportBlock> blocks = new ReportBuilder().build(new TelemetryData());

        assertEquals(10, blocks.size());
        assertTrue(blocks.get(0) instanceof TextBlock);
        assertTrue(blocks.get(1) instanceof TextBlock);
        assertTrue(blocks.get(2) instanceof TextBlock);
        assertTrue(blocks.get(3) instanceof RowBlock);
        assertTrue(blocks.get(4) instanceof TextBlock);
        assertTrue(blocks.get(5) instanceof ChartBlock);
        assertTrue(blocks.get(6) instanceof TextBlock);
        assertTrue(blocks.get(7) instanceof ChartBlock);
        assertTrue(blocks.get(8) instanceof TextBlock);
        assertTrue(blocks.get(9) instanceof TextBlock);

        try(PDDocument document = new PDDocument()){
            new ReportRenderer().render(document,blocks);
            String reportText = new PDFTextStripper().getText(document);
            assertTrue(reportText.contains("No compilation errors recorded."));
        }

    }

}
