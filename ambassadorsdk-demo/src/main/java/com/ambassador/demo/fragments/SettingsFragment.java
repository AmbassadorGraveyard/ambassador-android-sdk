package com.ambassador.demo.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.internal.views.NetworkCircleImageView;
import com.ambassador.demo.R;
import com.ambassador.demo.activities.LaunchActivity;
import com.ambassador.demo.activities.MainActivity;
import com.ambassador.demo.data.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class SettingsFragment extends Fragment implements MainActivity.TabFragment {

    @Bind(R.id.ivDisplayPicture) protected NetworkCircleImageView ivDisplayPicture;
    @Bind(R.id.tvSettingsName) protected TextView tvSettingsName;
    @Bind(R.id.tvUniversalId) protected TextView tvUniversalId;
    @Bind(R.id.tvSdkToken) protected TextView tvSdkToken;
    @Bind(R.id.ivCopyUniversalId) protected ImageButton ivCopyUniversalId;
    @Bind(R.id.ivCopySdkToken) protected ImageButton ivCopySdkToken;
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

        ivCopyUniversalId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(User.get().getUniversalId());
            }
        });

        ivCopySdkToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(User.get().getSdkToken());
            }
        });

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

    protected void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("simpleText", text));
        Toast.makeText(getActivity(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionClicked() {

    }

    @Override
    public Drawable getActionDrawable() {
        return ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_white);
    }

    @Override
    public boolean getActionVisibility() {
        return false;
    }

}
