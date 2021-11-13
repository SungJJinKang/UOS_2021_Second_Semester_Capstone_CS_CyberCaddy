package com.uoscybercaddy.dabajo.models;

import android.graphics.drawable.Drawable;

public class MemberInfoTutor {
    private String nickName;
    private String name;
    private String field;
    private String introduction;
    private String sex;



    private String photoUrl;


    private Drawable profilePhotoDrawable;
    private boolean profilePhotoDrawableIsLoaded = false;
    public MemberInfoTutor(String nickName, String name, String field, String introduction, String sex, String photoUrl){
        this.nickName = nickName;
        this.name = name;
        this.introduction = introduction;
        this.field = field;
        this.sex = sex;
        this.photoUrl = photoUrl;
    }

    public MemberInfoTutor(String photoUrl){
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
    public String getField(){
        return this.field;
    }
    public void setField(String field){
        this.field = field;
    }
    public String getIntroduction(){
        return this.introduction;
    }
    public void setIntroduction(String introduction){
        this.introduction = introduction;
    }
    public String getSex(){
        return this.sex;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
    public String getPhotoUrl(){return this.photoUrl;}
    public void setPhotoUrl(){this.photoUrl= photoUrl;}

    public Drawable GetProfilePhotoDrawable()
    {
        if(profilePhotoDrawableIsLoaded == false)
        {
            //Load Image
            profilePhotoDrawableIsLoaded = true;
        }
        return profilePhotoDrawable;
    }

}


