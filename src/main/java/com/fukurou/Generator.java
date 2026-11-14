package com.fukurou;

import java.io.File;
import java.io.IOException;

public interface Generator {
    File generate(TelemetryData data) throws IOException;
}
