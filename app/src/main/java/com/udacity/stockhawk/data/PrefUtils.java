package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.udacity.stockhawk.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtils {
//TODO::SHARED PREFERENCES STORE DEFAULT INFO EVEN AFTER DELETING
    private PrefUtils() {
    }

     public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        boolean initialized = prefs.getBoolean(initializedKey, false);
         Log.d("Hefny +PrefUtils","Intialized Key Exist :: "+prefs.contains(initializedKey));
       // Log.d("Hefny "+PrefUtils.class.getSimpleName(),"is Intialized "+initialized);
        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            if(editor.commit())
                Log.d("Hefny"+PrefUtils.class.getName(),"Successful Intialization");
            else
                Log.d("Hefny"+PrefUtils.class.getName(),"UnSuccessful Intialization");;
            return defaultStocks;
        }
        final Set<String>prefSet= prefs.getStringSet(stocksKey, new HashSet<String>());
        Log.d("Hefny "+PrefUtils.class.getSimpleName(),prefSet.toString());
        return prefSet;
    }

     private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);
        Set<String>newStocks=new HashSet<>(stocks);
        if (add) {
            newStocks.add(symbol);
        } else {
            newStocks.remove(symbol);
        }
        Log.d("Hefny"+PrefUtils.class.getName(),newStocks.toString());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, newStocks);
        if(editor.commit()){
            Log.d("Hefny"+PrefUtils.class.getName(),"Successful Commit");
            Log.d("Hefny"+PrefUtils.class.getName(),getStocks(context).toString());
        }
        else
            Log.d("Hefny"+PrefUtils.class.getName(),"Unsuccessful Commit");
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

        editor.commit();
    }

}
