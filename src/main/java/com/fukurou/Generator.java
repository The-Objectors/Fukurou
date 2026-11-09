package com.fukurou;

import java.io.IOException;

public interface Generator {
    void generate(TelemetryData data) throws IOException;
}
