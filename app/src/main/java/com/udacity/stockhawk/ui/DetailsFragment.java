package com.udacity.stockhawk.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsFragment extends Fragment {
    public static final String SYMBOL_KEY="SYM_KEY";
    String mSymblol="";
    @BindView(R.id.placeHolder)TextView tv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().get(SYMBOL_KEY)!=null)
            mSymblol=getArguments().getString(SYMBOL_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_details_, container, false);
        ButterKnife.bind(this,root);
        tv.setText(mSymblol);
        return root;
    }
}
