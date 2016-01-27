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

import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class SignupFragment extends Fragment {

    @Bind(R.id.etEmail)     protected EditText  etEmail;
    @Bind(R.id.etUsername)  protected EditText  etUsername;
    @Bind(R.id.etPassword)  protected EditText  etPassword;
    @Bind(R.id.btnSignup)    protected Button   btnSignup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);

        btnSignup.setOnClickListener(btnLoginOnClickListener);

        return view;
    }

    protected View.OnClickListener btnLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "hit", Toast.LENGTH_LONG).show();
        }
    };
}
