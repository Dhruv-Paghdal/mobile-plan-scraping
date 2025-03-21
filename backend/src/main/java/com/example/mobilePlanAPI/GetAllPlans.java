package com.example.mobilePlanAPI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GetAllPlans {
    public static String readSpecificCSV(String filePath) {
        StringBuilder jsonRes = new StringBuilder("[");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return "[]";
            }
            String[] headers = headerLine.split(",");
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (!firstLine) {
                    jsonRes.append(",");
                }
                firstLine = false;
                String[] values = line.split(",");
                jsonRes.append("\n  {");
                for (int i = 0; i < headers.length; i++) {
                    jsonRes.append("\"").append(headers[i].trim()).append("\": ")
                            .append("\"").append(values[i].trim()).append("\"");
                    if (i < headers.length - 1) {
                        jsonRes.append(", ");
                    }
                }
                jsonRes.append("}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonRes.append("\n]");
        return jsonRes.toString();
    }
}
