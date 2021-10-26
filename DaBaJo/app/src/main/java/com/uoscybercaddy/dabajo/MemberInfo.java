package com.uoscybercaddy.dabajo;

import android.graphics.drawable.Drawable;

import java.util.Optional;

public class MemberInfo {
    private String nickName;
    private String name;
    private String introduction;
    private String sex;
    private String tutortuty;
    private String photoUrl;

    private Drawable profilePhotoDrawable;
    private boolean profilePhotoDrawableIsLoaded = false;

    public MemberInfo(String nickName, String name, String introduction, String sex, String tutortuty, String photoUrl){
        this.nickName = nickName;
        this.name = name;
        this.introduction = introduction;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.photoUrl = photoUrl;
    }

    public MemberInfo(String nickName, String name, String introduction, String sex, String tutortuty){
        this.nickName = nickName;
        this.name = name;
        this.introduction = introduction;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.photoUrl = photoUrl;
    }
    public MemberInfo(String photoUrl){
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
    public String getIntroduction(){
        return this.introduction;
    }
    public void setIntroduction(String phone){
        this.introduction = introduction;
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
