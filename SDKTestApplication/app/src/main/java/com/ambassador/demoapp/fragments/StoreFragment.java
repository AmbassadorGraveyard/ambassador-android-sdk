package com.ambassador.demoapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class StoreFragment extends Fragment {

    @Bind(R.id.btnBuy) protected Button btnBuy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, view);

        btnBuy.setOnClickListener(btnBuyOnClickListener);

        return view;
    }

    protected View.OnClickListener btnBuyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "hit", Toast.LENGTH_SHORT).show();
        }
    };

}
