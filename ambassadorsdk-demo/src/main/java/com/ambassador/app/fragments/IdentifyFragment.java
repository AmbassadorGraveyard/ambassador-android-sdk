package com.ambassador.app.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.app.BuildConfig;
import com.ambassador.app.R;
import com.ambassador.app.activities.MainActivity;
import com.ambassador.app.exports.Export;
import com.ambassador.app.exports.IdentifyExport;
import com.ambassador.app.utils.Share;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class IdentifyFragment extends Fragment implements MainActivity.TabFragment {

    @Bind(R.id.etIdentify)  protected EditText  etEmail;
    @Bind(R.id.btnIdentify) protected Button    btnIdentify;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identify, container, false);
        ButterKnife.bind(this, view);

        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().length() != 0) {
                    String email = etEmail.getText().toString();
                    if (!(new Identify(email).isValidEmail())) {
                        new Device(getActivity()).closeSoftKeyboard(etEmail);
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid email address!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                etEmail.requestFocus();
                            }
                        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
                        return;
                    }
                    Toast.makeText(getActivity(), "Identifying!", Toast.LENGTH_LONG).show();
                    AmbassadorSDK.identify(email);
                    closeSoftKeyboard();
                } else {
                    new Device(getActivity()).closeSoftKeyboard(etEmail);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter an email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etEmail.requestFocus();
                        }
                    }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
                }
            }
        });

        view.findViewById(R.id.ivFlags).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Version Code: " + BuildConfig.VERSION_CODE, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        etEmail.requestFocus();
        closeSoftKeyboard();
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    @Override
    public void onActionClicked() {
        if (etEmail == null || !new Identify(etEmail.getText().toString()).isValidEmail()) {
            new Device(getActivity()).closeSoftKeyboard(etEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid email address!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return;
        }

        Export<String> export = new IdentifyExport();
        export.setModel(etEmail.getText().toString());
        String filename = export.zip(getActivity());
        new Share(filename).withSubject("Ambassador Identify Instructions").withBody(export.getReadme()).execute(getActivity());
    }

    @Override
    public Drawable getActionDrawable() {
        return ContextCompat.getDrawable(getActivity(), R.drawable.ic_share_white);
    }

    @Override
    public boolean getActionVisibility() {
        return true;
    }

}