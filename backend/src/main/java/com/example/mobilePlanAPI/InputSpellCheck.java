package com.example.mobilePlanAPI;
import java.io.*;
import java.util.*;

import static com.example.mobilePlanAPI.spellcheck.buildVocabulary;
import static com.example.mobilePlanAPI.spellcheck.getSuggestions;


public class InputSpellCheck {
    public static List<String> checkInputAndGetSuggestion(String inputFromUser) throws IOException {
        List<String> suggestions = new ArrayList<>();
        CHT v = new CHT();
        buildVocabulary("bell.csv");
        buildVocabulary("telus.csv");
        buildVocabulary("freedom.csv");
        if (v.cts(inputFromUser)) {
            suggestions.add("Correct Input");
        } else {
            List<String> suggestionFound = getSuggestions(inputFromUser);
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
