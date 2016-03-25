package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.demoapp.BuildConfig;
import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class LoginFragment extends Fragment {

    @Bind(R.id.etEmail)     protected EditText  etEmail;
    @Bind(R.id.btnIdentify) protected Button    btnIdentify;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        btnIdentify.setOnClickListener(btnIdentifyOnClickListener);
        view.findViewById(R.id.ivFlags).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Version Code: " + BuildConfig.VERSION_CODE, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnIdentify.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent data = new Intent();
                data.putExtra("referrer", "mbsy_cookie_code=jwnZ&device_id=test1234");
                InstallReceiver.getInstance().onReceive(getActivity(), data);
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

    protected View.OnClickListener btnIdentifyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (etEmail.getText().length() != 0) {
                String email = etEmail.getText().toString();
                Toast.makeText(getActivity(), "Logging in!", Toast.LENGTH_LONG).show();
                Demo.get().identify(email);
                closeSoftKeyboard();
            } else {
                Toast.makeText(getActivity(), "Please enter an email and password!", Toast.LENGTH_LONG).show();
            }
        }
    };
    
    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

}
