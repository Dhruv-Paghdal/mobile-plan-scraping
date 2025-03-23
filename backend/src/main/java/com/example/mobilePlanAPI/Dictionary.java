package com.example.mobilePlanAPI;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Dictionary<Value> {

    private static final int R = 256;

    private Node root;      // root of trie
    private int N;          // number of keys in trie

    // R-way trie node
    private static class Node {
        private Object val; //int variable initialized at the last character of the string to denote if the string is delete or not
        private Node[] child = new Node[R];
        int count;
    }

    public Dictionary() {
    }

    /*
     * Helper function to get the root node of subtrie of key
     *
     * param:
     * y - object of type Node
     * key - the string to be searched in the trie
     * d - depth of the trie, start with 0
     *
     * return - the root node of the subtrie of key
     **/
    private Node get(Node y, String keyString, int depth) {
        if (y == null) return null;
        if (depth == keyString.length()) return y;
        char character = keyString.charAt(depth);
        return get(y.child[character], keyString, depth+1);
    }

    /**
     * Function to insert the key-value pair into the trie, if already present overwrite it.
     * If the value is null, delete the key from trie.
     *
     * param:
     * key - the string that needs to be stored
     * val - the value is an int that is used to identify the element. It is stored at the last node of the word.
     *
     *
     */
    public void put(String key, Value val) {
        if (val == null) delete(key);
        else root = put(root, key, val, 0);
    }

    private Node put(Node y, String keyString, Value value, int depth) {
        if (y == null) y = new Node();
        if (depth == keyString.length()) {
            if (y.val == null) N++;
            y.val = value;
            return y;
        }
        char c = keyString.charAt(depth);
        y.child[c] = put(y.child[c], keyString, value, depth+1);
        return y;
    }

    public int size() {
        return N;
    }

    /**
     * Returns all keys in the trie
     * return - iterable object of all keys in the symbol table
     */
    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    /**
     * Function to return all the keys in the set that start with prefix
     * param
     * prefix - the prefix
     *
     * return - iterable object of keys that start with the provided prefix
     */
    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> results = new LinkedList<>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    /* Driver function of collect
     *
     * param:
     * x - object of type Node
     * prefix - object of type StringBuilder
     * results - a list of all strings that match the prefix
     *
     *
     * */
    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) return;
        if (x.val != null) results.add(prefix.toString());
        for (char c = 0; c < R; c++) {
            prefix.append(c);
            collect(x.child[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Removes the key from the set if the key is present.
     * param
     * key - the key
     */
    public void delete(String key) {
        root = delete(root, key, 0);
    }

    /* Function to delete a String from the trie
     *
     * params:
     * x - object of type Node
     * key - String that is to be deleted
     * d - depth of the tree
     *
     *
     * return - first child which point to a valid Node
     * */
    private Node delete(Node y, String keyString, int depth) {
        if (y == null) return null;
        if (depth == keyString.length()) {
            if (y.val != null) N--;
            y.val = null;
        }
        else {
            char c = keyString.charAt(depth);
            y.child[c] = delete(y.child[c], keyString, depth+1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (y.val != null) return y;
        for (int c = 0; c < R; c++)
            if (y.child[c] != null)
                return y;
        return null;
    }
}
