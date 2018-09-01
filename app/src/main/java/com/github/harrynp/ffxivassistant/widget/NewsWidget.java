package com.github.harrynp.ffxivassistant.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.github.harrynp.ffxivassistant.MainActivity;
import com.github.harrynp.ffxivassistant.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewsWidgetConfigureActivity NewsWidgetConfigureActivity}
 */
public class NewsWidget extends AppWidgetProvider{

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String newsName = NewsWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.news_widget);
        views.setTextViewText(R.id.tv_news_widget_name, newsName);

        Intent titleIntent = new Intent(context, MainActivity.class);
        PendingIntent titlePendingIntent = PendingIntent.getActivity(context, 0, titleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tv_news_widget_name, titlePendingIntent);

        Intent intent = new Intent(context, NewsWidgetService.class);

        //Fix from https://stackoverflow.com/questions/11350287/ongetviewfactory-only-called-once-for-multiple-widgets
//        intent.putExtra("NEWS_NAME", newsName);
        intent.setData(Uri.fromParts("content", newsName, null));
        views.setRemoteAdapter(R.id.lv_widget, intent);

        Intent clickIntentTemplate = new Intent(Intent.ACTION_VIEW);
        PendingIntent clickPendingIntentTemplate = PendingIntent.getActivity(context, 0, clickIntentTemplate, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget, clickPendingIntentTemplate);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NewsWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

