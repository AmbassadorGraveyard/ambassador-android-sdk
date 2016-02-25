package com.ambassador.demoapp.fragments;

import android.content.Context;
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

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class LoginFragment extends Fragment {

    @Bind(R.id.etEmail)     protected EditText  etEmail;
    @Bind(R.id.etPassword)  protected EditText  etPassword;
    @Bind(R.id.btnLogin)    protected Button    btnLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        btnLogin.setOnClickListener(btnLoginOnClickListener);

        btnLogin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AmbassadorSDK.presentWelcomeScreen(getActivity());
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

    protected View.OnClickListener btnLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (etEmail.getText().length() != 0 && etPassword.getText().length() != 0) {
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
