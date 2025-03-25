package com.example.mobilePlanAPI;

import java.io.*;
import java.util.*;
import java.util.Dictionary;

class FComparator implements Comparator<SearchFrequency.Node>{

    // Overriding compare()method of Comparator
    // for descending order of cgpa
    public int compare(SearchFrequency.Node s1, SearchFrequency.Node s2) {
        if (s1.freq < s2.freq)
            return 1;
        else if (s1.freq > s2.freq)
            return -1;
        return 0;
    }
}


public class SearchFrequency {

    HashMap<String, Integer> map = new HashMap<>();
    PriorityQueue<Node> pq = new PriorityQueue<>(5, new FComparator());

    class Node {
        String word;
        int freq;

        Node(String w, int i) {
            this.word = w;
            this.freq = i;
        }
    }

    // returns the top searched words
    public List<String> mostSearchedWords() {
        List<String> result = new ArrayList<>();
        List<Node> str = new ArrayList<>();

        for(int i=0; i<3; i++) {
            Node n = pq.remove();
            result.add(n.word);
            str.add(n);
        }

        for(int i=0; i<3; i++) {
            pq.offer(str.get(i));
        }

        return result;
    }

    // creates a priority queue from a hashmap
    public void createPQ() {
        for(String key: map.keySet()) {
            pq.offer(new Node(key, map.get(key)));
        }
    }

    public void insert(String item) {
        if(map.get(item) != null) {
            map.replace(item, map.get(item));
        } else {
            map.put(item, 1);
        }
        pq.clear();
        createPQ();
    }

    //read the words and their count from the file
    public void createMap() throws IOException {

        // Creating an object of BufferedReader class
        BufferedReader bfro = new BufferedReader(
                new FileReader("/home/shubh/Documents/freq.txt"));

        // Declaring a string variable
        String st;

        while ((st = bfro.readLine()) != null) {
            String[] temp = st.split(" ");
            map.put(temp[0], Integer.valueOf(temp[1]));
        }
        createPQ();
    }

    public static void main(String[] args) throws IOException {
        SearchFrequency sf = new SearchFrequency();

        sf.createMap();
        for(String w: sf.mostSearchedWords()) {
            System.out.println(w);
        }

    }

}

