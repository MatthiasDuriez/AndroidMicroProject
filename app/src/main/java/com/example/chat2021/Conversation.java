package com.example.chat2021;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

public class Conversation {
    String id;
    @SerializedName("active")
    String isActive;
    String theme;

    public String getIsActive() {
        return  this.isActive;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id='" + id + '\'' +
                ", active='" + isActive + '\'' +
                ", theme='" + theme + '\'' +
                '}';
    }
    // {"version":1.3,"success":true,"status":200,
    // "conversations":[{"id":"4","active":"1","theme":"test1"},{"id":"8","active":"1","theme":"WEB
    //3
}
