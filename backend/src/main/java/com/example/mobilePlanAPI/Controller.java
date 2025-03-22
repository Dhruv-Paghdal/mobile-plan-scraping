package com.example.mobilePlanAPI;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.example.mobilePlanAPI.GetAllPlans.readSpecificCSV;
import static com.example.mobilePlanAPI.InputSpellCheck.checkInputAndGetSuggestion;

@RestController
public class Controller {

    @GetMapping("/get-all-plans")
    public String getAllPlans(@RequestParam String companyName){
        String fileToRead;
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
        String response = readSpecificCSV(fileToRead);
        return response;
    }

    @GetMapping("/spell-check")
    public List<String> checkInput(@RequestParam String input) throws IOException {
        List<String> response = checkInputAndGetSuggestion(input);
        return response;
    }
}
