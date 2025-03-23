package com.example.mobilePlanAPI;

import java.util.*;

public class DataPlanAVLTree {
    private static class TreeNode {
        String d_data;
        int d_cnt;
        int d_nodeHeight;
        TreeNode d_leftNode, d_rightNode;

        TreeNode(String keyword) {
            this.d_data = keyword;
            this.d_cnt = 1;
            this.d_nodeHeight = 1;
        }
    }

    private TreeNode root;

    private int computeHeight(TreeNode node) {
        if(node == null){
            return 0;
        }
        else{
            return node.d_nodeHeight;
        }
    }

    private int calculateBalance(TreeNode node) {
        if(node == null){
            return 0;
        }
        else{
            return computeHeight(node.d_leftNode) - computeHeight(node.d_rightNode);
        }
    }

    private TreeNode rotated_rightNode(TreeNode pivot) {
        TreeNode newRoot = pivot.d_leftNode;
        pivot.d_leftNode = newRoot.d_rightNode;
        newRoot.d_rightNode = pivot;

        pivot.d_nodeHeight = Math.max(computeHeight(pivot.d_leftNode), computeHeight(pivot.d_rightNode)) + 1;
        newRoot.d_nodeHeight = Math.max(computeHeight(newRoot.d_leftNode), computeHeight(newRoot.d_rightNode)) + 1;
        return newRoot;
    }

    private TreeNode rotated_leftNode(TreeNode pivot) {
        TreeNode newRoot = pivot.d_rightNode;
        pivot.d_rightNode = newRoot.d_leftNode;
        newRoot.d_leftNode = pivot;

        pivot.d_nodeHeight = Math.max(computeHeight(pivot.d_leftNode), computeHeight(pivot.d_rightNode)) + 1;
        newRoot.d_nodeHeight = Math.max(computeHeight(newRoot.d_leftNode), computeHeight(newRoot.d_rightNode)) + 1;
        return newRoot;
    }

    public void insertKeyword(String keyword) {
        root = insert(root, keyword);
    }

    private TreeNode insert(TreeNode node, String keyword) {
        if (node == null) return new TreeNode(keyword);

        if (keyword.equals(node.d_data)) {
            node.d_cnt++;
        } else if (keyword.compareTo(node.d_data) < 0) {
            node.d_leftNode = insert(node.d_leftNode, keyword);
        } else {
            node.d_rightNode = insert(node.d_rightNode, keyword);
        }

        node.d_nodeHeight = Math.max(computeHeight(node.d_leftNode), computeHeight(node.d_rightNode)) + 1;
        return balanceTree(node, keyword);
    }

    private TreeNode balanceTree(TreeNode node, String keyword) {
        int balanceFactor = calculateBalance(node);

        if (balanceFactor > 1 && keyword.compareTo(node.d_leftNode.d_data) < 0) {
            return rotated_rightNode(node);
        }
        if (balanceFactor < -1 && keyword.compareTo(node.d_rightNode.d_data) > 0) {
            return rotated_leftNode(node);
        }
        if (balanceFactor > 1 && keyword.compareTo(node.d_leftNode.d_data) > 0) {
            node.d_leftNode = rotated_leftNode(node.d_leftNode);
            return rotated_rightNode(node);
        }
        if (balanceFactor < -1 && keyword.compareTo(node.d_rightNode.d_data) < 0) {
            node.d_rightNode = rotated_rightNode(node.d_rightNode);
            return rotated_leftNode(node);
        }
        return node;
    }

    public List<Map.Entry<String, Integer>> getSortedKeywords() {
        List<Map.Entry<String, Integer>> keywordList = new ArrayList<>();
        collectKeywords(root, keywordList);
        keywordList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        return keywordList;
    }

    private void collectKeywords(TreeNode node, List<Map.Entry<String, Integer>> list) {
        if (node != null) {
            collectKeywords(node.d_leftNode, list);
            list.add(new AbstractMap.SimpleEntry<>(node.d_data, node.d_cnt));
            collectKeywords(node.d_rightNode, list);
        }
    }

    public int getKeywordCount(String keyword) {
        return searchKeyword(root, keyword);
    }

    private int searchKeyword(TreeNode node, String keyword) {
        if (node == null) {
            return 0;
        }
        if (keyword.equals(node.d_data)) {
            return node.d_cnt;
        }
        if(keyword.compareTo(node.d_data) < 0){
            return searchKeyword(node.d_leftNode, keyword);
        }
        else {
           return searchKeyword(node.d_rightNode, keyword);
        }
    }
}
