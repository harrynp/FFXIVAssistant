package com.github.harrynp.ffxivassistant.data.pojo.devtracker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Post {

    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("thread")
    @Expose
    Thread thread;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

}