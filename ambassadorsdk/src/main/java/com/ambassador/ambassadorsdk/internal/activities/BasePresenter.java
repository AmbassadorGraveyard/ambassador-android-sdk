package com.ambassador.ambassadorsdk.internal.activities;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<M, V> {

    protected M model;
    protected WeakReference<V> view;

    public void setModel(M model) {
        resetState();
        this.model = model;
        if (setupDone()) {
            updateView();
        }
    }

    protected void resetState() {
        // Do any teardown for when a new model is set.
    }

    public void bindView(@NonNull V view) {
        this.view = new WeakReference<>(view);
        if (setupDone()) {
            updateView();
        }
    }

    public void unbindView() {
        this.view = null;
    }

    protected V view() {
        return view == null ? null : view.get();
    }

    protected abstract void updateView();

    protected boolean setupDone() {
        return view() != null && model != null;
    }

}
