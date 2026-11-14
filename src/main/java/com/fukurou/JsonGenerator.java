package com.fukurou;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

// implements the Strategy design pattern 
public class JsonGenerator implements Generator{

    @Override
    public File generate(TelemetryData data)throws IOException{
        ObjectMapper om = new ObjectMapper();

        File file = OutputPaths.jsonFile();
        
        om.writerWithDefaultPrettyPrinter().writeValue(file, data);
        System.out.println("Telemetry data file saved to: " + file.getAbsolutePath());
        return file;
    }
}
