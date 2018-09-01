package com.github.harrynp.ffxivassistant.data.pojo.charactersearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Paging {

    @SerializedName("page")
    @Expose
    Integer page;
    @SerializedName("total")
    @Expose
    Integer total;
    @SerializedName("pages")
    @Expose
    List<Integer> pages = null;
    @SerializedName("next")
    @Expose
    Integer next;
    @SerializedName("prev")
    @Expose
    Integer prev;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public Integer getPrev() {
        return prev;
    }

    public void setPrev(Integer prev) {
        this.prev = prev;
    }

}