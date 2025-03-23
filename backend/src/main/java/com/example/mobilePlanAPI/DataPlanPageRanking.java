package com.example.mobilePlanAPI;

import java.util.*;

public class DataPlanPageRanking {
    private List<Map.Entry<String, Integer>> rankedPages; // Stores ranked page data

    public DataPlanPageRanking() {
        this.rankedPages = new ArrayList<>();
    }

    public void analyzeCSVFiles(String[] fileList, List<String> searchKeywords) {
        DataPlanKeywordExtract extractor = new DataPlanKeywordExtract();
        rankedPages.clear(); // Reset previous rankings

        // Map to store total keyword frequency per file
        Map<String, Integer> keywordFrequencyMap = new HashMap<>();

        for (String file : fileList) {
            DataPlanAVLTree keywordTree = new DataPlanAVLTree();
            extractor.parseCSV(file, keywordTree);

            int totalCount = 0;
            for (String keyword : searchKeywords) {
                totalCount += keywordTree.getKeywordCount(keyword);
            }
            keywordFrequencyMap.put(file, totalCount);
        }

        // Convert map entries into a list and sort by frequency in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(keywordFrequencyMap.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        rankedPages.addAll(sortedEntries);
    }

    public List<String> showRankings() {
        List<String> finalResult = new ArrayList<>();
        for (int i = 0; i < rankedPages.size(); i++) {
            Map.Entry<String, Integer> pageData = rankedPages.get(i);
            finalResult.add((i + 1) + "-" +pageData.getKey() + "-" + pageData.getValue());
        }
        return finalResult;
    }

    public static void main(String[] args) {
    }
}
