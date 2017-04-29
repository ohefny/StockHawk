package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String SYMBOL_KEY="SYM_KEY";
    private static final int HISTORY_LOADER_ID=1;
    private static final String HISTORY_KEY="HISTORY";
    String mSymblol="";
    @BindView(R.id.chart)LineChart mChart;
    private String mHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().get(SYMBOL_KEY)!=null)
            mSymblol=getArguments().getString(SYMBOL_KEY);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mHistory!=null&&!mHistory.isEmpty())
            outState.putString(HISTORY_KEY,mHistory);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_details_, container, false);
        ButterKnife.bind(this,root);
        if(savedInstanceState==null||savedInstanceState.get(HISTORY_KEY)==null)
            getLoaderManager().initLoader(HISTORY_LOADER_ID,null,this);
        else{
            mHistory=savedInstanceState.getString(HISTORY_KEY);
            onHistoryLoaded();
        }
        return root;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader mHistoryLoader = new CursorLoader(getActivity(),
                Contract.Quote.URI,
                Contract.Quote.QOUTE_HISTORY_COLUMN.toArray(new String[]{}),
                "symbol like ?", new String[]{mSymblol}, null);
        return mHistoryLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int idx=data.getColumnIndex(Contract.Quote.COLUMN_HISTORY);
        while(data.moveToNext())
            mHistory=data.getString(idx);
        data.close();
        onHistoryLoaded();
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    private void onHistoryLoaded() {
        String[]lines=mHistory.split("\n");
        final List<String> xVals=new ArrayList<>();
        List<Entry> list= new ArrayList<>();
        int count=0;
        for(int i=lines.length-1;i>=0;i--){
            String[]vals=lines[count++].split(",");
            xVals.add(vals[0]);
            list.add(new Entry((float)(count-1),(float)Double.parseDouble(vals[1])));
        }
        LineDataSet lineDataSet=new LineDataSet(list,mSymblol);
        LineData lineData=new LineData(lineDataSet);
        XAxis xAxis=mChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVals.get(xVals.size()-(int)value -1);
            }
        });
        mChart.setData(lineData);
        mChart.invalidate();

    }
}
