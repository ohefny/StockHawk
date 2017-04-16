package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.sync.SymbolsFileLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks,
        StockAdapter.StockAdapterOnClickHandler {

    private static final int AVAIL_SYMBOLS_LOADER = 1;
    private static final int DOWNLOAD_SYMBOLS_LOADER =2 ;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView stockRecyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error)
    TextView error;
    @BindView(R.id.fab) FloatingActionButton fab;
    private StockAdapter adapter;
    private static final int STOCK_LOADER = 0;
    public static final String NEW_STOCK_SYMBOL = "NEW-STOCK";
    MainFragmentActionListener mListener;
    BroadcastReceiver mBroadcastReciver;
    private CursorLoader mUserStocksLoader;
    private CursorLoader mAvailableSymbolsLoader;
    private SymbolsFileLoader mDownloadSymbolsLoader;
    private ArrayAdapter autoCompleteAdapter;
    private AddStockDialog addStockDialog;
    private boolean networkProblem=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainFragment.class.getSimpleName(),"Fuck "+Thread.currentThread().getName()+" ID::"+ Thread.currentThread().getId());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_main,container,false);
        ButterKnife.bind(this,view);
        adapter = new StockAdapter(getActivity(), this);
        stockRecyclerView.setAdapter(adapter);
        stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
        autoCompleteAdapter=new ArrayAdapter(getActivity(),R.layout.suggest_item);
        addStockDialog= new AddStockDialog();
        onRefresh();

        QuoteSyncJob.initialize(getActivity());


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String symbol = adapter.getSymbolAtPosition(viewHolder.getAdapterPosition());
                PrefUtils.removeStock(getActivity(), symbol);
                getActivity().getContentResolver().delete(Contract.Quote.makeUriForStock(symbol), null, null);
            }
        }).attachToRecyclerView(stockRecyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStockDialog.show(getActivity().getFragmentManager(), "StockDialogFragment");

            }
        });

        return view;
    }

    @Override
    public void onRefresh() {

        QuoteSyncJob.syncImmediately(getActivity());

        isErrorOccur();
    }

    private void isErrorOccur() {
        if (!networkUp() && adapter.getItemCount() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_network));
            error.setVisibility(View.VISIBLE);
        } else if (!networkUp()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.toast_no_connectivity, Toast.LENGTH_LONG).show();
        } else if (PrefUtils.getStocks(getActivity()).size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks));
            error.setVisibility(View.VISIBLE);
        } else if(adapter.getItemCount()==0&&networkProblem){
            swipeRefreshLayout.setRefreshing(false);
            error.setText(getString(R.string.error_no_stocks_no_network));
            error.setVisibility(View.VISIBLE);
        }

        else {
            error.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // IF 7 DAYS PAST SINCE THE LAST TIME SYMBOLS UPDATED LOAD FROM INTERNTER THE NEW SYMBOLS LIST
        Calendar prevDate=PrefUtils.getSymbolListLastUpdated(getActivity());
        Calendar nowDate=Calendar.getInstance();
        prevDate.add(Calendar.DAY_OF_MONTH,7);
        if(prevDate.compareTo(nowDate)>0){
            //TODO:: CHECK IF THE AVAILABLE SYMBOLS HAS TO BE UPDATED IF SO DOWNLOAD FROM INTERNET
            getLoaderManager().initLoader(DOWNLOAD_SYMBOLS_LOADER, null, this);
        }

       getLoaderManager().initLoader(STOCK_LOADER, null, this);
       getLoaderManager().initLoader(AVAIL_SYMBOLS_LOADER, null, this);

       super.onActivityCreated(savedInstanceState);
    }
    private boolean networkUp() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void button(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getActivity().getFragmentManager(), "StockDialogFragment");
    }



    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id==STOCK_LOADER){
                mUserStocksLoader= new CursorLoader(getActivity(),
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
                return mUserStocksLoader;

        }
        else if (id==AVAIL_SYMBOLS_LOADER)
        {
            mAvailableSymbolsLoader=new CursorLoader(getActivity(),
                    Contract.Symbols.URI,
                    Contract.Symbols.SYMBOLS_COL.toArray(new String[]{}),null,null,Contract.Symbols.COLUMN_SYMBOL
                    );

            return  mAvailableSymbolsLoader;
        }
        else if(id==DOWNLOAD_SYMBOLS_LOADER){
            mDownloadSymbolsLoader=new SymbolsFileLoader(getActivity(),getActivity().getFilesDir().getPath().toString()+"/symb.txt");
           // mDownloadSymbolsLoader.startLoading();
            return mDownloadSymbolsLoader;

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        if(loader.equals(mUserStocksLoader)){
            Cursor cursorData=(Cursor)data;
            swipeRefreshLayout.setRefreshing(false);

            if (cursorData.getCount() != 0) {
                error.setVisibility(View.GONE);
            }
           // else if(!networkUp()){
             //   error.setVisibility(View.VISIBLE);
            //}
            adapter.setCursor(cursorData);
        }
        else if(loader.equals(mAvailableSymbolsLoader)){
            Cursor cursorData=(Cursor)data;
            List<String> c=new ArrayList<String>();
            while (cursorData.moveToNext())
                c.add(cursorData.getString(0));

            autoCompleteAdapter.clear();
            autoCompleteAdapter.addAll(c);
            addStockDialog.setAdapter(autoCompleteAdapter);

            //TODO: ADD THIS DATA CURSOR TO THE SUGGESTION TEXT ADAPTER

        }
        else if(loader.equals(mDownloadSymbolsLoader)){
            if((Integer)(data)>0)
                PrefUtils.updateSymbolListLastUpdated(getActivity());
            Log.d(MainFragment.class.getSimpleName(),"Fuck Inserted Symbols :: "+data.toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener=(MainFragmentActionListener)getActivity();
        setupReceiver();
        getActivity().registerReceiver(mBroadcastReciver,new IntentFilter(QuoteSyncJob.ACTION_DATA_NOT_FOUND));
        getActivity().registerReceiver(mBroadcastReciver,new IntentFilter(QuoteSyncJob.ACTION_NETWORK_PROBLEM));
    }

    private void setupReceiver() {
        mBroadcastReciver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(QuoteSyncJob.ACTION_DATA_UPDATED)){
                    getLoaderManager().restartLoader(STOCK_LOADER,null,MainFragment.this);
                    networkProblem=false;
                }
                else if(intent.getAction().equals(QuoteSyncJob.ACTION_DATA_NOT_FOUND)) {
                    //TODO::REPLACE IT WITH SNACKBAR
                    Toast.makeText(getActivity(), intent.getStringExtra(NEW_STOCK_SYMBOL) + "Not Found", Toast.LENGTH_SHORT).show();
                    PrefUtils.removeStock(getActivity(),intent.getStringExtra(NEW_STOCK_SYMBOL));
                }
                else if(intent.getAction().equals(QuoteSyncJob.ACTION_NETWORK_PROBLEM)){
                    networkProblem=true;
                    isErrorOccur();
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mBroadcastReciver);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setCursor(null);
    }


    @Override
    public void onClick(String symbol) {
        mListener.onItemClicked(symbol);
    }

    public void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void addStock(String symbol) {
            if (symbol != null && !symbol.isEmpty()) {

                if (networkUp()) {
                    swipeRefreshLayout.setRefreshing(true);
                } else {
                    String message = getString(R.string.toast_stock_added_no_connectivity, symbol);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }

                PrefUtils.addStock(getActivity(), symbol);
                QuoteSyncJob.syncImmediately(getActivity());
            }

    }






    public static interface MainFragmentActionListener{
         void onItemClicked(String symbol);

    }

}
