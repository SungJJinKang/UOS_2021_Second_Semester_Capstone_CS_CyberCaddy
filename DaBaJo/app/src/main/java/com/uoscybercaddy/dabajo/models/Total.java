package com.uoscybercaddy.dabajo.models;

public class Total {
    private int raterCnt;
    private float totalRating;

    public Total(int raterCnt, float totalRating) {
        this.raterCnt = raterCnt;
        this.totalRating = totalRating;
    }

    public float getRaterCnt(){
        return this.raterCnt;
    }
    public void setRaterCnt(int rating){
        this.raterCnt = raterCnt;
    }
    public float getTotalRating(){
        return this.totalRating;
    }
    public void setTotalRating(float totalRating){
        this.totalRating = totalRating;
    }
}
