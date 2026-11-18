package com.fukurou.report.block;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/** Leaf {@link ReportBlock} that wraps and draws a run of text (Composite pattern). */
public class TextBlock implements ReportBlock{
    /** The text as given, which may contain {@code \n} line breaks. */
    private final String raw;
    /** Font used to measure and draw the text. */
    private final PDFont font;
    /** Font size in points. */
    private final float fontSize;
    /** Distance between line baselines in points. */
    private final float leading;
    /** Splits the text into lines that fit the width. */
    private final TextWrapper textWrapper = new TextWrapper();
    /** Wrapped lines from the last call, reused while the width is unchanged. */
    private List<String> cachedLines;
    /** Width the cached lines were wrapped to. */
    private float cachedWidth;

    /**
     * Creates a text block.
     *
     * @param text     the text; {@code \n} starts a new line
     * @param font     the font
     * @param fontSize the font size in points
     * @param leading  the distance between line baselines in points
     */
    public TextBlock(String text, PDFont font, float fontSize, float leading){
        this.raw = text;
        this.font = font;
        this.fontSize = fontSize;
        this.leading = leading;
    }

    /**
     * The text wrapped to the given width, reusing the previous result if the
     * width has not changed.
     *
     * @param width the available width in points
     * @return the wrapped lines
     * @throws IOException if measuring text fails
     */
    private List<String> lines(float width) throws IOException {
        if (cachedLines == null || Float.compare(cachedWidth, width) != 0) {
            List<String> wrapped = textWrapper.wrap(raw, font, fontSize, width);
            cachedLines = wrapped;
            cachedWidth = width;
        }
        return cachedLines;
    }

    /**
     * Number of wrapped lines multiplied by the leading.
     *
     * @param w the available width in points
     * @return the block height in points
     * @throws IOException if measuring text fails
     */
    @Override
    public float height(float w) throws IOException {
        return lines(w).size() * leading;
    }

    /**
     * Draws the wrapped lines. The first baseline is {@code fontSize} below
     * {@code yTop}; each following line is {@code leading} lower.
     *
     * @param doc  the document being written
     * @param c    the content stream to draw on
     * @param x    left edge in points
     * @param yTop top edge in points
     * @param w    the available width in points
     * @throws IOException if measuring or drawing text fails
     */
    @Override public void render(PDDocument doc, PDPageContentStream c,
            float x, float yTop, float w) throws IOException{

        float y = yTop - fontSize;
        for(String line : lines(w)){
            c.beginText();
            c.setFont(font, fontSize);
            c.newLineAtOffset(x, y);
            c.showText(line);
            c.endText();
            y -= leading;
        }

    }
}
