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

    /**
     * Busca todas as ocorrências de um padrão (substring) em um texto usando o algoritmo KMP.
     * A busca não diferencia maiúsculas de minúsculas.
     */
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

}
