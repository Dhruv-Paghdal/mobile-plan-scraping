package com.example.mobilePlanAPI;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetAllPlans {
    public static String readSpecificCSV(String filePath) {
        StringBuilder jsonRes = new StringBuilder("[");
        try (BufferedReader br = new BufferedReader(new FileReader("/home/shubh/Desktop/mobile-plan-scraping/backend/" + filePath))) {
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


    public List<String> convertStringtoWords() {

        List<String> ls = new ArrayList<>();
        String[] nextRecord;

        String[] filenames = {"freedom.csv", "bell.csv"};

        for(String file : filenames) {
            try {
                CSVReader reader = new CSVReader(new FileReader("/home/shubh/Desktop/mobile-plan-scraping/backend/"+file));

                while((nextRecord = reader.readNext()) != null) {
                    for(String record: nextRecord) {
                        ls.addAll(Arrays.stream(record.replaceAll("[-+()/]", " ").split(" ")).filter(str -> str.matches("^[0-9a-zA-Z]+$")).map(String::toLowerCase).collect(Collectors.toList()));
                    }
                }
            } catch (IOException | CsvValidationException e) {
                throw new RuntimeException(e);
            }
        }

        return ls;
    }
}
