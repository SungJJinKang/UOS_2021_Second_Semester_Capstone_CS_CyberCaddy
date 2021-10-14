package com.uoscybercaddy.dabajo.view;


public class WriteInfo {
    private String title;
    private String body;
    private String writer;

    public WriteInfo(String title, String contents, String writer){
        this.title = title;
        this.body = contents;
        this.writer = writer;
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
}