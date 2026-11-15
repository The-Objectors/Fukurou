package com.fukurou.report;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fukurou.telemetry.TelemetryData;
public class PdfGeneratorTest {
    @TempDir
    Path tempDir;

    @Test
    void generatesPdfWithAtLeastOnePage() throws Exception {
        String originalHome = System.getProperty("user.home");
        
        //this test saves the pdf inside a temp folder in junit and checks if the file exists and has at least one page
        try{
            System.setProperty("user.home", tempDir.toString());
            File file = new PdfGenerator().generate(new TelemetryData());

            assertTrue(file.exists(), "The PDF file should exist");
            assertTrue(file.length() > 0, "The PDF should not be empty");

            try(PDDocument document = PDDocument.load(file)){
                assertTrue(document.getNumberOfPages() >= 1);
            }
        }
        finally {
            if(originalHome == null){
                System.clearProperty("user.home");
            }
            else {
                System.setProperty("user.home", originalHome);
            }
        }
    }
}
