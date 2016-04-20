package com.ambassador.demo.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demo.R;
import com.ambassador.demo.activities.main.MainActivity;
import com.ambassador.demo.api.Requests;
import com.ambassador.demo.api.pojo.LoginResponse;
import com.ambassador.demo.data.User;
import com.ambassador.demo.views.LoginEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends Activity {

    @Bind(R.id.etEmail) protected LoginEditText etEmail;
    @Bind(R.id.etPassword) protected LoginEditText etPassword;
    @Bind(R.id.btnLogin) protected Button btnLogin;
    @Bind(R.id.tvNoAccount) protected TextView tvNoAccount;

    @Bind(R.id.rlLoading) protected FrameLayout rlLoading;
    @Bind(R.id.pbLoading) protected ProgressBar pbLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#1a232d"));
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        etEmail.setHint("Email address");
        etEmail.setImage(R.drawable.username_icon);
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setPosition(LoginEditText.Position.TOP);

        etPassword.setHint("Password");
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setImage(R.drawable.password_icon);
        etPassword.setPosition(LoginEditText.Position.BOTTOM);
        etPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = etEmail.getText();
                String password = etPassword.getText();

                if (emailAddress == null || "".equals(emailAddress) || password == null || "".equals(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter an email and password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(new Identify(emailAddress).isValidEmail())) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                enterLoading();
                closeSoftKeyboard();

                Requests.get().login(emailAddress, password, new Callback<LoginResponse>() {
                    @Override
                    public void success(LoginResponse loginResponse, Response response) {
                        exitLoading();
                        User.get().load(loginResponse);
                        finishLogin();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(LoginActivity.this, "Incorrect email/password!", Toast.LENGTH_SHORT).show();
                        exitLoading();
                    }
                });
            }
        });

        tvNoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.getambassador.com/schedule-a-demo"));
                startActivity(intent);
            }
        });

        pbLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

    protected void enterLoading() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlLoading, "alpha", 0, 0.75f);
        animator.setDuration(300);
        animator.start();
    }

    protected void exitLoading() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlLoading, "alpha", 0.75f, 0);
        animator.setDuration(300);
        animator.start();
    }

    protected void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);
    }

    protected void finishLogin() {
        Intent next = new Intent(LoginActivity.this, MainActivity.class);
        next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(next);
    }

}
