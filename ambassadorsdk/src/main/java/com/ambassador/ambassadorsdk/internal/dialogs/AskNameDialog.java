package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.res.ColorResource;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.ShakableEditText;
import com.google.gson.JsonObject;

import org.json.JSONException;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog to ask the user for his/her name if the backend doesn't have it.
 */
public final class AskNameDialog extends Dialog {

    public enum DismissStatus {
        CONTINUE, CANCEL
    }

    @Bind(B.id.etFirstName) protected ShakableEditText  etFirstName;
    @Bind(B.id.etLastName)  protected ShakableEditText  etLastName;
    @Bind(B.id.btnCancel)   protected Button            btnCancel;
    @Bind(B.id.btnContinue) protected Button            btnContinue;

    @Inject protected RequestManager    requestManager;
    @Inject protected User              user;
    @Inject protected Device            device;

    public DismissStatus dismissStatus;

    public AskNameDialog(Context context, ProgressDialog pd) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contact_name);
        ButterFork.bind(this);
        AmbSingleton.getInstance().getAmbComponent().inject(this);

        setCanceledOnTouchOutside(false);

        setupTheme();
        setupButtons();
    }

    private void setupTheme() {
        etFirstName.setTint(new ColorResource(R.color.ambassador_blue).getColor());
        etLastName.setTint(new ColorResource(R.color.ambassador_blue).getColor());
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelClicked();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClicked();
            }
        });
    }

    private void cancelClicked() {
        dismissStatus = DismissStatus.CANCEL;
        dismiss();
    }

    private void continueClicked() {
        if (etFirstName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.first_name_empty).getValue(), Toast.LENGTH_SHORT).show();
            etFirstName.shake();
        } else if (etLastName.getText().toString().isEmpty()) {
            Toast.makeText(getOwnerActivity(), new StringResource(R.string.last_name_empty).getValue(), Toast.LENGTH_SHORT).show();
            etLastName.shake();
        } else {
            try {
                updateName(etFirstName.getText().toString(), etLastName.getText().toString());
            } catch (JSONException e) {
                dismiss();
                dismissStatus = DismissStatus.CONTINUE;
            }
        }
    }

    private void updateName(@NonNull final String firstName, @NonNull final String lastName) throws JSONException {
        final JsonObject pusherData = user.getPusherInfo();
        if (pusherData == null) return;

        pusherData.remove("firstName");
        pusherData.addProperty("firstName", firstName);
        pusherData.remove("lastName");
        pusherData.addProperty("lastName", lastName);

        user.setPusherInfo(pusherData);

        user.getAmbassadorIdentification().setFirstName(firstName);
        user.getAmbassadorIdentification().setLastName(lastName);

        PusherManager pusherManager = new PusherManager();
        pusherManager.addPusherListener(new PusherListenerAdapter() {
            @Override
            public void subscribed() {
                super.subscribed();
                requestManager.updateNameRequest(new PusherManager(), pusherData.get("email").getAsString(), firstName, lastName, null);
                dismiss();
            }
        });

        pusherManager.startNewChannel();
        pusherManager.subscribeChannelToAmbassador();
        hide();
    }

    public void showKeyboard() {
        device.openSoftKeyboard(etFirstName);
    }

}
