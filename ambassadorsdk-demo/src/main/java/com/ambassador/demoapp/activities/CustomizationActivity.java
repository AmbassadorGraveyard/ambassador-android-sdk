package com.ambassador.demoapp.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import com.ambassador.ambassadorsdk.internal.views.CircleImageView;
import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomizationActivity extends AppCompatActivity {

    @Bind(R.id.ivProductPhoto) protected CircleImageView ivProductPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);
        ButterKnife.bind(this);
        setUpActionBar();
        ivProductPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_photo));
    }

    protected void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarColor)));
        setTitle(Html.fromHtml("<small>Edit Refer a Friend View</small>"));
    }

}
