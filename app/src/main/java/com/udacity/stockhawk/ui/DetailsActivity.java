package com.udacity.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.stockhawk.R;

public class DetailsActivity extends AppCompatActivity {
    public final static String STOCK_KEY="STOCK_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        DetailsFragment detailsFragment=new DetailsFragment();
        String symbol= (String) getIntent().getExtras().get(STOCK_KEY);
        Bundle bundle=new Bundle();

        bundle.putString(DetailsFragment.SYMBOL_KEY,symbol !=null ? symbol:"");
        detailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.details_container,detailsFragment).commit();
    }
}
