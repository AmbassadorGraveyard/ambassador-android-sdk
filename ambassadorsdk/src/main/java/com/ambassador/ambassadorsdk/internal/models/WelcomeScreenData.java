package com.ambassador.ambassadorsdk.internal.models;

import android.view.View;

/**
 * Represents data needed to properly show a WelcomeScreenDialog.
 */
public class WelcomeScreenData {

    protected String imageUrl;
    protected String title;
    protected String message;

    protected String buttonText;
    protected View.OnClickListener buttonOnClickListener;

    protected String link1Text;
    protected View.OnClickListener link1OnClickListener;

    protected String link2Text;
    protected View.OnClickListener link2OnClickListener;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public View.OnClickListener getButtonOnClickListener() {
        return buttonOnClickListener;
    }

    public void setButtonOnClickListener(View.OnClickListener buttonOnClickListener) {
        this.buttonOnClickListener = buttonOnClickListener;
    }

    public String getLink1Text() {
        return link1Text;
    }

    public void setLink1Text(String link1Text) {
        this.link1Text = link1Text;
    }

    public View.OnClickListener getLink1OnClickListener() {
        return link1OnClickListener;
    }

    public void setLink1OnClickListener(View.OnClickListener link1OnClickListener) {
        this.link1OnClickListener = link1OnClickListener;
    }

    public String getLink2Text() {
        return link2Text;
    }

    public void setLink2Text(String link2Text) {
        this.link2Text = link2Text;
    }

    public View.OnClickListener getLink2OnClickListener() {
        return link2OnClickListener;
    }

    public void setLink2OnClickListener(View.OnClickListener link2OnClickListener) {
        this.link2OnClickListener = link2OnClickListener;
    }

}
