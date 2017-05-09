package com.udacity.stockhawk;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import static com.udacity.stockhawk.StocksWidget.updateAppWidget;

/**
 * Created by BeTheChange on 5/9/2017.
 */

public class UpdateWidgetsService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public static final String UPDATE_WIDGETS_ACTION="UPDATE_WIDGETS";
    public UpdateWidgetsService(){
        super("UpdateWidgetsService");
    }
    public UpdateWidgetsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
             if(intent.getAction().equals(UPDATE_WIDGETS_ACTION)){
                 updateAllWidgets(this);
             }
    }
    private static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, StocksWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.widget_list_view);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    public static void startUpdateWidgetsService(Context context) {
        Intent intent=new Intent(context, UpdateWidgetsService.class);
        intent.setAction(UpdateWidgetsService.UPDATE_WIDGETS_ACTION);
        context.startService(intent);
    }
}
