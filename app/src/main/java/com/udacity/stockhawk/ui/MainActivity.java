package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentActionListener{
    private boolean mTwoPanel=false;
    private MainFragment mMain_Fragment;
    private DetailsFragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.details_container) != null) {
            mTwoPanel = true;
            if (savedInstanceState == null) {
                mDetailsFragment = new DetailsFragment();
                Bundle bundle=new Bundle();
                bundle.putString(DetailsFragment.SYMBOL_KEY,"");
                mDetailsFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add
                        (R.id.details_container,mDetailsFragment).commit();
            }
        } else
            mTwoPanel = false;

    }

    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
        } else {
            item.setIcon(R.drawable.ic_dollar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof MainFragment)
            mMain_Fragment=((MainFragment)(fragment));
        else
            mDetailsFragment=(DetailsFragment)(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            if(mMain_Fragment!=null)
                mMain_Fragment.updateAdapter();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(String symbol) {
        if(mTwoPanel){
            Bundle bundle=new Bundle();
            bundle.putString(DetailsFragment.SYMBOL_KEY,symbol !=null ? symbol:"");
            mDetailsFragment.argumentsUpdated(bundle);
        }
        else{
            Intent intent=new Intent(this,DetailsActivity.class);
            intent.putExtra(DetailsActivity.STOCK_KEY,symbol);
            startActivity(intent);
        }

    }
    void addStock(String symbol) {
        if (mMain_Fragment!=null) {
              mMain_Fragment.addStock(symbol);
        }
    }
}
