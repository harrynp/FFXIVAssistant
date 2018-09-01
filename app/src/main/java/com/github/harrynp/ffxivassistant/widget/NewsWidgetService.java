package com.github.harrynp.ffxivassistant.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.github.harrynp.ffxivassistant.R;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Lodestone;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.LodestoneNews;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Maintenance;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Notice;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Status;
import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Topic;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NewsWidgetRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class NewsWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private String mNewsName;
    private final String XIVDB_BASE_URL = "https://xivdb.com/assets/";
    private final String LODESTONE_PATH = "lodestone.json";
    List<LodestoneNews> mNewsList;


    public NewsWidgetRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
        //Fix from https://stackoverflow.com/questions/11350287/ongetviewfactory-only-called-once-for-multiple-widgets
//        mNewsName = intent.getStringExtra("NEWS_NAME");
        mNewsName = intent.getData().getSchemeSpecificPart();
    }

    @Override
    public void onCreate() {
        mNewsList = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        Lodestone lodestone = null;
        try {
            lodestone = getLodestone();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lodestone != null){
            mNewsList.clear();
            if (mNewsName.equals("Topics")) {
                mNewsList.addAll(lodestone.getTopics());
            } else if (mNewsName.equals("Notices")){
                mNewsList.addAll(lodestone.getNotices());
            } else if (mNewsName.equals("Maintenance")){
                mNewsList.addAll(lodestone.getMaintenance());
            } else if (mNewsName.equals("Status")){
                mNewsList.addAll(lodestone.getStatus());
            }
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mNewsList == null) return 0;
        return mNewsList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mNewsList == null || mNewsList.size() == 0){
            return null;
        }
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_news_widget);

        views.setTextViewText(R.id.tv_news_title, mNewsList.get(position).getTitle());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(mNewsList.get(position).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            String pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMMddyyyyhhmmaa");
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            String postDate = format.format(date);
            views.setTextViewText(R.id.tv_news_post_time, "Post time: " + postDate);
        }
        if (mNewsList.get(position).getUrl() != null) {
            Intent fillInIntent = new Intent();
            fillInIntent.setData(Uri.parse(mNewsList.get(position).getUrl()));
            views.setOnClickFillInIntent(R.id.ll_list_item_news_widget, fillInIntent);
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private Lodestone getLodestone() throws IOException {
        // These two need to be declared outside the try/catch so that they can be closed in the finally block.
        Uri builtUri = Uri.parse(XIVDB_BASE_URL).buildUpon()
                .appendPath(LODESTONE_PATH)
                .build();
        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        if (url == null){
            return null;
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) urlConnection.getContent()));
        urlConnection.disconnect();
        return gson.fromJson(root, Lodestone.class);
    }
}