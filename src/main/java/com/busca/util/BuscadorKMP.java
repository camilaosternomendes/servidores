package com.busca.util;

import java.util.ArrayList;
import java.util.List;     

public class BuscadorKMP {

    /**
     * Constrói o array de prefixos (LPS - Longest Proper Prefix suffix)
     * para o algoritmo KMP. Este array ajuda a pular caracteres
     * durante a busca quando ocorre uma incompatibilidade.
     *
     * @param pattern A substring (padrão) a ser buscada.
     * @return Um array de inteiros representando o LPS.
     */
    private static int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0; // length of the previous longest prefix suffix
        int i = 1;

        lps[0] = 0; // lps[0] is always 0

        // The loop calculates lps[i] for i = 1 to M-1
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

}
