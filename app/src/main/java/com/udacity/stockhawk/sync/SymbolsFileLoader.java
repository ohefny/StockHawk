package com.udacity.stockhawk.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.udacity.stockhawk.data.Contract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by BeTheChange on 4/15/2017.
 */

public class SymbolsFileLoader extends AsyncTaskLoader<Integer> {
    private final String path;
    private StringBuilder stringBuilder;
    private Integer inserted;
    public SymbolsFileLoader(Context context, String path) {
        super(context);
        this.path=path;
        //startLoading();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if(stringBuilder==null)
            forceLoad();
        else
            deliverResult(inserted);
        Log.d("OnStartLoading","Fuck "+Thread.currentThread().getName()+" ID::"+ Thread.currentThread().getId());
    }

    @Override
    protected Integer onLoadInBackground() {
        Log.d("onLoadInBackground","Fuck "+Thread.currentThread().getName()+" ID::"+ Thread.currentThread().getId());
        return super.onLoadInBackground();
    }

    @Override
    public Integer loadInBackground() {
        Log.d(SymbolsFileLoader.class.getSimpleName(),"Fuck "+Thread.currentThread().getName()+" ID::"+ Thread.currentThread().getId());
        Log.d(SymbolsFileLoader.class.getSimpleName(),"Start Loading");
        File file=new File(path);
        stringBuilder=new StringBuilder();
        try {
            FileInputStream fileReader=new FileInputStream(file);
            BufferedReader inputStreamReader=new BufferedReader(new InputStreamReader(fileReader));
            while (inputStreamReader.ready()){
                String string=inputStreamReader.readLine();
                stringBuilder.append(string);
                stringBuilder.append('\n');

            }
            inputStreamReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[]values=stringBuilder.toString().split("\n");
        ContentValues[]contentValues=new ContentValues[values.length];
        for(int i=1;i<values.length;i++){
            String[]vals=split(values[i].toCharArray());
            contentValues[i]=new ContentValues();
            contentValues[i].put(Contract.Symbols.COLUMN_SYMBOL,vals[0]);
            contentValues[i].put(Contract.Symbols.COLUMN_ISSUE_NAME,vals[1]);
            contentValues[i].put(Contract.Symbols.COLUMN_Primary_Listing_Mkt,vals[2]);
        }

        inserted=getContext().getContentResolver().bulkInsert(Contract.Symbols.URI,contentValues);
       // deliverResult(stringBuilder.toString());
        return inserted;
    }

    private String[] split(char[] chars) {
        String[]strings=new String[]{new String(),new String(),new String()};
        int count=0;
        for(int i=0;i<chars.length;i++){
            if(chars[i]!='|'){
                strings[count]+=chars[i];
            }
            else
                count++;

        }
        return strings;
    }
}
