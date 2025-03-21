package com.example.mobilePlanAPI;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.example.mobilePlanAPI.GetAllPlans.readSpecificCSV;

@RestController
public class Controller {

    @PostMapping ("/get-all-plans")
    public String getAllPlans(@RequestBody Map<String, String> request){
        String companyName = request.get("companyName");
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

}
