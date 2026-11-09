package com.fukurou;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

// implements the Strategy design pattern 
public class JsonGenerator implements Generator{

    @Override
    public void generate(TelemetryData data)throws IOException{
        ObjectMapper om = new ObjectMapper();

        File dir = new File(System.getProperty("user.home"), "fukurou");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create directory: " + dir.getAbsolutePath());
        }
        File file = new File(dir, "telemetry_data.json");
        
        om.writerWithDefaultPrettyPrinter().writeValue(file, data);
        System.out.println("Telemetry data file saved to: " + file.getAbsolutePath());
        
    }
}
