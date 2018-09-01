package com.github.harrynp.ffxivassistant.network;

import android.util.MalformedJsonException;

import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResults;
import com.github.harrynp.ffxivassistant.data.pojo.devtracker.Post;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Lodestone;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class XIVDBApiClient {
    private static final String BASE_URL = "https://api.xivdb.com/";
    private XIVDBApi xivdbApi;

    public XIVDBApiClient() {
        //Capturing JSON using https://stackoverflow.com/questions/32514410/logging-with-retrofit-2
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();

        xivdbApi = retrofit.create(XIVDBApi.class);
    }

    public void getLodestone(final LodestoneListener listener) {
        Call<Lodestone> call = xivdbApi.getLodestone();
        call.enqueue(new Callback<Lodestone>() {
            @Override
            public void onResponse(Call<Lodestone> call, Response<Lodestone> response) {
                if (response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(new MalformedJsonException(response.body().toString()));
                    Timber.d("JSON not formatted correctly: %s", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Lodestone> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure(t);
            }
        });
    }

    public void getDevTracker(final DevTrackerListener<Post> listener){
        Call<ArrayList<Post>> call = xivdbApi.getDevTracker();
        call.enqueue(new Callback<ArrayList<Post>>(){

            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                if (response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(new MalformedJsonException(response.body().toString()));
                    Timber.d("JSON not formatted correctly: %s", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure(t);
            }
        });
    }

    public void getCharacterResults(final CharactersResultsListener listener, String name,  String server) {
        Call<CharacterResults> call = xivdbApi.getCharacterResults("characters", name, server);
        call.enqueue(new Callback<CharacterResults>() {
            @Override
            public void onResponse(Call<CharacterResults> call, Response<CharacterResults> response) {
                if (response.body() != null){
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(new MalformedJsonException(response.body().toString()));
                    Timber.d("JSON not formatted correctly: %s", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<CharacterResults> call, Throwable t) {
                t.printStackTrace();
                listener.onFailure(t);
            }
        });
    }
}
