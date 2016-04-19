package com.ambassador.demo.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demo.R;
import com.ambassador.demo.activities.MainActivity;
import com.ambassador.demo.api.Requests;
import com.ambassador.demo.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.demo.data.User;
import com.ambassador.demo.dialogs.CampaignChooserDialog;
import com.ambassador.demo.dialogs.GroupChooserDialog;
import com.ambassador.demo.exports.ConversionExport;
import com.ambassador.demo.exports.Export;
import com.ambassador.demo.utils.Share;
import com.ambassador.demo.views.ExpandableLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class ConversionFragment extends Fragment implements MainActivity.TabFragment {

    /** ScrollView encapsulating all views. */
    @Bind(R.id.svConversion) protected ScrollView svConversion;

    // Ambassador section views
    @Bind(R.id.elAmbassador) protected ExpandableLayout elAmbassador;
    @Bind(R.id.etAmbassadorEmail) protected EditText etAmbassadorEmail;

    // Customer section views
    @Bind(R.id.elCustomer) protected ExpandableLayout elCustomer;
    @Bind(R.id.etCustomerEmail) protected EditText etCustomerEmail;
    @Bind(R.id.etFirstName) protected EditText etFirstName;
    @Bind(R.id.etLastName) protected EditText etLastName;
    @Bind(R.id.etUID) protected EditText etUID;
    @Bind(R.id.swEnrollAsAmbassador) protected SwitchCompat swEnrollAsAmbassador;
    @Bind(R.id.rlEnrollSubInputs) protected RelativeLayout rlEnrollSubInputs;
    @Bind(R.id.rlGroupChooser) protected RelativeLayout rlGroupChooser;
    @Bind(R.id.tvSelectedGroups) protected TextView tvSelectedGroups;
    @Bind(R.id.swEmailNewAmbassador) protected SwitchCompat swEmailNewAmbassador;
    @Bind(R.id.etCustom1) protected EditText etCustom1;
    @Bind(R.id.etCustom2) protected EditText etCustom2;
    @Bind(R.id.etCustom3) protected EditText etCustom3;

    // Commission section views
    @Bind(R.id.elCommission) protected ExpandableLayout elCommission;
    @Bind(R.id.rlCampaignChooser) protected RelativeLayout rlCampaignChooser;
    @Bind(R.id.tvSelectedCampaign) protected TextView tvSelectedCampaign;
    @Bind(R.id.etRevenue) protected EditText etRevenue;
    @Bind(R.id.swConversionApproved) protected SwitchCompat swApproved;
    @Bind(R.id.etTransactionUID) protected EditText etTransactionUID;
    @Bind(R.id.etEventData1) protected EditText etEventData1;
    @Bind(R.id.etEventData2) protected EditText etEventData2;
    @Bind(R.id.etEventData3) protected EditText etEventData3;

    // Conversion buttons
    @Bind(R.id.btnConversion) protected Button btnConversion;

    /** Name of the selected campaign. */
    protected String selectedCampaignName;

    /** Ambassador backend ID for selected campaign. */
    protected int selectedCampaignId;

    /** String of selected groups separated by commas. */
    protected String selectedGroups;

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

        swEnrollAsAmbassador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                float dp18 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getActivity().getResources().getDisplayMetrics());
                int finalHeight = (int) (rlGroupChooser.getMeasuredHeight() + swEmailNewAmbassador.getMeasuredHeight() + dp18);
                ValueAnimator valueAnimator;
                if (isChecked) {
                    valueAnimator = ValueAnimator.ofInt(rlEnrollSubInputs.getHeight(), finalHeight);
                } else {
                    valueAnimator = ValueAnimator.ofInt(rlEnrollSubInputs.getHeight(), 0);
                }
                valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
                valueAnimator.setDuration(500);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = rlEnrollSubInputs.getLayoutParams();
                        layoutParams.height = val;
                        rlEnrollSubInputs.setLayoutParams(layoutParams);
                    }
                });
                valueAnimator.start();
            }
        });

        rlCampaignChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CampaignChooserDialog campaignChooserDialog = new CampaignChooserDialog(getActivity());
                campaignChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (campaignChooserDialog.getResult() == null) return;
                        JsonObject json = new JsonParser().parse(campaignChooserDialog.getResult()).getAsJsonObject();
                        String name = json.get("name").getAsString();
                        int id = json.get("id").getAsInt();
                        setCampaign(name, id);
                    }
                });
                campaignChooserDialog.show();
            }
        });

        rlGroupChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final GroupChooserDialog groupChooserDialog = new GroupChooserDialog(getActivity());
                groupChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String data = groupChooserDialog.getResult();
                        setGroups(data);
                    }
                });
                groupChooserDialog.show();
            }
        });

        btnConversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verifiedInputs()) return;

                new IdentifyAugurSDK().getIdentity();

                final ConversionParameters parameters = getConversionParametersBasedOnInputs();
                String referrerEmail = etAmbassadorEmail.getText().toString();

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
                                new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "An ambassador could not be found for the email and campaign provided.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        elAmbassador.setTitle("Ambassador");
        elCustomer.setTitle("Customer");
        elCommission.setTitle("Commission");

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

    public void setCampaign(String name, int id) {
        selectedCampaignName = name;
        selectedCampaignId = id;
        tvSelectedCampaign.setText(name);
        tvSelectedCampaign.setTextColor(Color.parseColor("#333333"));
    }

    public void setGroups(String groups) {
        if (groups == null) {
            return;
        } else if (groups.equals("")) {
            selectedGroups = null;
            tvSelectedGroups.setText("Select groups");
            tvSelectedGroups.setTextColor(Color.parseColor("#e6e6e6"));
        } else {
            selectedGroups = groups;
            tvSelectedGroups.setText(groups);
            tvSelectedGroups.setTextColor(Color.parseColor("#333333"));
        }
    }

    protected boolean verifiedInputs() {
        return verifiedInputs(true);
    }

    protected boolean verifiedInputs(boolean includeReferrer) {
        if (getView() == null) return false;

        if (!(new Identify(etAmbassadorEmail.getText().toString()).isValidEmail()) && includeReferrer) {
            elAmbassador.inflate();
            new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid referrer email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, elAmbassador.getTop());
                    etAmbassadorEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (!(new Identify(etCustomerEmail.getText().toString()).isValidEmail())) {
            elCustomer.inflate();
            new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid referred email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, elCustomer.getTop());
                    etCustomerEmail.requestFocus();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (selectedCampaignName == null) {
            elCommission.inflate();
            new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a campaign ID!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svConversion.smoothScrollTo(0, elCommission.getTop());
                    rlCampaignChooser.performClick();
                }
            }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
            return false;
        }

        if (!stringHasContent(etRevenue.getText().toString())) {
            elCommission.inflate();
            new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a revenue amount!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    elCommission.inflate();
                    svConversion.smoothScrollTo(0, elCommission.getTop());
                    etRevenue.requestFocus();
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

        String referredEmail = new ValueOrDefault<>(etCustomerEmail, defaults.email).get();
        float revenueAmount = new ValueOrDefault<>(etRevenue, defaults.revenue).getFloat();
        int campaignId = selectedCampaignId;

        String addToGroupId = "gid";
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
        int autoCreate = 0;
        int deactivateNewAmbassador = 0;
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

        Export<ConversionParameters> export = new ConversionExport();
        export.setModel(getConversionParametersBasedOnInputs());
        String filename = export.zip(getActivity());
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
