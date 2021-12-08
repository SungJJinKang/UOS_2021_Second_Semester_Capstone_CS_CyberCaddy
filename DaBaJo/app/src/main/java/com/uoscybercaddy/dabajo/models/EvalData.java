package com.uoscybercaddy.dabajo.models;

public class EvalData {
    private String body;
    private float rating;
    private String tutee_id;
    // nickname 부분 추가
    private String nickname;

    public EvalData(String body, float rating,String tutee_id, String nickname) {
        this.body = body;
        this.rating = rating;
        this.tutee_id = tutee_id;
        // nickname 부분 추가
        this.nickname = nickname;
    }

    public String getBody(){
        return this.body;
    }
    public void setBody(String body){
        this.body = body;
    }
    public float getRating(){
        return this.rating;
    }
    public void setRating(float rating){
        this.rating = rating;
    }
    public String getTutee_id() {
        return this.tutee_id;
    }
    public void setTutee_id() {
        this.tutee_id = tutee_id;
    }
    // nickname 부분 추가
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname() {
        this.nickname = nickname;
    }

}
