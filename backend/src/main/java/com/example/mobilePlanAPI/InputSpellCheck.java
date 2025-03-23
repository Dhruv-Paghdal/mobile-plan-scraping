package com.example.mobilePlanAPI;
import java.io.*;
import java.util.*;

import static com.example.mobilePlanAPI.spellcheck.buildVocabulary;
import static com.example.mobilePlanAPI.spellcheck.getSuggestions;


public class InputSpellCheck {
    //Method to check the input from user for spelling and providing suggestion
    public static List<String> checkInputAndGetSuggestion(String inputFromUser) throws IOException {
        List<String> suggestions = new ArrayList<>();
        CHT v = new CHT();
        //Building vocabulary using cuckoo hash table
        buildVocabulary("bell.csv");
        buildVocabulary("telus.csv");
        buildVocabulary("freedom.csv");
        buildVocabulary("rogers.csv");
        buildVocabulary("virgin.csv");
        if (v.cts(inputFromUser)) {
            suggestions.add("Correct Input");
        } else {
            List<String> suggestionFound = getSuggestions(inputFromUser);
            //Returning suggestion if found
            if(suggestionFound.size() > 3) {
                suggestions = suggestionFound.subList(0,3);
            }
            else {
                suggestions = suggestionFound;
            }
        }
        return suggestions;
    }

}
