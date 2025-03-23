package com.example.mobilePlanAPI;

import java.util.HashMap;

public class SearchFrequency {
    HashMap<String, Integer> hashMap = new HashMap<>();

    public void insert(String key) {

        if(hashMap.get(key) == null) {
            hashMap.put(key, 1);
        } else {
            hashMap.put(key, hashMap.get(key)+1);
        }
    }

    public Integer search(String key) {
        return hashMap.get(key);
    }
}
