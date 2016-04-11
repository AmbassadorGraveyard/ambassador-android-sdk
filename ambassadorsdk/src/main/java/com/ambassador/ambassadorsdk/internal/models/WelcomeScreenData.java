package com.ambassador.ambassadorsdk.internal.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ambassador.ambassadorsdk.WelcomeScreenDialog;

import java.util.Arrays;

/**
 * Represents data needed to properly show a WelcomeScreenDialog.
 */
public class WelcomeScreenData {

    /** Text to display on the top bar where the "Close" button is. */
    protected String topBarText;

    /** URL to the image to display as the avatar. */
    protected String imageUrl;

    /** Name to place wherever a {{ name }} id is placed. */
    protected String name;

    /** Large text to display below picture. */
    protected String title;

    /** Smaller text to display below button. */
    protected String message;

    /** Text for the main button to have. */
    protected String buttonText;

    /** Action for the main button. */
    protected View.OnClickListener buttonOnClickListener;

    /** Text for the bottom left button. */
    protected String link1Text;

    /** Action for the bottom left button. */
    protected View.OnClickListener link1OnClickListener;

    /** Text for the bottom right button. */
    protected String link2Text;

    /** Action for the bottom right button. */
    protected View.OnClickListener link2OnClickListener;

    /** Color to theme the welcome screen with. */
    protected int colorTheme;

    public WelcomeScreenData() {

    }

    public String getTopBarText() {
        return topBarText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getButtonText() {
        return buttonText;
    }

    public View.OnClickListener getButtonOnClickListener() {
        return buttonOnClickListener;
    }

    public String getLink1Text() {
        return link1Text;
    }

    public View.OnClickListener getLink1OnClickListener() {
        return link1OnClickListener;
    }

    public String getLink2Text() {
        return link2Text;
    }

    public View.OnClickListener getLink2OnClickListener() {
        return link2OnClickListener;
    }

    public int getColorTheme() {
        return colorTheme;
    }

    public static class Builder {

        protected WelcomeScreenData welcomeScreenData;

        public Builder() {
            welcomeScreenData = new WelcomeScreenData();
        }

        public Builder setTopBarText(String topBarText) {
            welcomeScreenData.topBarText = topBarText;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            welcomeScreenData.imageUrl = imageUrl;
            return this;
        }

        public Builder setName(String name) {
            welcomeScreenData.name = name;
            return this;
        }

        public Builder setTitle(String title) {
            welcomeScreenData.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            welcomeScreenData.message = message;
            return this;
        }

        public Builder setButtonText(String buttonText) {
            welcomeScreenData.buttonText = buttonText;
            return this;
        }

        public Builder setButtonOnClickListener(View.OnClickListener buttonOnClickListener) {
            welcomeScreenData.buttonOnClickListener = buttonOnClickListener;
            return this;
        }

        public Builder setLink1Text(String link1Text) {
            welcomeScreenData.link1Text = link1Text;
            return this;
        }

        public Builder setLink1OnClickListener(View.OnClickListener link1OnClickListener) {
            welcomeScreenData.link1OnClickListener = link1OnClickListener;
            return this;
        }

        public Builder setLink2Text(String link2Text) {
            welcomeScreenData.link2Text = link2Text;
            return this;
        }

        public Builder setLink2OnClickListener(View.OnClickListener link2OnClickListener) {
            welcomeScreenData.link2OnClickListener = link2OnClickListener;
            return this;
        }

        public Builder setColorTheme(int colorTheme) {
            welcomeScreenData.colorTheme = colorTheme;
            return this;
        }

        public WelcomeScreenData build() {
            return welcomeScreenData;
        }

    }

    /**
     * Appends information from WelcomeScreenDialog.Parameters to WelcomeScreenData, and returns the
     * WelcomeScreenData object.
     * @param parameters the Parameters object with info to append.
     * @return WelcomeScreenData with info from parameters appended.
     */
    @NonNull
    public WelcomeScreenData withParameters(@Nullable WelcomeScreenDialog.Parameters parameters) {
        if (parameters == null) return this;

        buttonOnClickListener = parameters.getButtonOnClickListener();
        link1OnClickListener = parameters.getLink1OnClickListener();
        link2OnClickListener = parameters.getLink2OnClickListener();
        topBarText = parameters.getTopBarText();
        title = parameters.getTitleText();
        message = parameters.getMessageText();
        buttonText = parameters.getButtonText();
        link1Text = parameters.getLink1Text();
        link2Text = parameters.getLink2Text();
        colorTheme = parameters.getColorTheme();

        return this;
    }

    /**
     * Appends information from WelcomeScreenDialog.BackendData to WelcomeScreenData, and returns the
     * WelcomeScreenData object.
     * @param backendData the BackendData object with info to append.
     * @return WelcomeScreenData with info from backendData appended.
     */
    @NonNull
    public WelcomeScreenData withBackendData(@Nullable WelcomeScreenDialog.BackendData backendData) {
        if (backendData == null) return this;

        imageUrl = backendData.getImageUrl();
        name = backendData.getName();

        return this;
    }

    /**
     * Converts instances of {{ name }} in strings to the stored name.
     * @return WelcomeScreenData with converted strings.
     */
    @NonNull
    public WelcomeScreenData parseName() {
        if (name == null) return this;

        topBarText = replaceNameVars(topBarText);
        title = replaceNameVars(title);
        message = replaceNameVars(message);
        buttonText = replaceNameVars(buttonText);
        link1Text = replaceNameVars(link1Text);
        link2Text = replaceNameVars(link2Text);

        return this;
    }

    /**
     * Replaces all occurrences of "{{ name }}" in the passed in text with the stored name, and
     * returns it.
     * @param text the text to replace name vars in.
     * @return new String with all occurrences replaced.
     */
    @NonNull
    protected String replaceNameVars(@NonNull String text) {
        String ret = text;
        while (ret.contains("{{ name }}")) {
            int index = ret.indexOf("{{ name }}");
            String addition = isMidSentence(index, ret) ? midSentenceName(name) : name;
            ret = ret.substring(0, index) + addition + ret.substring(index + "{{ name }}".length());
        }
        return ret;
    }

    /**
     * Converts a String to be usable in the middle of a sentence. Sets beginning to lowercase.
     * @param text the text to convert.
     * @return the new String.
     */
    @NonNull
    protected String midSentenceName(@NonNull String text) {
        return text.contains(" of ") ? String.valueOf(text.charAt(0)).toLowerCase() + text.substring(1) : text;
    }

    /**
     * Using a current index and a String it will determine if the current point is mid sentence.
     * @param currentIndex the index to backtrack from.
     * @param text the text to check indices on.
     * @return true if mid sentence.
     */
    protected boolean isMidSentence(int currentIndex, @NonNull String text) {
        Character[] continueBacktrack = new Character[]{ ' ' };
        Character[] sentenceEnder = new Character[]{ '.', '?', '!' };
        for (int i = currentIndex - 1; i >= 0; i--) {
            char spot = text.charAt(i);
            if (Arrays.asList(sentenceEnder).contains(spot)) {
                return false;
            } else if (!Arrays.asList(continueBacktrack).contains(spot)) {
                return true;
            }
        }

        return false;
    }

}
