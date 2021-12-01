package com.uoscybercaddy.dabajo.models;

import java.io.Serializable;

public class URLS implements Serializable,Comparable<URLS> {
    String imagevideo;
    String urls;
    int order;

    public URLS() {
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

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

    @Override
    public int compareTo(URLS o) {
        if (this.order < o.getOrder()) {
            return -1;
        } else if (this.order > o.getOrder()) {
            return 1;
        }
        return 0;
    }
}
