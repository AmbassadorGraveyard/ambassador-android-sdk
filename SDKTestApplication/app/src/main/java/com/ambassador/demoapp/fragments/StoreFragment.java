package com.ambassador.demoapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.MainActivity;
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
            if (Demo.get().getEmail() != null) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Purchase successful")
                        .setMessage("Thank you for buying from Ambassador!")
                        .setPositiveButton("Done", null)
                        .show();

                Demo.get().conversion();
            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Authentication needed")
                        .setMessage("Please login to complete your purchase.")
                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MainActivity) getActivity()).switchToTabAtIndex(0);
                            }
                        }).setNegativeButton("Cancel", null)
                        .show();
            }
        }
    };

}
