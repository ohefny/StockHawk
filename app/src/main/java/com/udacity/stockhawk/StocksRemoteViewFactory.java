package com.udacity.stockhawk;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.DetailsActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

/**
 * Created by BeTheChange on 5/1/2017.
 */

public class StocksRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    Cursor mCursor;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    private float price;
    private float percentageChange;
    private float absoluteChange;
    private String symbol;
    private ContentResolver mContentResolver;

    public StocksRemoteViewFactory(Context context,Intent intent){
        this.mContext=context;
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        dollarFormat.setPositivePrefix("$");
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");


    }
    @Override
    public void onCreate() {

        mContentResolver=mContext.getContentResolver();

    }

    @Override
    public void onDataSetChanged() {
        Uri uri= Contract.Quote.URI;
        mCursor=mContentResolver.query(uri,(Contract.Quote.QUOTE_COLUMNS).toArray(new String[]{}),null, null,Contract.Quote.COLUMN_SYMBOL);

        Set<String> stockPref = PrefUtils.getStocks(mContext);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mCursor==null)
            return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0)
            return null;
        mCursor.moveToPosition(position);
        price=mCursor.getFloat(Contract.Quote.POSITION_PRICE);
        percentageChange=mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        absoluteChange=mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        symbol=mCursor.getString(Contract.Quote.POSITION_SYMBOL);
        RemoteViews remoteView=new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);
        remoteView.setTextViewText(R.id.symbol,symbol);
        remoteView.setTextViewText(R.id.price,dollarFormat.format(price));
        if(absoluteChange>0)
            remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        else
            remoteView.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);
        String change=dollarFormatWithPlus.format(absoluteChange);
        String percentage=percentageFormat.format(percentageChange / 100);
        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            remoteView.setTextViewText(R.id.change,change);
        } else {
            remoteView.setTextViewText(R.id.change,percentage);
        }
        Intent fillIntent=new Intent();
        fillIntent.putExtra(DetailsActivity.STOCK_KEY,symbol);
        remoteView.setOnClickFillInIntent(R.id.quote_widget_item,fillIntent);
        return remoteView;
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
}
