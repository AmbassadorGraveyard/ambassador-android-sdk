package com.ambassador.app.activities.main.identify;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.app.R;
import com.ambassador.app.activities.PresenterManager;
import com.ambassador.app.activities.main.MainActivity;
import com.ambassador.app.dialogs.CampaignChooserDialog;
import com.ambassador.app.utils.Share;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class IdentifyFragment extends Fragment implements IdentifyView, MainActivity.TabFragment {

    protected IdentifyPresenter identifyPresenter;

    @Bind(R.id.etIdentify) protected EditText etEmail;
    @Bind(R.id.etUserId) protected EditText etUserId;
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
    @Bind(R.id.tvSelectedCampaign) protected TextView tvSelectedCampaign;
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

        swAutoEnroll.setChecked(false);
        rlCampaignChooser.setAlpha(0.4f);

        swAutoEnroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rlCampaignChooser.setAlpha(1f);
                } else {
                    rlCampaignChooser.setAlpha(0.4f);
                }
            }
        });

        rlCampaignChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swAutoEnroll.isChecked()) {
                    identifyPresenter.onCampaignChooserClicked();
                } else {
                    Toast.makeText(getActivity(), "Auto enroll must be enabled!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle traits = new Bundle();
                traits.putString("email", etEmail.getText().toString());
                traits.putString("firstName", etFirstName.getText().toString());
                traits.putString("lastName", etLastName.getText().toString());
                traits.putString("company", etCompany.getText().toString());
                traits.putString("phone", etPhone.getText().toString());

                Bundle address = new Bundle();
                address.putString("street", etStreet.getText().toString());
                address.putString("city", etCity.getText().toString());
                address.putString("state", etState.getText().toString());
                address.putString("postalCode", etPostalCode.getText().toString());
                address.putString("country", etCountry.getText().toString());

                traits.putBundle("address", address);

                Bundle options = swAutoEnroll.isChecked() ? new Bundle() : null;

                identifyPresenter.onSubmitClicked(etUserId.getText().toString(), traits, options);
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
    public void getCampaigns() {
        final CampaignChooserDialog campaignChooserDialog = new CampaignChooserDialog(getActivity());
        campaignChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String result = campaignChooserDialog.getResult();
                identifyPresenter.onCampaignResult(result);
            }
        });
        campaignChooserDialog.show();
    }

    @Override
    public void setCampaignText(String campaignText) {
        tvSelectedCampaign.setText(campaignText);
        tvSelectedCampaign.setTextColor(Color.parseColor("#333333"));
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
        Bundle traits = new Bundle();
        traits.putString("email", etEmail.getText().toString());
        traits.putString("firstName", etFirstName.getText().toString());
        traits.putString("lastName", etLastName.getText().toString());
        traits.putString("company", etCompany.getText().toString());
        traits.putString("phone", etPhone.getText().toString());

        Bundle address = new Bundle();
        address.putString("street", etStreet.getText().toString());
        address.putString("city", etCity.getText().toString());
        address.putString("state", etState.getText().toString());
        address.putString("postalCode", etPostalCode.getText().toString());
        address.putString("country", etCountry.getText().toString());

        traits.putBundle("address", address);

        Bundle options = swAutoEnroll.isChecked() ? new Bundle() : null;

        identifyPresenter.onActionClicked(etEmail.getText().toString(), traits, options);
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
