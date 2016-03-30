package com.ambassador.demoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.internal.views.NetworkCircleImageView;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.activities.LaunchActivity;
import com.ambassador.demoapp.data.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class SettingsFragment extends Fragment {

    @Bind(R.id.ivDisplayPicture) protected NetworkCircleImageView ivDisplayPicture;
    @Bind(R.id.tvSettingsName) protected TextView tvSettingsName;
    @Bind(R.id.tvUniversalId) protected TextView tvUniversalId;
    @Bind(R.id.tvSdkToken) protected TextView tvSdkToken;
    @Bind(R.id.rlLogout) protected RelativeLayout rlLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        ivDisplayPicture.load(User.get().getAvatarUrl());
        tvSettingsName.setText(User.get().getName());
        tvUniversalId.setText(User.get().getUniversalId());
        tvSdkToken.setText(User.get().getSdkToken());
        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.logout();
                Intent intent = new Intent(getActivity(), LaunchActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

}
