package com.uoscybercaddy.dabajo.models;

public class EvalData {
    private String body;
    private float rating;
    private String tutee_id;
//    private String tutor_id;

    public EvalData(String body, float rating,String tutee_id/*, String tutor_id*/) {
        this.body = body;
        this.rating = rating;
        this.tutee_id = tutee_id;
//        this.tutor_id = tutor_id;
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
//    public String getTutor_id() {
//        return this.tutor_id;
//    }
//    public void setTutor_id() {
//        this.tutee_id = tutor_id;
//    }

}
