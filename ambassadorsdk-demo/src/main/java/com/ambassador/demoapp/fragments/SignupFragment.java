package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.demoapp.CustomizationPackage;
import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class SignupFragment extends Fragment {

    @Bind(R.id.etEmail)     protected EditText  etEmail;
    @Bind(R.id.etUsername)  protected EditText  etUsername;
    @Bind(R.id.etPassword)  protected EditText  etPassword;
    @Bind(R.id.btnSignup)   protected Button    btnSignup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);

        btnSignup.setOnClickListener(btnLoginOnClickListener);
        btnSignup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("amb-zip", new CustomizationPackage(getActivity()).add("raf.xml", RAFOptions.get()).zip());
                File file = new File(getContext().getFilesDir(), "test.zip");
                Uri uri = FileProvider.getUriForFile(getContext(), "com.ambassador.fileprovider", file);
                final Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("*/*")
                        .setStream(uri)
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                getActivity().startActivity(intent);

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

    protected View.OnClickListener btnLoginOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (etEmail.getText().length() != 0 && etUsername.getText().length() != 0 && etPassword.getText().length() != 0) {
                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                Toast.makeText(getActivity(), "Signing up!", Toast.LENGTH_LONG).show();
                Demo.get().signupConversion(email, username);
                closeSoftKeyboard();
            } else {
                Toast.makeText(getActivity(), "Please enter an email, username, and password!", Toast.LENGTH_LONG).show();
            }
        }
    };

}
