package com.uoscybercaddy.dabajo.view;


import java.util.Date;

public class WriteInfo {
    private String title;
    private String body;
    private String writer;
    public Date createdAt;

    public int imageCount = 0;

    public WriteInfo(String title, String contents, String writer, Date createdAt){
        this.title = title;
        this.body = contents;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getBody(){
        return this.body;
    }
    public void setBody(String body){
        this.body = body;
    }
    public String getWriter(){
        return this.writer;
    }
    public void setWriter(String writer){
        this.writer = writer;
    }
    public Date getCreatedAt(){
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt){
        this.createdAt = createdAt;
    }
}