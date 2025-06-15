package com.busca.util;

import java.util.ArrayList;
import java.util.List;

public class BuscadorKMP {

    private static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0;
        int i = 1;

        lps[0] = 0;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static List<Integer> search(String text, String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (text == null || pattern == null || text.isEmpty() || pattern.isEmpty()) {
            return occurrences;
        }

        String lowerCaseText = text.toLowerCase();
        String lowerCasePattern = pattern.toLowerCase();

        int n = lowerCaseText.length();
        int m = lowerCasePattern.length();

        if (m == 0) return occurrences;
        if (m > n) return occurrences;

        int[] lps = computeLPSArray(lowerCasePattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (lowerCasePattern.charAt(j) == lowerCaseText.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                occurrences.add(i - j);
                j = lps[j - 1];
            } else if (i < n && lowerCasePattern.charAt(j) != lowerCaseText.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return occurrences;
    }

    public static void main(String[] args) {
        String text1 = "ABABDABACDABABCABAB";
        String pattern1 = "ABABCABAB";
        List<Integer> found1 = BuscadorKMP.search(text1, pattern1);
        System.out.println("Text: " + text1 + ", Pattern: " + pattern1);
        System.out.println("Occurrences at indices: " + found1);

        String text2 = "AAAAAA";
        String pattern2 = "AA";
        List<Integer> found2 = BuscadorKMP.search(text2, pattern2);
        System.out.println("\nText: " + text2 + ", Pattern: " + pattern2);
        System.out.println("Occurrences at indices: " + found2);

        String text3 = "O rato roeu a roupa do rei de Roma.";
        String pattern3 = "Rato";
        List<Integer> found3 = BuscadorKMP.search(text3, pattern3);
        System.out.println("\nText: " + text3 + ", Pattern: " + pattern3);
        System.out.println("Occurrences at indices: " + found3);

        String text4 = "Este e um exemplo de texto para teste.";
        String pattern4 = "não existe";
        List<Integer> found4 = BuscadorKMP.search(text4, pattern4);
        System.out.println("\nText: " + text4 + ", Pattern: " + pattern4);
        System.out.println("Occurrences at indices: " + found4);
    }
}
