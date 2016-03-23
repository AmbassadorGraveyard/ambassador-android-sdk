package com.ambassador.demoapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.demoapp.R;
import com.ambassador.demoapp.api.Requests;
import com.ambassador.demoapp.api.pojo.LoginResponse;
import com.ambassador.demoapp.data.User;
import com.ambassador.demoapp.views.LoginEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    @Bind(R.id.etEmail) protected LoginEditText etEmail;
    @Bind(R.id.etPassword) protected LoginEditText etPassword;
    @Bind(R.id.btnLogin) protected Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.isStored()) {
            finishLogin();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        etEmail.setHint("Email address");
        etEmail.setImage(R.drawable.username_icon);
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etPassword.setHint("Password");
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setImage(R.drawable.password_icon);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = etEmail.getText();
                String password = etPassword.getText();
                Requests.get().login(emailAddress, password, new Callback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse, Response response) {
                        User.get().load(loginResponse);
                        finishLogin();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(LoginActivity.this, "Incorrect email/password!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected void finishLogin() {
        Intent next = new Intent(LoginActivity.this, MainActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(next);
    }

}
