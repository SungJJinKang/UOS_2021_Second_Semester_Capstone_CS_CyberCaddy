package com.uoscybercaddy.dabajo.models;

import java.util.HashMap;

public class ModelNotification {
    String pId, pCategory, pTutortuty, timeStamp, pUid, notification, sUid, sName, sEmail, sImage;

    public ModelNotification(String pId, String pCategory, String pTutortuty, String timeStamp, String pUid, String notification, String sUid, String sName, String sEmail, String sImage) {
        this.pId = pId;
        this.pCategory = pCategory;
        this.pTutortuty = pTutortuty;
        this.timeStamp = timeStamp;
        this.pUid = pUid;
        this.notification = notification;
        this.sUid = sUid;
        this.sName = sName;
        this.sEmail = sEmail;
        this.sImage = sImage;
    }

    public ModelNotification() {
    }

    public ModelNotification(HashMap<String, String> value){
        this.pId = value.get("pId");
        this.pCategory = value.get("pCategory");
        this.pTutortuty = value.get("pTutortuty");
        this.timeStamp = value.get("timeStamp");
        this.pUid = value.get("pUid");
        this.notification = value.get("notification");
        this.sUid = value.get("sUid");
        this.sName = value.get("sName");
        this.sEmail = value.get("sEmail");
        this.sImage = value.get("sImage");
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpCategory() {
        return pCategory;
    }

    public void setpCategory(String pCategory) {
        this.pCategory = pCategory;
    }

    public String getpTutortuty() {
        return pTutortuty;
    }

    public void setpTutortuty(String pTutortuty) {
        this.pTutortuty = pTutortuty;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid(String sUid) {
        this.sUid = sUid;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }
}
