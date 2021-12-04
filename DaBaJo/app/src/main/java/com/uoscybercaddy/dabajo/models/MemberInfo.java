package com.uoscybercaddy.dabajo.models;

import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Optional;

public class MemberInfo {
    private String nickName;
    private String name;
    private String introduction;
    private String sex;
    private String tutortuty;
    private HashMap<String, Long> categoriesCount;
    private HashMap<String, Long> commentsCount;

    public String getTutortuty() {
        return tutortuty;
    }

    public void setTutortuty(String tutortuty) {
        this.tutortuty = tutortuty;
    }

    public HashMap<String, Long> getCategoriesCount() {
        return categoriesCount;
    }

    public void setCategoriesCount(HashMap<String, Long> categoriesCount) {
        this.categoriesCount = categoriesCount;
    }

    public HashMap<String, Long> getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(HashMap<String, Long> commentsCount) {
        this.commentsCount = commentsCount;
    }

    public MemberInfo(String nickName, String name, String introduction, String sex, String tutortuty, HashMap<String, Long> categoriesCount, String photoUrl, Drawable profilePhotoDrawable, boolean profilePhotoDrawableIsLoaded) {
        this.nickName = nickName;
        this.name = name;
        this.introduction = introduction;
        this.sex = sex;
        this.tutortuty = tutortuty;
        this.categoriesCount = categoriesCount;
        this.commentsCount = new HashMap<String, Long>();
        this.photoUrl = photoUrl;
        this.profilePhotoDrawable = profilePhotoDrawable;
        this.profilePhotoDrawableIsLoaded = profilePhotoDrawableIsLoaded;
    }

    private String photoUrl;


    private Drawable profilePhotoDrawable;
    private boolean profilePhotoDrawableIsLoaded = false;
    public MemberInfo(String nickName, String name, String introduction, String sex, String photoUrl){
        this.nickName = nickName;
        this.name = name;
        this.introduction = introduction;
        this.sex = sex;
        this.photoUrl = photoUrl;
    }

    public MemberInfo() {
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
