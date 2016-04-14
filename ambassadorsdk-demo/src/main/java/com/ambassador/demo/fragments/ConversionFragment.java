package com.ambassador.demo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demo.Zipper;
import com.ambassador.demo.R;
import com.ambassador.demo.activities.MainActivity;
import com.ambassador.demo.api.Requests;
import com.ambassador.demo.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.demo.data.User;
import com.ambassador.demo.utils.Share;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class ConversionFragment extends Fragment implements MainActivity.TabFragment {

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

    @Bind(R.id.swConversionApproved) protected SwitchCompat swApproved;
    @Bind(R.id.swAutoCreate) protected SwitchCompat swAutoCreate;
    @Bind(R.id.swDeactivateNewAmbassador) protected SwitchCompat swDeactivateNewAmbassador;
    @Bind(R.id.swEmailNewAmbassador) protected SwitchCompat swEmailNewAmbassador;

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

        swApproved.setChecked(true);
        swAutoCreate.setChecked(true);

        btnConversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verifiedInputs()) return;

                new IdentifyAugurSDK().getIdentity();

                final ConversionParameters parameters = getConversionParametersBasedOnInputs();
                String referrerEmail = etReferrerEmail.getText().toString();

                registerShortCode(parameters.getCampaign(), referrerEmail, new ShortCodeRegistrationListener() {
                    @Override
                    public void success() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AmbassadorSDK.registerConversion(parameters, false);
                                parameters.prettyPrint();
                                Toast.makeText(getActivity(), "Conversion registered!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void failure() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Device(getActivity()).closeSoftKeyboard(etReferredEmail);
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "An ambassador could not be found for the email and campaign provided.", Snackbar.LENGTH_LONG).show();
                            }
                        });
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
        return verifiedInputs(true);
    }

    protected boolean verifiedInputs(boolean includeReferrer) {
        if (getView() == null) return false;

        if (!(new Identify(etReferrerEmail.getText().toString()).isValidEmail()) && includeReferrer) {
            new Device(getActivity()).closeSoftKeyboard(etReferredEmail);
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
            new Device(getActivity()).closeSoftKeyboard(etReferredEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid referred email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, tvRequiredParameters.getTop());
                    etReferredEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (!stringHasContent(etRevenue.getText().toString())) {
            new Device(getActivity()).closeSoftKeyboard(etReferredEmail);
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
            new Device(getActivity()).closeSoftKeyboard(etReferredEmail);
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

        int isApproved = swApproved.isChecked() ? 1 : 0;
        int autoCreate = swAutoCreate.isChecked() ? 1 : 0;
        int deactivateNewAmbassador = swDeactivateNewAmbassador.isChecked() ? 1 : 0;
        int emailNewAmbassador = swEmailNewAmbassador.isChecked() ? 1 : 0;

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
                } else {
                    failure(null);
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

    @Override
    public void onActionClicked() {
        if (!verifiedInputs(false)) {
            return;
        }

        StringBuilder readmeBuilder = new StringBuilder();
        readmeBuilder.append("AmbassadorSDK 1.1.4\n");
        readmeBuilder.append("Take a look at the android docs for an in-depth explanation on installing and integrating the SDK:\nhttps://docs.getambassador.com/v2.0.0/page/android-sdk\n\n");
        readmeBuilder.append("Checkout the MyApplication.java file as an example implementation of this conversion request.\n");

        StringBuilder identifyBuilder = new StringBuilder();
        identifyBuilder.append("package com.example.example;\n\n");
        identifyBuilder.append("import android.app.Application;\n");
        identifyBuilder.append("import com.ambassador.ambassadorsdk.ConversionParameters;\n");
        identifyBuilder.append("import com.ambassador.ambassadorsdk.AmbassadorSDK;\n\n");
        identifyBuilder.append("public class MyApplication extends Application {\n\n");
        identifyBuilder.append("    @Override\n");
        identifyBuilder.append("    public void onCreate() {\n");
        identifyBuilder.append("        super.onCreate();\n");
        identifyBuilder.append(String.format("        AmbassadorSDK.runWithKeys(this, \"SDKToken %s\", \"%s\");\n", User.get().getSdkToken(), User.get().getUniversalId()));
        identifyBuilder.append("        ConversionParameters conversionParameters = new ConversionParameters.Builder()\n");
        identifyBuilder.append(getConversionParametersAdditionLines());
        identifyBuilder.append("            .build();\n");
        identifyBuilder.append("        AmbassadorSDK.registerConversion(conversionParameters, false);\n");
        identifyBuilder.append("    }\n\n");
        identifyBuilder.append("}");

        String filename = new Zipper(getActivity())
                .add("MyApplication.java", identifyBuilder.toString(), Zipper.Directory.FILES)
                .add("README.txt", readmeBuilder.toString(), Zipper.Directory.FILES)
                .zip("android-conversion.zip");

        new Share(filename).execute(getActivity());
    }

    protected String getConversionParametersAdditionLines() {
        String tab = "            ";
        String out = "";

        ConversionParameters parameters = getConversionParametersBasedOnInputs();

        out += tab + ".setEmail(" + etValue(parameters.email, true) + ")\n";
        out += tab + ".setRevenue(" + etValue(parameters.revenue, false) + "f)\n";
        out += tab + ".setCampaign(" + etValue(parameters.campaign, false) + ")\n";

        out += tab + ".setAddToGroupId(" + etValue(parameters.addToGroupId, true) + ")\n";
        out += tab + ".setFirstName(" + etValue(parameters.firstName, true) + ")\n";
        out += tab + ".setLastName(" + etValue(parameters.lastName, true) + ")\n";
        out += tab + ".setUID(" + etValue(parameters.uid, true) + ")\n";
        out += tab + ".setCustom1(" + etValue(parameters.custom1, true) + ")\n";
        out += tab + ".setCustom2(" + etValue(parameters.custom2, true) + ")\n";
        out += tab + ".setCustom3(" + etValue(parameters.custom3, true) + ")\n";
        out += tab + ".setTransactionUID(" + etValue(parameters.transactionUid, true) + ")\n";
        out += tab + ".setEventData1(" + etValue(parameters.eventData1, true) + ")\n";
        out += tab + ".setEventData2(" + etValue(parameters.eventData2, true) + ")\n";
        out += tab + ".setEventData3(" + etValue(parameters.eventData3, true) + ")\n";

        out += tab + String.format(".setIsApproved(%s)\n", parameters.isApproved);
        out += tab + String.format(".setAutoCreate(%s)\n", parameters.autoCreate);
        out += tab + String.format(".setDeactivateNewAmbassador(%s)\n", parameters.deactivateNewAmbassador);
        out += tab + String.format(".setEmailNewAmbassador(%s)\n", parameters.emailNewAmbassador);

        return out;
    }

    protected String etValue(String value, boolean quotes) {
        return (quotes ? "\"" : "") + value + (quotes ? "\"" : "");
    }

    protected String etValue(int value, boolean quotes) {
        return etValue(String.valueOf(value), quotes);
    }

    protected String etValue(float value, boolean quotes) {
        return etValue(String.valueOf(value), quotes);
    }

    @Override
    public Drawable getActionDrawable() {
        return ContextCompat.getDrawable(getActivity(), R.drawable.ic_share_white);
    }

    @Override
    public boolean getActionVisibility() {
        return true;
    }

}
