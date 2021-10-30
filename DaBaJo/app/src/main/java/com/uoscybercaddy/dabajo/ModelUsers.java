package com.uoscybercaddy.dabajo;

public class ModelUsers {
    String name, nickName, photoUrl, sex, tutortuty, introduction, search;

    public ModelUsers(String name, String nickName, String photoUrl, String sex, String tutortuty, String introduction, String search) {
        this.name = name;
        this.nickName = nickName;
        this.photoUrl = photoUrl;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.introduction = introduction;
        this.search = search;
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
}
