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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
            URL symbsFile=new URL("http://oatsreportable.finra.org/OATSReportableSecurities-EOD.txt");
            HttpURLConnection urlConnection = (HttpURLConnection) symbsFile.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.d("FUCK URL ::",symbsFile.toString());
            InputStreamReader fileReader=new InputStreamReader(
                  urlConnection.getInputStream());
            BufferedReader inputStreamReader=new BufferedReader(fileReader);
            String line;
            while ((line = inputStreamReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');

            }
            inputStreamReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
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
