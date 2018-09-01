package com.github.harrynp.ffxivassistant.data.pojo.lodestone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Banner {

    @SerializedName("url")
    @Expose
    String url;
    @SerializedName("banner")
    @Expose
    String banner;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

}