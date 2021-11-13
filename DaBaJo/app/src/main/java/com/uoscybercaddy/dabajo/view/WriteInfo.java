package com.uoscybercaddy.dabajo.view;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteInfo implements Serializable {
    private String title;
    private String body;
    private String writer;
    public Date createdAt;

    public int imageCount;

    public int videoCount;
    //TODO : 비디오 사이즈 수정 필요.
    //public int[] videoSize;
    public List<String> videoExtensions;
    public transient  boolean isVideoDataLoaded = false;
    public ArrayList<String> commentList;
    public WriteInfo() {

    }
    public WriteInfo(String title, String contents, String writer, Date createdAt){
        this.title = title;
        this.body = contents;
        this.writer = writer;
        this.createdAt = createdAt;
        this.commentList = new ArrayList<>();
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