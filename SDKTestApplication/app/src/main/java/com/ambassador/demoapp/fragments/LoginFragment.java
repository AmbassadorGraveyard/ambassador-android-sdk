package com.ambassador.demoapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        return view;
    }

    protected View.OnClickListener btnLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = etEmail.getText().toString();
            Toast.makeText(getActivity(), "Logging in!", Toast.LENGTH_LONG).show();
            Demo.get().identify(email);
            Demo.get().setEmail(email);
        }
    };

}
