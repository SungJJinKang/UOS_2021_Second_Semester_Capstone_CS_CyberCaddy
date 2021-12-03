package com.uoscybercaddy.dabajo.models;

public class UsersCategoriesCount {
    String category;
    int count;
    public UsersCategoriesCount() {
    }
    public UsersCategoriesCount(String category, int count) {
        this.category = category;
        this.count = count;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
