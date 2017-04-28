package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.udacity.stockhawk.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        Set<String>results= prefs.getStringSet(stocksKey, new HashSet<String>());
        Log.d(PrefUtils.class.getSimpleName(),results.toString());
        return results;
    }
    public static Calendar getSymbolListLastUpdated(Context context){
        String symbolListLasUpdatedKey=context.getString(R.string.pref_symbols_last_updated);
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        String lastUpdated;
        lastUpdated=prefs.getString(symbolListLasUpdatedKey,"1990-01-01");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();

        try {
            date=sdf.parse(lastUpdated);
        }
        catch (Exception e){

            return null;
        }
        Log.d(PrefUtils.class.getSimpleName(),"Returned "+sdf.format(date));
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        //Log.d("Calender",calendar.getDisplayName());
        return calendar;

    }
    public static void updateSymbolListLastUpdated(Context context){
        String symbolListLasUpdatedKey=context.getString(R.string.pref_symbols_last_updated);
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastUpdated=sdf.format(new Date()).toString();
        Log.d(PrefUtils.class.getSimpleName(),"To Be Stored"+lastUpdated);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString(symbolListLasUpdatedKey,lastUpdated);
        editor.apply();
    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);
        Set<String>newStocks=new HashSet<>();
        newStocks.addAll(stocks);
        if (add) {
            newStocks.add(symbol);
        } else {
            newStocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, newStocks);

        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

}
