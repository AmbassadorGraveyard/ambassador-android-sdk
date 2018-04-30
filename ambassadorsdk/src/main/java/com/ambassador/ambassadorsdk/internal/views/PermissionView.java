package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

public class PermissionView extends LinearLayout {

    @Bind(B.id.ivPermissionType) protected ImageView ivPermissionType;
    @Bind(B.id.tvPermissionMessage) protected TextView tvPermissionMessage;
    @Bind(B.id.btnTurnOn) protected Button btnTurnOn;

    @Inject protected RAFOptions raf;

    protected OnButtonClickListener listener;

    public PermissionView(Context context) {
        super(context);
        init();
    }

    public PermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        AmbSingleton.getInstance().getAmbComponent().inject(this);
        inflate(getContext(), R.layout.view_permission_needed_layout, this);
        ButterFork.bind(this);
        ivPermissionType.setColorFilter(raf.get().getContactsToolbarColor(), PorterDuff.Mode.SRC_IN);
        btnTurnOn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick();
                }
            }
        });
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnButtonClickListener {
        void onClick();
    }
}
