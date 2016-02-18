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
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
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

    // region Views
    @Bind(B.id.etFirstName) protected ShakableEditText  etFirstName;
    @Bind(B.id.etLastName)  protected ShakableEditText  etLastName;
    @Bind(B.id.btnCancel)   protected Button            btnCancel;
    @Bind(B.id.btnContinue) protected Button            btnContinue;
    // endregion

    // region Dependencies
    @Inject protected RequestManager    requestManager;
    @Inject protected User              user;
    @Inject protected Device            device;
    // endregion

    // region Local members
    protected ProgressDialog pd;
    // endregion

    public AskNameDialog(Context context, ProgressDialog pd) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }

        this.pd = pd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contact_name);
        ButterFork.bind(this);
        AmbSingleton.inject(this);

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
        hide();
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
            }
        }
    }

    private void updateName(@NonNull final String firstName, @NonNull final String lastName) throws JSONException {
        JsonObject pusherData = user.getPusherInfo();
        if (pusherData == null) return;

        pusherData.remove("firstName");
        pusherData.addProperty("firstName", firstName);
        pusherData.remove("lastName");
        pusherData.addProperty("lastName", lastName);

        pd.show();
        user.setPusherInfo(pusherData);

        requestManager.updateNameRequest(pusherData.get("email").getAsString(), firstName, lastName, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                user.setFirstName(etFirstName.getText().toString());
                user.setLastName(etLastName.getText().toString());
                pd.dismiss();
                hide();
            }

            @Override
            public void onFailure(Object failureResponse) {
                Toast.makeText(getOwnerActivity(), new StringResource(R.string.update_failure).getValue(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    public void showKeyboard() {
        device.openSoftKeyboard(etFirstName);
    }

}
