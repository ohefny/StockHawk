package com.udacity.stockhawk;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.*;

import com.udacity.stockhawk.ui.DetailsActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StocksWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stocks_widget);
        // views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent remoteAdapterIntent = new Intent(context, StocksRemoteViewsService.class);
        views.setRemoteAdapter(R.id.widget_list_view, remoteAdapterIntent);
        Intent templateIntent = new Intent(context, DetailsActivity.class);
        views.setPendingIntentTemplate(R.id.widget_list_view, PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        views.setEmptyView(R.id.widget_list_view, R.id.emptyView);
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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }



}

