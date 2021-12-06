package com.uoscybercaddy.dabajo.models;

import java.util.HashMap;
import java.util.List;

public class ModelUsers {
    String name, nickName, photoUrl, sex, tutortuty, introduction, search, uid, onlineStatus, typingTo;
    boolean isBlocked = false;
    HashMap<String, Long> categoriesCount;
    HashMap<String, HashMap<String, HashMap<String,String>>> comments;

    public ModelUsers(String name, String nickName, String photoUrl, String sex, String tutortuty, String introduction, String search, String uid, String onlineStatus, String typingTo, boolean isBlocked, HashMap<String, Long> categoriesCount, HashMap<String, HashMap<String, HashMap<String, String>>> comments) {
        this.name = name;
        this.nickName = nickName;
        this.photoUrl = photoUrl;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.introduction = introduction;
        this.search = search;
        this.uid = uid;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.isBlocked = isBlocked;
        this.categoriesCount = categoriesCount;
        this.comments = comments;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public HashMap<String, Long> getCategoriesCount() {
        return categoriesCount;
    }

    public void setCategoriesCount(HashMap<String, Long> categoriesCount) {
        this.categoriesCount = categoriesCount;
    }

    public HashMap<String, HashMap<String, HashMap<String, String>>> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, HashMap<String, HashMap<String, String>>> comments) {
        this.comments = comments;
    }


    public ModelUsers(String name, String nickName, String photoUrl, String sex, String tutortuty, String introduction, String uid) {
        this.name = name;
        this.nickName = nickName;
        this.photoUrl = photoUrl;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.introduction = introduction;
        this.uid = uid;
    }




    public ModelUsers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTutortuty() {
        return tutortuty;
    }

    public void setTutortuty(String tutortuty) {
        this.tutortuty = tutortuty;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }
}
