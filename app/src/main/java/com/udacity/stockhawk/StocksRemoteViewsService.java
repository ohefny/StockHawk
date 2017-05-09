package com.udacity.stockhawk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class StocksRemoteViewsService extends android.widget.RemoteViewsService {

    public StocksRemoteViewsService() {
    }
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StocksRemoteViewFactory(this.getApplicationContext(),intent);
    }
}
