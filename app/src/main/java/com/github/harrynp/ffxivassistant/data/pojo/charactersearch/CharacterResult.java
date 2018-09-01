package com.github.harrynp.ffxivassistant.data.pojo.charactersearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CharacterResult {

    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("server")
    @Expose
    String server;
    @SerializedName("icon")
    @Expose
    String icon;
    @SerializedName("last_updated")
    @Expose
    String lastUpdated;
    @SerializedName("id")
    @Expose
    Integer id;
    @SerializedName("url")
    @Expose
    String url;
    @SerializedName("url_type")
    @Expose
    String urlType;
    @SerializedName("url_api")
    @Expose
    String urlApi;
    @SerializedName("url_xivdb")
    @Expose
    String urlXivdb;

    public CharacterResult(){
    }

    public CharacterResult(String name, String server, String icon, int id, String urlApi, String urlXivdb){
        this.name = name;
        this.server = server;
        this.icon = icon;
        this.id = id;
        this.url = urlApi;
        this.urlXivdb = urlXivdb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }

    public String getUrlXivdb() {
        return urlXivdb;
    }

    public void setUrlXivdb(String urlXivdb) {
        this.urlXivdb = urlXivdb;
    }

}