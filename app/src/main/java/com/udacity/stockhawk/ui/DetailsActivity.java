package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public final static String STOCK_KEY="STOCK_KEY";
    private final int COOPERATION_NAME =1;
    String symbol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        DetailsFragment detailsFragment=new DetailsFragment();
        symbol= (String) getIntent().getExtras().get(STOCK_KEY);
        Bundle bundle=new Bundle();
        getSupportLoaderManager().initLoader(COOPERATION_NAME,null,(LoaderManager.LoaderCallbacks<Cursor>)this);
        bundle.putString(DetailsFragment.SYMBOL_KEY,symbol !=null ? symbol:"");
        detailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.details_container,detailsFragment).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI,new String[]{Contract.Quote.COLUMN_NAME}
                ,"symbol = ?",new String[]{symbol},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            while(data.moveToNext())
                updateIssueName(data.getString(0));
            data.close();
    }

    private void updateIssueName(String string) {
        if(string!=null||string.isEmpty())
            getSupportActionBar().setTitle(string);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
