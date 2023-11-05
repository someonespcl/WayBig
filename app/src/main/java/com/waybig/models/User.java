package com.waybig.models;

public class User {
    private String name, email, phoneNumber, image, userId;

    public User() {
        // Default constructor required for Firebase database mapping
    }

    public User(String name, String email, String phoneNumber, String image, String userId) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.userId = userId;
    }

    // Getters and setters for the fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getImage() {
    	return image;
    }
    
    public void setImage(String image) {
    	this.image = image;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
