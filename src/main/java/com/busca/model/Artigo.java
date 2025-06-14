package com.busca.model;

import com.fasterxml.jackson.annotation.JsonCreator; // Adicionado
import com.fasterxml.jackson.annotation.JsonProperty;   // Adicionado
import java.io.Serializable;

public class Artigo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String abstractText;
    private String label;

    // Construtores
    public Artigo() {
    }

    @JsonCreator
    public Artigo(@JsonProperty("title") String title,
                  @JsonProperty("abstract") String abstractText,
                  @JsonProperty("label") String label) {
        this.title = title;
        this.abstractText = abstractText;
        this.label = label;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getLabel() {
        return label;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
