package com.github.harrynp.ffxivassistant.data.pojo.devtracker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class User {

    @SerializedName("username")
    @Expose
    String username;
    @SerializedName("color")
    @Expose
    String color;
    @SerializedName("title")
    @Expose
    String title;
    @SerializedName("avatar")
    @Expose
    String avatar;
    @SerializedName("signature")
    @Expose
    String signature;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}