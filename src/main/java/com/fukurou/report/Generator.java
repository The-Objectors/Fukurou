package com.fukurou.report;

import java.io.File;
import java.io.IOException;

import com.fukurou.telemetry.TelemetryData;

/**
 * Strategy interface for report output formats.
 *
 * <p><b>Design pattern: Strategy.</b> Each concrete strategy
 * ({@link JsonGenerator}, {@link PdfGenerator}) encapsulates one way of turning the
 * collected {@link TelemetryData} into a file. The context ({@code Fukurou}) holds
 * its generators behind this interface, so a new output format is added by writing
 * one more implementation without touching the context.
 */
public interface Generator {
    File generate(TelemetryData data) throws IOException;
}
