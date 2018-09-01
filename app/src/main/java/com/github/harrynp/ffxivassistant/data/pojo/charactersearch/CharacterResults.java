package com.github.harrynp.ffxivassistant.data.pojo.charactersearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CharacterResults {

    @SerializedName("characters")
    @Expose
    Characters characters;

    public Characters getCharacters() {
        return characters;
    }

    public void setCharacters(Characters characters) {
        this.characters = characters;
    }

}