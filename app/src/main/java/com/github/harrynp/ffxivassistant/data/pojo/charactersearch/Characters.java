package com.github.harrynp.ffxivassistant.data.pojo.charactersearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Characters {

    @SerializedName("results")
    @Expose
    List<CharacterResult> results = null;
    @SerializedName("total")
    @Expose
    Integer total;
    @SerializedName("paging")
    @Expose
    Paging paging;

    public List<CharacterResult> getResults() {
        return results;
    }

    public void setResults(List<CharacterResult> results) {
        this.results = results;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

}