package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.api.Requests;
import com.ambassador.demoapp.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.demoapp.data.User;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demoapp.dialogs.CampaignChooserDialog;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class ConversionFragment extends Fragment {

    @Bind(R.id.svConversion) protected ScrollView svConversion;

    @Bind(R.id.tvReferrerEmail) protected TextView tvReferrerEmail;
    @Bind(R.id.tvRequiredParameters) protected TextView tvRequiredParameters;

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
                if (!verifiedInputs()) return;

                final ConversionParameters parameters = getConversionParametersBasedOnInputs();
                String referrerEmail = etReferrerEmail.getText().toString();

                registerShortCode(parameters.getCampaign(), referrerEmail, new ShortCodeRegistrationListener() {
                    @Override
                    public void success() {
                        AmbassadorSDK.registerConversion(parameters, false);
                        Toast.makeText(getActivity(), "Conversion registered!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure() {
                        Toast.makeText(getActivity(), "An error occurred registering the conversion.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
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


    protected boolean verifiedInputs() {
        if (getView() == null) return false;

        if (!(new Identify(etReferrerEmail.getText().toString()).isValidEmail())) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid referrer email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, tvReferrerEmail.getTop());
                    etReferrerEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (!(new Identify(etReferredEmail.getText().toString()).isValidEmail())) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please a valid referred email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, tvRequiredParameters.getTop());
                    etReferredEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (!stringHasContent(etRevenue.getText().toString())) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a revenue amount!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, tvRequiredParameters.getTop());
                    etRevenue.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (!stringHasContent(etCampaign.getText().toString())) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a campaign ID!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, tvRequiredParameters.getTop());
                    etCampaign.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        return true;
    }

    protected boolean stringHasContent(String text) {
        return text != null && !text.isEmpty() && !text.equals("");
    }


    protected ConversionParameters getConversionParametersBasedOnInputs() {
        ConversionParameters defaults = new ConversionParameters();

        String referredEmail = new ValueOrDefault<>(etReferredEmail, defaults.email).get();
        float revenueAmount = new ValueOrDefault<>(etRevenue, defaults.revenue).getFloat();
        int campaignId = new ValueOrDefault<>(etCampaign, defaults.campaign).getInteger();

        String addToGroupId = new ValueOrDefault<>(etGroupId, defaults.addToGroupId).get();
        String firstName = new ValueOrDefault<>(etFirstName, defaults.firstName).get();
        String lastName = new ValueOrDefault<>(etLastName, defaults.lastName).get();
        String uid = new ValueOrDefault<>(etUID, defaults.uid).get();
        String custom1 = new ValueOrDefault<>(etCustom1, defaults.custom1).get();
        String custom2 = new ValueOrDefault<>(etCustom2, defaults.custom2).get();
        String custom3 = new ValueOrDefault<>(etCustom3, defaults.custom3).get();
        String transactionUID = new ValueOrDefault<>(etTransactionUID, defaults.transactionUid).get();
        String eventData1 = new ValueOrDefault<>(etEventData1, defaults.eventData1).get();
        String eventData2 = new ValueOrDefault<>(etEventData2, defaults.eventData2).get();
        String eventData3 = new ValueOrDefault<>(etEventData3, defaults.eventData3).get();

        int isApproved = swApproved.isEnabled() ? 1 : 0;
        int autoCreate = swAutoCreate.isEnabled() ? 1 : 0;
        int deactivateNewAmbassador = swDeactivateNewAmbassador.isEnabled() ? 1 : 0;
        int emailNewAmbassador = swEmailNewAmbassador.isEnabled() ? 1 : 0;

        return new ConversionParameters.Builder()
                .setEmail(referredEmail)
                .setRevenue(revenueAmount)
                .setCampaign(campaignId)
                .setAddToGroupId(addToGroupId)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setUid(uid)
                .setCustom1(custom1)
                .setCustom2(custom2)
                .setCustom3(custom3)
                .setTransactionUid(transactionUID)
                .setEventData1(eventData1)
                .setEventData2(eventData2)
                .setEventData3(eventData3)
                .setIsApproved(isApproved)
                .setAutoCreate(autoCreate)
                .setDeactivateNewAmbassador(deactivateNewAmbassador)
                .setEmailNewAmbassador(emailNewAmbassador)
                .build();
    }

    protected static class ValueOrDefault<T> {

        protected String value;
        protected T defaultValue;

        public ValueOrDefault(@NonNull EditText editText, T defaultValue) {
            this.value = editText.getText().toString();
            this.defaultValue = defaultValue;
        }

        protected T getDefault() {
            return defaultValue;
        }

        public T get() {
            if (value == null || value.isEmpty()) {
                return getDefault();
            } else {
                try {
                    return (T) value;
                } catch (Exception e) {
                    return getDefault();
                }
            }
        }

        public Float getFloat() {
            return Float.parseFloat(String.valueOf(get()));
        }

        public Integer getInteger() {
            return Integer.parseInt(String.valueOf(get()));
        }

    }

    public void registerShortCode(final int campaignId, final String referrerEmail, final ShortCodeRegistrationListener listener) {
        Requests.get().getShortCodeFromEmail(User.get().getSdkToken(), campaignId, referrerEmail, new Callback<GetShortCodeFromEmailResponse>() {
            @Override
            public void success(GetShortCodeFromEmailResponse getShortCodeFromEmailResponse, Response response) {
                if (getShortCodeFromEmailResponse.count > 0) {
                    GetShortCodeFromEmailResponse.Result result = getShortCodeFromEmailResponse.results[0];
                    String shortCode = result.short_code;

                    if (shortCode == null) {
                        failure(null);
                        return;
                    }

                    // Inject install receiver event to register short code
                    Intent data = new Intent();
                    data.putExtra("referrer", "mbsy_cookie_code=" + shortCode + "&device_id=test1234");
                    InstallReceiver.getInstance().onReceive(getActivity(), data);

                    listener.success();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                listener.failure();
            }
        });
    }

    protected interface ShortCodeRegistrationListener {
        void success();
        void failure();
    }

}
