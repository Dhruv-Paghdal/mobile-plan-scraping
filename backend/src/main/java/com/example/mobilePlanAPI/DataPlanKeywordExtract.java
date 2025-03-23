package com.example.mobilePlanAPI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataPlanKeywordExtract {
    public void parseCSV(String dataPlanFileName, DataPlanAVLTree dataPlanAvlTree) {
        try (BufferedReader objectBufferedReader = new BufferedReader(new FileReader(dataPlanFileName))) {
            String lineOfFile;
            while ((lineOfFile = objectBufferedReader.readLine()) != null) {
                String[] rows = lineOfFile.split(",");
                for (String rowData : rows) {
                    String[] words = rowData.toLowerCase().split("[-+\\s]+");
                    for (String word : words) {
                        if (!word.isEmpty()) {
                            dataPlanAvlTree.insertKeyword(word.toLowerCase());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
