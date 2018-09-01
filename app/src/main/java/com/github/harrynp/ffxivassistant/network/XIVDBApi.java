package com.github.harrynp.ffxivassistant.network;

import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResults;
import com.github.harrynp.ffxivassistant.data.pojo.devtracker.Post;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Lodestone;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface XIVDBApi {
    @GET("assets/lodestone.json")
    Call<Lodestone> getLodestone();

    @GET("assets/devtracker.json")
    Call<ArrayList<Post>> getDevTracker();

    @GET("search")
    Call<CharacterResults> getCharacterResults(@Query("one") String content, @Query("string") String name, @Query("server|et") String server);

    @GET("character/{id}")
    Call<ArrayList> getCharacter(@Path("id") Integer id);
}
