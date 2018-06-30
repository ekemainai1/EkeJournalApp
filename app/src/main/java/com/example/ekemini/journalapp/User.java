package com.example.ekemini.journalapp;

public class User {

    // columns
    String userId;
    String id;
    String userName;
    String userEmail;
    String userPost;

    public User(){
    }

    public User(String userId, String id, String userName,
                String userEmail, String userPost) {
        this.userId = userId;
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPost = userPost;
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPost() {
        return userPost;
    }
}
