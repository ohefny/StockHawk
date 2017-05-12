package com.udacity.stockhawk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.CursorAdapter;

import com.udacity.stockhawk.data.Contract.Quote;
import com.udacity.stockhawk.data.Contract.Symbols;


class DbHelper extends SQLiteOpenHelper {


    private static final String NAME = "StockHawk.db";
    private static final int VERSION = 1;


    DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qouteStm = new String("CREATE TABLE " + Quote.TABLE_NAME + " ("
                + Quote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Quote.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + Quote.COLUMN_PRICE + " REAL NOT NULL, "
                + Quote.COLUMN_ABSOLUTE_CHANGE + " REAL NOT NULL, "
                + Quote.COLUMN_PERCENTAGE_CHANGE + " REAL NOT NULL, "
                + Quote.COLUMN_HISTORY + " TEXT NOT NULL, "
                + Quote.COLUMN_NAME + " TEXT NOT NULL, "
                + "UNIQUE (" + Quote.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);");
        String symbolsStm = new String("CREATE TABLE "+ Symbols.TABLE_NAME +" ("
                + Symbols._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Symbols.COLUMN_SYMBOL+" TEXT NOT NULL, "
                + Symbols.COLUMN_ISSUE_NAME+" TEXT , "
                + Symbols.COLUMN_Primary_Listing_Mkt+" TEXT," +
                " UNIQUE (" + Symbols.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);)");

        Log.d(DbHelper.class.getSimpleName(),symbolsStm+qouteStm);
        db.execSQL(qouteStm);
        db.execSQL(symbolsStm);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Quote.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + Symbols.TABLE_NAME);

        onCreate(db);
    }
}
