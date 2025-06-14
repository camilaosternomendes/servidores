package com.busca.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException; 
import com.fasterxml.jackson.databind.ObjectMapper;      

import java.io.Serializable;

public class Artigo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String abstractText;
    private String label;

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

    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getLabel() {
        return label;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Título: " + (title != null ? title : "N/A") + "\n" +
               "Abstract: " + (abstractText != null ? abstractText : "N/A") + "\n" +
               "Label: " + (label != null ? label : "N/A") + "\n" +
               "--------------------------------------------------";
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Artigo fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Artigo.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
