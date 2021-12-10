package com.uoscybercaddy.dabajo.models;

import java.security.MessageDigest;

public class ModelEvalList {
    String body;
    // name은 nickname과 같음
    String name;
    Double rating;

    public ModelEvalList(Double rating, String name, String body)
    {
        this.rating = rating;
        this.name = name;
        this.body = body;
    }

    public Double getRating() {
        return rating;
    }
    public void setRating() {
        this.rating = rating;
    }
    public String getName() {
        return name;
    }
    public void setName() {
        this.name = name;
    }
    public String getBody() {
        return body;
    }
    public void setBody() {
        this.body = body;
    }

}
