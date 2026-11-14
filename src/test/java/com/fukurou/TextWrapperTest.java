package com.fukurou;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

class TextWrapperTest {
    private final TextWrapper wrapper = new TextWrapper();

    @Test
    void preservesExplicitAndBlankLines() throws IOException {
        List<String> lines = wrapper.wrap("first\n\nlast", PDType1Font.HELVETICA, 10f, 500f);

        assertEquals(Arrays.asList("first", "", "last"), lines);
    }

    @Test
    void splitsLongWordsWithoutExceedingWidth() throws IOException {
        float maxWidth = PDType1Font.HELVETICA.getStringWidth("ABCD") / 1000f * 10f;
        List<String> lines = wrapper.wrap(
            "ABCDEFGHIJKL", PDType1Font.HELVETICA, 10f, maxWidth
        );

        assertEquals("ABCDEFGHIJKL", String.join("", lines));
        for (String line : lines) {
            assertTrue(PDType1Font.HELVETICA.getStringWidth(line) / 1000f * 10f <= maxWidth);
        }
    }
}
