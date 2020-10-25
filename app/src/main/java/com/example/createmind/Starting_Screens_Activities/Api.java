package com.example.createmind.Starting_Screens_Activities;

import android.app.Application;

public class Api extends Application {

    private String userName,userId;

    private static Api instance;
    public static Api getInstance(){
        if (instance == null)
            instance = new Api();
            return instance;
        }

    public Api() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
