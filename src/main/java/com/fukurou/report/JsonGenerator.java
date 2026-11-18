package com.fukurou.report;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fukurou.telemetry.TelemetryData;

/**
 * Concrete {@link Generator} strategy that serialises {@link TelemetryData} to a
 * pretty-printed JSON file using Jackson. See {@link Generator} for the pattern.
 */
public class JsonGenerator implements Generator{

    /** Creates a JSON generator. */
    public JsonGenerator(){
    }

    /**
     * Writes the telemetry as pretty-printed JSON to the path given by
     * {@link OutputPaths}, replacing any existing file there, and prints the
     * file's path to standard output.
     *
     * @param data the telemetry to write
     * @return the JSON file that was written
     * @throws IOException if the output directory cannot be created or the file cannot be written
     */
    @Override
    public File generate(TelemetryData data)throws IOException{
        ObjectMapper om = new ObjectMapper();

        File file = OutputPaths.jsonFile(data);

        om.writerWithDefaultPrettyPrinter().writeValue(file, data);
        System.out.println("Telemetry data file saved to: " + file.getAbsolutePath());
        return file;
    }
}
