package com.ambassador.demo.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demo.BuildConfig;
import com.ambassador.demo.exports.Zipper;
import com.ambassador.demo.R;
import com.ambassador.demo.activities.MainActivity;
import com.ambassador.demo.data.User;
import com.ambassador.demo.utils.Share;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class IdentifyFragment extends Fragment implements MainActivity.TabFragment {

    @Bind(R.id.etIdentify)  protected EditText  etEmail;
    @Bind(R.id.btnIdentify) protected Button    btnIdentify;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_identify, container, false);
        ButterKnife.bind(this, view);

        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().length() != 0) {
                    String email = etEmail.getText().toString();
                    if (!(new Identify(email).isValidEmail())) {
                        new Device(getActivity()).closeSoftKeyboard(etEmail);
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid email address!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                etEmail.requestFocus();
                            }
                        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
                        return;
                    }
                    Toast.makeText(getActivity(), "Identifying!", Toast.LENGTH_LONG).show();
                    AmbassadorSDK.identify(email);
                    closeSoftKeyboard();
                } else {
                    new Device(getActivity()).closeSoftKeyboard(etEmail);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter an email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etEmail.requestFocus();
                        }
                    }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
                }
            }
        });

        view.findViewById(R.id.ivFlags).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Version Code: " + BuildConfig.VERSION_CODE, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        etEmail.requestFocus();
        closeSoftKeyboard();
    }

    private void closeSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

    @Override
    public void onActionClicked() {
        if (etEmail == null || !new Identify(etEmail.getText().toString()).isValidEmail()) {
            new Device(getActivity()).closeSoftKeyboard(etEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid email address!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return;
        }

        StringBuilder readmeBuilder = new StringBuilder();
        readmeBuilder.append("AmbassadorSDK 1.1.4\n");
        readmeBuilder.append("Take a look at the android docs for an in-depth explanation on installing and integrating the SDK:\nhttps://docs.getambassador.com/v2.0.0/page/android-sdk\n\n");
        readmeBuilder.append("Checkout the MyApplication.java file as an example implementation of this identify request.\n");

        StringBuilder identifyBuilder = new StringBuilder();
        identifyBuilder.append("package com.example.example;\n\n");
        identifyBuilder.append("import android.app.Application;\n");
        identifyBuilder.append("import com.ambassador.ambassadorsdk.AmbassadorSDK;\n\n");
        identifyBuilder.append("public class MyApplication extends Application {\n\n");
        identifyBuilder.append("    @Override\n");
        identifyBuilder.append("    public void onCreate() {\n");
        identifyBuilder.append("        super.onCreate();\n");
        identifyBuilder.append(String.format("        AmbassadorSDK.runWithKeys(this, \"SDKToken %s\", \"%s\");\n", User.get().getSdkToken(), User.get().getUniversalId()));
        identifyBuilder.append(String.format("        AmbassadorSDK.identify(\"%s\");w\n", etEmail.getText().toString()));
        identifyBuilder.append("    }\n\n");
        identifyBuilder.append("}");

        String filename = new Zipper(getActivity())
                .add("MyApplication.java", identifyBuilder.toString())
                .add("README.txt", readmeBuilder.toString())
                .zip("android-identify.zip");

        new Share(filename).execute(getActivity());
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
