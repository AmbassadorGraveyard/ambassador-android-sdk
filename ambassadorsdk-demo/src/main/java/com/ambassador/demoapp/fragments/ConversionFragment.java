package com.ambassador.demoapp.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.ambassador.demoapp.CustomizationPackage;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.activities.MainActivity;
import com.ambassador.demoapp.data.User;
import com.ambassador.demoapp.utils.Share;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ConversionFragment extends Fragment implements MainActivity.TabFragment {

    @Bind(R.id.etConversionEmail) protected EditText etEmail;
    @Bind(R.id.etConversionRevenue) protected EditText etRevenue;
    @Bind(R.id.etConversionCampaign) protected EditText etCampaign;
    @Bind(R.id.swConversionApproved) protected Switch swApproved;
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
                if (!validateEditTextContainsInput(etEmail, "email") ||
                        !validateEditTextContainsInput(etRevenue, "revenue amount") ||
                            !validateEditTextContainsInput(etCampaign, "campaign ID")) {

                    return;

                }

                String email = etEmail.getText().toString();
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

    @Override
    public void onActionClicked() {
        StringBuilder readmeBuilder = new StringBuilder();
        readmeBuilder.append("AmbassadorSDK 1.1.4\n");
        readmeBuilder.append("Checkout the Application.java as an example implementation of this conversion request.\n");

        StringBuilder identifyBuilder = new StringBuilder();
        identifyBuilder.append("package com.example.example;\n\n");
        identifyBuilder.append("import android.app.Application;\n");
        identifyBuilder.append("import com.ambassador.ambassadorsdk.ConversionParameters;\n");
        identifyBuilder.append("import com.ambassador.ambassadorsdk.AmbassadorSDK;\n\n");
        identifyBuilder.append("public class MyApplication extends Application {\n\n");
        identifyBuilder.append("    @Override\n");
        identifyBuilder.append("    public void onCreate() {\n");
        identifyBuilder.append("        super.onCreate();\n");
        identifyBuilder.append(String.format("        AmbassadorSDK.runWithKeys(this, \"%s\", \"%s\");\n", User.get().getSdkToken(), User.get().getUniversalId()));
        identifyBuilder.append("        ConversionParameters conversionParameters = new ConversionParameters.Builder()\n");
        identifyBuilder.append(getConversionParametersAdditionLines());
        identifyBuilder.append("            .build();\n");
        identifyBuilder.append("        AmbassadorSDK.registerConversion(conversionParameters);\n");
        identifyBuilder.append("    }\n\n");
        identifyBuilder.append("}");

        String filename = new CustomizationPackage(getActivity())
                .add("Application.java", identifyBuilder.toString(), CustomizationPackage.Directory.FILES)
                .add("README.txt", readmeBuilder.toString(), CustomizationPackage.Directory.FILES)
                .zip();

        new Share(filename).execute(getActivity());
    }

    protected String getConversionParametersAdditionLines() {
        String tab = "            ";
        String out = "";

        out += etEmpty(etCampaign) ? "" : tab + ".setCampaign(" + etValue(etCampaign, false) + ")\n";
        out += etEmpty(etEmail) ? "" : tab + ".setEmail(" + etValue(etEmail, true) + ")\n";
//        out += et.getText().toString().isEmpty() ? "" : tab + ".setCampaign(" + conversionParameters.campaign + ")\n";
//        out += etCampaign.getText().toString().isEmpty() ? "" : tab + ".setCampaign(" + conversionParameters.campaign + ")\n";
//        out += etCampaign.getText().toString().isEmpty() ? "" : tab + ".setCampaign(" + conversionParameters.campaign + ")\n";

        return out;
    }

    protected boolean etEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    protected String etValue(EditText editText, boolean quotes) {
        return (quotes ? "\"" : "") + editText.getText().toString() + (quotes ? "\"" : "");
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
