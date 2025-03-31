package com.example.mobilePlanAPI;

import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

import static com.example.mobilePlanAPI.GetAllPlans.readSpecificCSV;
import static com.example.mobilePlanAPI.InputSpellCheck.checkInputAndGetSuggestion;
import static com.example.mobilePlanAPI.PageRanking.getPageRank;
import static com.example.mobilePlanAPI.SearchFrequency.readFile;
import static com.example.mobilePlanAPI.SearchFrequency.writeFile;

//Controller to handle the end-points
@RestController
public class Controller {

    //Method to get data of each provider
    @GetMapping("/get-all-plans")
    public String getAllPlans(@RequestParam String companyName){
        String fileToRead;
        //Switching based on company name
        switch(companyName) {
            case "freedom":
                fileToRead = "freedom.csv";
                break;
            case "telus":
                fileToRead = "telus.csv";
                break;
            case "bell":
                fileToRead = "bell.csv";
                break;
            case "virgin":
                fileToRead = "virgin.csv";
                break;
            case "rogers":
                fileToRead = "rogers.csv";
                break;
            default:
                fileToRead = "freedom.csv";
        }
        //Getting the response
        String response = readSpecificCSV(fileToRead);
        return response;
    }

    //Method to check the spelling and provide suggestions
    @GetMapping("/spell-check")
    public List<String> checkInput(@RequestParam String input) throws IOException {
        //Getting the response
        List<String> response = checkInputAndGetSuggestion(input);
        return response;
    }

    //Method to get suggestion for word completion
    @GetMapping("/getsuggestion")
    public List<String> getWordSuggestion(@RequestParam String text) {

        Hashtable<String, Integer> temptable = new Hashtable<>();
        ArrayList<String> sorted_suggestions = new ArrayList<>();
        ArrayList<Integer> suggestions = new ArrayList<>();


        Dictionary dict = new Dictionary();
        GetAllPlans gap = new GetAllPlans();
        FrequencyCount sf = new FrequencyCount();

        // function call to extract unique words from the files
        List<String> planWords = gap.convertStringtoWords();
        int val=0;

        // words extracted from the files are put into trie to create a vocabulary/dictionary
        for(String word: planWords) {
            dict.put(word, val++);
            sf.insert(word);
        }

        //words matching a prefix are stored in a temporary DS
        for(Object w : dict.keysWithPrefix(text)) {
            temptable.put((String) w, sf.search((String) w));
        }

        // get the frequency count of the suggestion words
        for (Map.Entry<String, Integer> entry : temptable.entrySet()) {
            suggestions.add(entry.getValue());
        }

        // sort words based on the frequency count
        Collections.sort(suggestions, Collections.reverseOrder());
        for (int num : suggestions) {
            for (Entry<String, Integer> entry : temptable.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sorted_suggestions.add(entry.getKey());
                }
            }
        }

        return sorted_suggestions;
    }

    //Method to display final result based on page ranking
    @GetMapping("/page-ranking")
    public String getPageRanking(@RequestParam String input) throws IOException {

        Map<String, Integer> wordMap = readFile("freq.txt");
        if (wordMap.containsKey(input)) {
            // Increment the count
            wordMap.put(input, wordMap.get(input) + 1);
        } else {
            // Add a new word with count 1
            wordMap.put(input, 1);
        }

        // Write updated content back to the file
        writeFile("freq.txt", wordMap);
        return getPageRank(input);
    }

    @GetMapping("/search-freq")
    public List<String> searchFreq() throws IOException {
        SearchFrequency sf = new SearchFrequency();
        List<String> l = new ArrayList<>();
        sf.createMap();
        for(String w: sf.mostSearchedWords()) {
            l.add(w);
        }

        return l;
    }
}
