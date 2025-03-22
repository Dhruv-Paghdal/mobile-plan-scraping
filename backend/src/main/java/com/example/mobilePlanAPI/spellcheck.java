package com.example.mobilePlanAPI;

import java.io.*;
import java.util.*;

// Cuckoo Hash Table for efficient word storage and lookup
class CHT {
    private static final int S = 101; // Table size chosen as a prime number
    private String[] t1; // Primary hash table
    private String[] t2; // Secondary hash table
    private int M_L = 50; // Maximum loops for insertion attempts

    // Constructor initializes hash tables
    public CHT() {
        t1 = new String[S];
        t2 = new String[S];
    }

    // First hash function
    private int h1(String k1) {
        return Math.abs(k1.hashCode()) % S; // Modulo operation for indexing
    }

    // Second hash function
    private int h2(String k2) {
        return Math.abs((k2.hashCode() / S)) % S; // Alternative index calculation
    }

    // Insert word into the hash table using Cuckoo Hashing
    public void ist(String k1) {
        String t = k1;
        int p1, p2;

        // Attempt to place the word within the max loop limit
        for (int i1 = 0; i1 < M_L; i1++) {
            p1 = h1(t);
            if (t1[p1] == null) { // If empty slot found, insert it
                t1[p1] = t;
                return;
            }

            // If slot is occupied, swap with existing word
            String dp = t1[p1];
            t1[p1] = t;
            t = dp;

            // Try inserting into second table
            p2 = h2(t);
            if (t2[p2] == null) {
                t2[p2] = t;
                return;
            }

            // Repeat process if collision occurs
            dp = t2[p2];
            t2[p2] = t;
            t = dp;
        }
    }

    // Check if a word exists in either hash table
    public boolean cts(String k1) {
        int p1 = h1(k1);
        int p2 = h2(k1);
        return (t1[p1] != null && t1[p1].equals(k1)) ||
               (t2[p2] != null && t2[p2].equals(k1));
    }
}

// Edit Distance Calculation
class EDist {
    public static int gDist(String w1, String w2) {
        int m1 = w1.length(), n1 = w2.length();
        int[][] dp = new int[m1 + 1][n1 + 1];

        // Initialize base cases (empty string edits)
        for (int i1 = 0; i1 <= m1; i1++) dp[i1][0] = i1;
        for (int j1 = 0; j1 <= n1; j1++) dp[0][j1] = j1;

        // Compute edit distance using dynamic programming
        for (int i2 = 1; i2 <= m1; i2++) {
            for (int j2 = 1; j2 <= n1; j2++) {
                if (w1.charAt(i2 - 1) == w2.charAt(j2 - 1))
                    dp[i2][j2] = dp[i2 - 1][j2 - 1]; // No change needed
                else
                    dp[i2][j2] = 1 + Math.min(dp[i2 - 1][j2 - 1], // Replace
                                     Math.min(dp[i2 - 1][j2], dp[i2][j2 - 1])); // Insert/Delete
            }
        }
        return dp[m1][n1]; // Final edit distance
    }
}

// Merge Sort to arrange suggestions by similarity
class MSt {
    public static void sort(List<String> ws, String t1) {
        if (ws.size() < 2) return; // Base case: List of size 1 is already sorted
        int m1 = ws.size() / 2;

        // Split list into two halves
        List<String> l = new ArrayList<>(ws.subList(0, m1));
        List<String> r = new ArrayList<>(ws.subList(m1, ws.size()));

        sort(l, t1); // Sort left half
        sort(r, t1); // Sort right half
        mrg(ws, l, r, t1); // Merge sorted halves
    }

    // Merge function based on edit distance
    private static void mrg(List<String> words, List<String> l, List<String> r, String t) {
        int i1 = 0, j1 = 0, k1 = 0;
        while (i1 < l.size() && j1 < r.size()) {
            if (EDist.gDist(l.get(i1), t) <= EDist.gDist(r.get(j1), t))
                words.set(k1++, l.get(i1++));
            else
                words.set(k1++, r.get(j1++));
        }
        while (i1 < l.size()) words.set(k1++, l.get(i1++)); // Copy remaining left elements
        while (j1 < r.size()) words.set(k1++, r.get(j1++)); // Copy remaining right elements
    }
}

// Main Spell Checker Class
public class spellcheck {
    private static CHT v = new CHT(); // Vocabulary storage
    private static List<String> wLt = new ArrayList<>(); // Word list

    // Load vocabulary from CSV file
    public static void buildVocabulary(String fP) throws IOException {
        File f1 = new File(fP);
        
        if (!f1.exists()) { // Validate file existence
            System.out.println("Error: File not found at " + fP);
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(f1));
        String line;

        // Read file line by line
        while ((line = br.readLine()) != null) {
            String[] ws = line.split(","); // Split words assuming comma-separated format
            for (String w1 : ws) {
                w1 = w1.trim().toLowerCase(); // Normalize word (trim spaces, lowercase)
                if (!w1.isEmpty()) {
                    String[] individualWord = w1.split("[-+\\s]+");
                    if(individualWord.length > 0){
                        for (String word:individualWord) {
                            v.ist(word); // Store word in hash table
                            wLt.add(word); // Add to list for suggestions
                        }
                    }
                    else {
                        v.ist(w1); // Store word in hash table
                        wLt.add(w1); // Add to list for suggestions
                    }
                }
            }
        }
        br.close(); // Close file after reading
    }

    // Generate word suggestions using edit distance
    public static List<String> getSuggestions(String input) {
        Set<String> uS = new HashSet<>(); // Use a set to eliminate duplicates

        // Compare input word with vocabulary
        for (String w1 : wLt) {
            int dst = EDist.gDist(input, w1);
            if (dst > 0) { // Avoid identical words and limit suggestions
                uS.add(w1);
            }
        }

        List<String> s1 = new ArrayList<>(uS);
        MSt.sort(s1, input); // Arrange suggestions by similarity

        return s1;
    }

    public static void main(String[] a1) throws IOException {
        // Define correct file path
        String fileP = "b.csv";
        buildVocabulary(fileP);

        Scanner sc1 = new Scanner(System.in);
        System.out.println("Enter a word to spell check:");
        String input = sc1.nextLine().toLowerCase();

        // Check if word is correct
        if (v.cts(input)) {
            System.out.println("The word is correct!");
        } else {
            System.out.println("Word not found. Suggestions:");
            List<String> s1 = getSuggestions(input);
            for (String suggestion : s1) {
                System.out.println(suggestion);
            }
        }
        sc1.close(); // Close scanner to prevent memory leaks
    }
}
