package com.uoscybercaddy.dabajo.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ModelPost implements Comparable<ModelPost> {
    String pId, pTitle, pDescr, pTime, uid, uEmail, uDp, uName, pCategory, pTutortuty;
    int arrayCount, pLikes, pComments;
    List<URLS> pImage;
    List<String> pLikers;
    HashMap<String, HashMap<String, Object>> Comments;
    HashMap<String, Integer> pCommenters;

    public int getpComments() {
        return pComments;
    }

    public void setpComments(int pComments) {
        this.pComments = pComments;
    }

    public HashMap<String, HashMap<String, Object>> getComments() {
        return Comments;
    }

    public void setComments(HashMap<String, HashMap<String, Object>> comments) {
        Comments = comments;
    }

    public HashMap<String, Integer> getpCommenters() {
        return pCommenters;
    }

    public void setpCommenters(HashMap<String, Integer> pCommenters) {
        this.pCommenters = pCommenters;
    }

    public ModelPost(String pId, String pTitle, String pDescr, String pTime, String uid, String uEmail, String uDp, String uName, String pCategory, String pTutortuty, int arrayCount, int pLikes, int pComments, List<URLS> pImage, List<String> pLikers, HashMap<String, HashMap<String, Object>> comments, HashMap<String, Integer> pCommenters) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDescr;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.pCategory = pCategory;
        this.pTutortuty = pTutortuty;
        this.arrayCount = arrayCount;
        this.pLikes = pLikes;
        this.pComments = pComments;
        this.pImage = pImage;
        this.pLikers = pLikers;
        Comments = comments;
        this.pCommenters = pCommenters;
    }

    public int getpLikes() {
        return pLikes;
    }

    public void setpLikes(int pLikes) {
        this.pLikes = pLikes;
    }

    public List<String> getpLikers() {
        return pLikers;
    }

    public void setpLikers(List<String> pLikers) {
        this.pLikers = pLikers;
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

    public ModelPost(){

        }
        public String getpId() {
                return pId;
        }

        public void setpId(String pId) {
                this.pId = pId;
        }

        public String getpTitle() {
                return pTitle;
        }

        public void setpTitle(String pTitle) {
                this.pTitle = pTitle;
        }

        public String getpDescr() {
                return pDescr;
        }

        public void setpDescr(String pDescr) {
                this.pDescr = pDescr;
        }

        public String getpTime() {
                return pTime;
        }

        public void setpTime(String pTime) {
                this.pTime = pTime;
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

        public int getArrayCount() {
                return arrayCount;
        }

        public void setArrayCount(int arrayCount) {
                this.arrayCount = arrayCount;
        }

        public List<URLS> getpImage() {
                return pImage;
        }

        public void setpImage(List<URLS> pImage) {
                this.pImage = pImage;
        }


    @Override
    public int compareTo(ModelPost o) {
        return this.pTime.compareTo(o.getpTime());
    }
}
