package com.ambassador.demo.activities.main.conversion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.demo.R;
import com.ambassador.demo.activities.PresenterManager;
import com.ambassador.demo.activities.main.MainActivity;
import com.ambassador.demo.dialogs.CampaignChooserDialog;
import com.ambassador.demo.dialogs.GroupChooserDialog;
import com.ambassador.demo.utils.Share;
import com.ambassador.demo.views.ExpandableLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ConversionFragment extends Fragment implements ConversionView, MainActivity.TabFragment {

    protected ConversionPresenter conversionPresenter;

    @Bind(R.id.svConversion) protected ScrollView svConversion;
    @Bind(R.id.elAmbassador) protected ExpandableLayout elAmbassador;
    @Bind(R.id.etAmbassadorEmail) protected EditText etAmbassadorEmail;
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
    @Bind(R.id.elCommission) protected ExpandableLayout elCommission;
    @Bind(R.id.rlCampaignChooser) protected RelativeLayout rlCampaignChooser;
    @Bind(R.id.tvSelectedCampaign) protected TextView tvSelectedCampaign;
    @Bind(R.id.etRevenue) protected EditText etRevenue;
    @Bind(R.id.swConversionApproved) protected SwitchCompat swApproved;
    @Bind(R.id.etTransactionUID) protected EditText etTransactionUID;
    @Bind(R.id.etEventData1) protected EditText etEventData1;
    @Bind(R.id.etEventData2) protected EditText etEventData2;
    @Bind(R.id.etEventData3) protected EditText etEventData3;
    @Bind(R.id.btnConversion) protected Button btnConversion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            conversionPresenter = new ConversionPresenter();
        } else {
            conversionPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversion, container, false);
        ButterKnife.bind(this, view);

        swApproved.setChecked(true);
        elAmbassador.setTitle("Ambassador");
        elCustomer.setTitle("Customer");
        elCommission.setTitle("Commission");

        swEnrollAsAmbassador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                conversionPresenter.onEnrollAsAmbassadorToggled(isChecked);
            }
        });

        rlCampaignChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversionPresenter.onCampaignChooserClicked();
            }
        });

        rlGroupChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversionPresenter.onGroupChooserClicked();
            }
        });

        btnConversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversionPresenter.onSubmitClicked();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        conversionPresenter.bindView(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        conversionPresenter.unbindView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(conversionPresenter, outState);
    }

    @Override
    public void toggleEnrollAsAmbassadorInputs(boolean isChecked) {
        float dp18 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getActivity().getResources().getDisplayMetrics());
        int targetValue = isChecked ? (int) (rlGroupChooser.getMeasuredHeight() + swEmailNewAmbassador.getMeasuredHeight() + dp18) : 0;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(rlEnrollSubInputs.getHeight(), targetValue);
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
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewGroup.LayoutParams layoutParams = rlEnrollSubInputs.getLayoutParams();
                if (layoutParams.height >= 10) {
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    rlEnrollSubInputs.setLayoutParams(layoutParams);
                }
            }
        });
        valueAnimator.start();
    }

    @Override
    public void getGroups(String preselected) {
        final GroupChooserDialog groupChooserDialog = new GroupChooserDialog(getActivity());
        if (preselected != null) groupChooserDialog.setSelected(preselected);
        groupChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                conversionPresenter.onGroupsResult(groupChooserDialog.getResult());
            }
        });
        groupChooserDialog.show();
    }

    @Override
    public void setGroupsText(String groupsText, boolean isHint) {
        int color = isHint ? Color.parseColor("#e6e6e6") : Color.parseColor("#333333");
        tvSelectedGroups.setText(groupsText);
        tvSelectedGroups.setTextColor(color);
    }

    @Override
    public void getCampaigns() {
        final CampaignChooserDialog campaignChooserDialog = new CampaignChooserDialog(getActivity());
        campaignChooserDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                conversionPresenter.onCampaignResult(campaignChooserDialog.getResult());
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
    public void notifyInvalidAmbassadorEmail() {
        elAmbassador.inflate();
        new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid ambassador email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svConversion.smoothScrollTo(0, elAmbassador.getTop());
                etAmbassadorEmail.requestFocus();
            }
        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
    }

    @Override
    public void notifyInvalidCustomerEmail() {
        elCustomer.inflate();
        new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please enter a valid customer email!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svConversion.smoothScrollTo(0, elCustomer.getTop());
                etCustomerEmail.requestFocus();
            }
        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
    }

    @Override
    public void notifyNoCampaign() {
        elCommission.inflate();
        new Device(getActivity()).closeSoftKeyboard(etCustomerEmail);
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Please select a campaign!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svConversion.smoothScrollTo(0, elCommission.getTop());
                rlCampaignChooser.performClick();
            }
        }).setActionTextColor(Color.parseColor("#8FD3FF")).show();
    }

    @Override
    public void notifyNoRevenue() {
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
        conversionPresenter.onActionClicked();
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
