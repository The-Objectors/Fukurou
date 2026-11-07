package com.fukurou;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonGenerator implements Generator{

    @Override
    public void generate(TelemetryData data){
        ObjectMapper om = new ObjectMapper();

        File dir = new File(System.getProperty("user.home"), "fukurou");
        File file = new File(dir, "telemetry_data.json");
        
        try{
            if(!dir.exists() && !dir.mkdirs()){
                throw new IOException("Could not create directory" + dir.getAbsolutePath());
            }

            om.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.println("Telemetry data file saved to: " + file.getAbsolutePath());
        }
        catch(IOException e){
            System.out.println("An error occurred while writing data to json.");
            e.printStackTrace();
        }
    }
}
