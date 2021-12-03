package com.uoscybercaddy.dabajo.models;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

public class ModelComment {
    String cId, comment, timeStamp, uid, uEmail, uDp, uName;

    public ModelComment() {
    }

    public ModelComment(String cId, String comment, String timeStamp, String uid, String uEmail, String uDp, String uName) {
        this.cId = cId;
        this.comment = comment;
        this.timeStamp = timeStamp;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }
    public ModelComment(HashMap<String, Object> comments){
        this.cId = comments.get("cId").toString();
        this.comment = comments.get("comment").toString();
        this.timeStamp = comments.get("timeStamp").toString();
        this.uid = comments.get("uid").toString();
        this.uEmail = comments.get("uEmail").toString();
        this.uDp = comments.get("uDp").toString();
        this.uName = comments.get("uName").toString();
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
