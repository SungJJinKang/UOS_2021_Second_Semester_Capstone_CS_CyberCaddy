package com.uoscybercaddy.dabajo.models;

import java.io.Serializable;

public class URLS implements Serializable {
    public String imagevideo;
    public String urls;

    public String getImagevideo() {
        return imagevideo;
    }

    public void setImagevideo(String imagevideo) {
        this.imagevideo = imagevideo;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }
}
