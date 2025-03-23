package com.example.mobilePlanAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.mobilePlanAPI.GetAllPlans.readSpecificCSV;

public class PageRanking {
    //Method to display final data based on page rank
    public static String getPageRank(String inputFromUser) {
        //Initialize the page ranker
        DataPlanPageRanking rankCalculator = new DataPlanPageRanking();
        String[] fileNames = {"bell.csv", "telus.csv", "freedom.csv", "rogers.csv", "virgin.csv"};
        //Getting the user input
        List<String> searchKeywords = Arrays.asList(inputFromUser.split(","));
        searchKeywords.replaceAll(String::trim);
        //Generating the page rank for each file
        rankCalculator.analyzeCSVFiles(fileNames, searchKeywords);
        List<String> providerList = new ArrayList<>();
        for (String entry : rankCalculator.showRankings()) {
            String[] parts = entry.split("-");
            int count = Integer.parseInt(parts[2]);
            if (count > 0) {
                String planProvider = parts[1].replace(".csv", "");
                providerList.add(planProvider);
            }
        }
        if(providerList.size() == 0){
            providerList.add("freedom");
        }
        StringBuilder finalResponse = new StringBuilder();
        //Combining all the data
        for (String provider : providerList) {
            finalResponse.append(readSpecificCSV(provider+".csv"));
        }
        return finalResponse.toString().replaceAll("\\]\\[", ",");
    }
}
