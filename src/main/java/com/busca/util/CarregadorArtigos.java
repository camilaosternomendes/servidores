
package com.busca.util;

import com.busca.model.Artigo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference; 

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class CarregadorArtigos {

    /**
     * Carrega uma lista de objetos Artigo a partir de um arquivo JSON.
     *
      @param filePath 
     * @return 
     */
    public static List<Artigo> carregar(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        List<Artigo> artigos = new ArrayList<>();
        try {

            artigos = mapper.readValue(new File(filePath), new TypeReference<List<Artigo>>() {});
            System.out.println("DEBUG: Carregados " + artigos.size() + " artigos do arquivo: " + filePath);
        } catch (IOException e) {
            System.err.println("Erro ao carregar artigos do arquivo " + filePath + ": " + e.getMessage());

        }
        return artigos;
    }
}
