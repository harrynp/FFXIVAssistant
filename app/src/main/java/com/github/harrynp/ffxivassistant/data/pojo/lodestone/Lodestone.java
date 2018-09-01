package com.github.harrynp.ffxivassistant.data.pojo.lodestone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Lodestone {

    @SerializedName("banners")
    @Expose
    List<Banner> banners = null;
    @SerializedName("topics")
    @Expose
    List<Topic> topics = null;
    @SerializedName("notices")
    @Expose
    List<Notice> notices = null;
    @SerializedName("maintenance")
    @Expose
    List<Maintenance> maintenance = null;
    @SerializedName("status")
    @Expose
    List<Status> status = null;
//    @SerializedName("community")
//    @Expose(serialize = false, deserialize = false)
//    List<Object> community = null;
//    @SerializedName("events")
//    @Expose(serialize = false, deserialize = false)
//    List<Object> events = null;
//    @SerializedName("popularposts")
//    @Expose(serialize = false, deserialize = false)
//    List<Object> popularposts = null;

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public List<Maintenance> getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(List<Maintenance> maintenance) {
        this.maintenance = maintenance;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }
//
//    public List<Object> getCommunity() {
//        return community;
//    }
//
//    public void setCommunity(List<Object> community) {
//        this.community = community;
//    }
//
//    public List<Object> getEvents() {
//        return events;
//    }
//
//    public void setEvents(List<Object> events) {
//        this.events = events;
//    }
//
//    public List<Object> getPopularposts() {
//        return popularposts;
//    }
//
//    public void setPopularposts(List<Object> popularposts) {
//        this.popularposts = popularposts;
//    }

}