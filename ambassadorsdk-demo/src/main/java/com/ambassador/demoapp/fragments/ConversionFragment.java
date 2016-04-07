package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demoapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ConversionFragment extends Fragment {

    @Bind(R.id.etReferrerEmail) protected EditText etReferrerEmail;

    @Bind(R.id.etConversionEmail) protected EditText etReferredEmail;
    @Bind(R.id.etConversionRevenue) protected EditText etRevenue;
    @Bind(R.id.etConversionCampaign) protected EditText etCampaign;

    @Bind(R.id.etGroupId) protected EditText etGroupId;
    @Bind(R.id.etFirstName) protected EditText etFirstName;
    @Bind(R.id.etLastName) protected EditText etLastName;
    @Bind(R.id.etUID) protected EditText etUID;
    @Bind(R.id.etCustom1) protected EditText etCustom1;
    @Bind(R.id.etCustom2) protected EditText etCustom2;
    @Bind(R.id.etCustom3) protected EditText etCustom3;
    @Bind(R.id.etTransactionUID) protected EditText etTransactionUID;
    @Bind(R.id.etEventData1) protected EditText etEventData1;
    @Bind(R.id.etEventData2) protected EditText etEventData2;
    @Bind(R.id.etEventData3) protected EditText etEventData3;

    @Bind(R.id.swConversionApproved) protected Switch swApproved;
    @Bind(R.id.swAutoCreate) protected Switch swAutoCreate;
    @Bind(R.id.swDeactivateNewAmbassador) protected Switch swDeactivateNewAmbassador;
    @Bind(R.id.swEmailNewAmbassador) protected Switch swEmailNewAmbassador;

    @Bind(R.id.btnConversion) protected Button btnConversion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversion, container, false);
        ButterKnife.bind(this, view);

        btnConversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEditTextContainsInput(etReferredEmail, "email") ||
                        !validateEditTextContainsInput(etRevenue, "revenue amount") ||
                            !validateEditTextContainsInput(etCampaign, "campaign ID")) {

                    return;

                }

                String email = etReferredEmail.getText().toString();
                if (!(new Identify(email).isValidEmail())) {
                    Toast.makeText(getActivity(), "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String revenue = etRevenue.getText().toString();

                String campaignId = etCampaign.getText().toString();
                boolean isApproved = swApproved.isActivated();

                ConversionParameters conversionParameters = new ConversionParameters.Builder()
                        .setEmail(email)
                        .setRevenue(Float.parseFloat(revenue))
                        .setCampaign(Integer.parseInt(campaignId))
                        .setIsApproved(isApproved ? 1 : 0)
                        .build();

                AmbassadorSDK.registerConversion(conversionParameters, false);
                Toast.makeText(getActivity(), "Conversion registered!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    protected boolean validateEditTextContainsInput(EditText editText, String name) {
        boolean startsWithVowel = false;
        for (String vowel : new String[]{"a", "e", "i", "o", "u"}) {
            if (name.startsWith(vowel)) {
                startsWithVowel = true;
                break;
            }
        }

        String ending = startsWithVowel ? "an" : "a";

        if (editText.getText().length() == 0) {
            Toast.makeText(getActivity(), String.format("Please enter %s %s!", ending, name), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        closeSoftKeyboard();
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

}
