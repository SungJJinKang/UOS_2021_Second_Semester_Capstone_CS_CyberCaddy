package com.uoscybercaddy.dabajo;

public class MemberInfo {
    private String nickName;
    private String name;
    private String phone;
    private String date;
    private String sex;
    private String tutortuty;
    private String photoUrl;

    public MemberInfo(String nickName, String name, String phone, String date, String sex, String tutortuty, String photoUrl){
        this.nickName = nickName;
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.photoUrl = photoUrl;

    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getNickName(){
        return this.nickName;
    }
    public void setNickName(String nickName){
        this.nickName = nickName;
    }
    public String getphone(){
        return this.phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getSex(){
        return this.sex;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
    public String getTutortuty(){
        return this.tutortuty;
    }
    public void setTutortuty(String tutortuty){
        this.tutortuty = tutortuty;
    }
    public String getPhotoUrl(){return this.photoUrl;}
    public void setPhotoUrl(){this.photoUrl= photoUrl;}
}
