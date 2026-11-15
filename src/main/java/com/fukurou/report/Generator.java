package com.fukurou.report;

import java.io.File;
import java.io.IOException;

import com.fukurou.telemetry.TelemetryData;

public interface Generator {
    File generate(TelemetryData data) throws IOException;
}
