package com.ambassador.app.activities.main.identify;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.app.R;
import com.ambassador.app.activities.PresenterManager;
import com.ambassador.app.activities.main.MainActivity;
import com.ambassador.app.utils.Share;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class IdentifyFragment extends Fragment implements IdentifyView, MainActivity.TabFragment {

    protected IdentifyPresenter identifyPresenter;

    @Bind(R.id.etIdentify) protected EditText etEmail;
    @Bind(R.id.etFirstName) protected EditText etFirstName;
    @Bind(R.id.etLastName) protected EditText etLastName;
    @Bind(R.id.etCompany) protected EditText etCompany;
    @Bind(R.id.etPhone) protected EditText etPhone;
    @Bind(R.id.etStreet) protected EditText etStreet;
    @Bind(R.id.etCity) protected EditText etCity;
    @Bind(R.id.etState) protected EditText etState;
    @Bind(R.id.etPostalCode) protected EditText etPostalCode;
    @Bind(R.id.etCountry) protected EditText etCountry;
    @Bind(R.id.swAutoEnroll) protected SwitchCompat swAutoEnroll;
    @Bind(R.id.rlCampaignChooser) protected RelativeLayout rlCampaignChooser;
    @Bind(R.id.btnIdentify) protected Button btnIdentify;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            identifyPresenter = new IdentifyPresenter();
        } else {
            identifyPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identify, container, false);
        ButterKnife.bind(this, view);

        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identifyPresenter.onSubmitClicked(etEmail.getText().toString());
            }
        });

        btnIdentify.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle traits = new Bundle();
                Bundle options = new Bundle();
                options.putString("campaign", "1048");
                AmbassadorSDK.identify(etEmail.getText().toString(), traits, options);
                Toast.makeText(getActivity(), "Long", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        identifyPresenter.bindView(this);
        etEmail.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        identifyPresenter.unbindView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(identifyPresenter, outState);
    }

    @Override
    public void notifyNoEmail() {
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter an email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.requestFocus();
            }
        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
    }

    @Override
    public void notifyInvalidEmail() {
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid email address!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail.requestFocus();
            }
        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
    }

    @Override
    public void notifyIdentifying() {
        Toast.makeText(getActivity(), "Identifying!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    @Override
    public void share(Share share) {
        share.execute(getActivity());
    }

    @Override
    public void onActionClicked() {
        identifyPresenter.onActionClicked(etEmail.getText().toString());
    }

    @DrawableRes
    @Override
    public int getActionDrawable() {
        return R.drawable.ic_share_white;
    }

    @Override
    public boolean getActionVisibility() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Identify";
    }

}
