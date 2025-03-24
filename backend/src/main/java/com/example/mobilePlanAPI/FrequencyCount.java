package com.example.mobilePlanAPI;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FrequencyCount {
    HashMap<String, Integer> hashMap = new HashMap<>();

    /* insert a new word in the table
    * if the word exists increase the current count by 1
    * if it does not exists insert it
    * */
    public void insert(String key) {

        if(hashMap.get(key) == null) {
            hashMap.put(key, 1);
        } else {
            hashMap.put(key, hashMap.get(key)+1);
        }
    }

    // get the value of the word
    public Integer search(String key) {
        return hashMap.get(key);
    }
}
