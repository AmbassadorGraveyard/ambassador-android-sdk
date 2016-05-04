package com.ambassador.app.activities.main.settings;

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
import com.ambassador.app.Demo;
import com.ambassador.app.R;
import com.ambassador.app.activities.LaunchActivity;
import com.ambassador.app.activities.PresenterManager;
import com.ambassador.app.activities.main.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class SettingsFragment extends Fragment implements SettingsView, MainActivity.TabFragment {

    protected SettingsPresenter settingsPresenter;

    @Bind(R.id.ivDisplayPicture) protected NetworkCircleImageView ivDisplayPicture;
    @Bind(R.id.tvUserName) protected TextView tvUserName;
    @Bind(R.id.tvUniversalId) protected TextView tvUniversalId;
    @Bind(R.id.tvSdkToken) protected TextView tvSdkToken;
    @Bind(R.id.ivCopyUniversalId) protected ImageButton ivCopyUniversalId;
    @Bind(R.id.ivCopySdkToken) protected ImageButton ivCopySdkToken;
    @Bind(R.id.rlLogout) protected RelativeLayout rlLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            settingsPresenter = new SettingsPresenter();
        } else {
            settingsPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        ivCopyUniversalId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsPresenter.onCopyUniversalIdClicked();
            }
        });

        ivCopySdkToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsPresenter.onCopySdkTokenClicked();
            }
        });

        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsPresenter.onLogoutClicked();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        settingsPresenter.bindView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        settingsPresenter.unbindView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(settingsPresenter, outState);
    }

    @Override
    public void setUserPicture(String url) {
        if (!ivDisplayPicture.didLoad()) {
            ivDisplayPicture.load(url);
        }
    }

    @Override
    public void setUserName(String name) {
        tvUserName.setText(name);
    }

    @Override
    public void setUniversalId(String universalId) {
        tvUniversalId.setText(universalId);
    }

    @Override
    public void setSdkToken(String sdkToken) {
        tvSdkToken.setText(sdkToken);
    }

    @Override
    public void logout() {
        Intent intent = new Intent(getActivity(), LaunchActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void notifyCopiedToClipboard() {
        Toast.makeText(Demo.get(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionClicked() {
        // This fragment has no action.
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
