package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.Secrets;
import com.ambassador.ambassadorsdk.internal.activities.ambassador.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.oauth.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.activities.survey.SurveyModel;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.conversion.ConversionParametersFactory;
import com.ambassador.ambassadorsdk.internal.conversion.ConversionStatusListener;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.factories.RAFOptionsFactory;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.kencochrane.raven.DefaultRavenFactory;

import java.io.InputStream;

import javax.inject.Inject;

public final class AmbassadorSDK {
    @Inject protected User user;
    @Inject protected Campaign campaign;
    @Inject protected PusherManager pusherManager;
    @Inject protected RequestManager requestManager;
    @Inject protected SocialOAuthActivity socialOAuthActivity;
    @Inject protected RAFOptions RAFOptions;

    protected AmbSingleton AmbSingleton;
    private static AmbassadorSDK instance;

    public static AmbassadorSDK getInstance() {
        return getInstance(null);
    }

    public static AmbassadorSDK getInstance(@Nullable Context context) {
        if (instance == null) {
            instance = new AmbassadorSDK(context);
        }

        return instance;
    }

    private AmbassadorSDK(@Nullable Context context) {
        if (context != null) {
            AmbSingleton.getInstance().setContext(context);
        }

        AmbSingleton.getInstance().buildDaggerComponent();
        AmbSingleton.getInstance().getAmbComponent().inject(this);
    }

    /**
     *
     * @param sdkToken
     * @param universalId
     */
    public void runWithKeys(String sdkToken, String universalId) {
        Auth auth = AmbSingleton.getInstance().getAmbComponent().provideAuth();
        auth.clear();
        auth.setSdkToken(sdkToken);
        auth.setUniversalId(universalId);

        new InstallReceiver().registerWith(AmbSingleton.getInstance().getContext());

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (ex instanceof Exception && BuildConfig.IS_RELEASE_BUILD) {
                    Exception exception = (Exception) ex;
                    for (StackTraceElement element : exception.getStackTrace()) {
                        element.getClassName();
                        if (element.getClassName().contains("com.ambassador")) {
                            DefaultRavenFactory.ravenInstance(Secrets.getSentryUrl())
                                    .sendException((Exception) ex);
                            Log.v("amb", "Sending exception to Sentry:");
                            Log.v("amb", ex.toString());
                            break;
                        }
                    }
                }

                defaultHandler.uncaughtException(thread, ex);
            }
        });

        SharedPreferences sharedPreferences = AmbSingleton.getInstance().getContext().getSharedPreferences("conversions", Context.MODE_PRIVATE);
        String content = sharedPreferences.getString("conversions", "[]");
        sharedPreferences.edit().putString("conversions", "[]").apply();
        final JsonArray conversions = new JsonParser().parse(content).getAsJsonArray();
        for (final JsonElement jsonElement : conversions) {
            AmbConversion ambConversion = new Gson().fromJson(jsonElement, AmbConversion.class);
            ambConversion.execute();
        }
    }

    /**
     * Identifies a user to the Ambassador SDK using a unique identifier and other optional information.
     * @param userId unique identifier for the user.
     * @param traits Bundle for other relevant identification properties.
     * @param options Bundle for other information like "campaign".
     */
    public void identify(String userId, Bundle traits, Bundle options) {
        AmbassadorIdentification ambassadorIdentification = new AmbassadorIdentification();

        if (traits != null) {
            ambassadorIdentification.setEmail(traits.getString("email", null));
            ambassadorIdentification.setFirstName(traits.getString("firstName", null));
            ambassadorIdentification.setLastName(traits.getString("lastName", null));
            ambassadorIdentification.setCompany(traits.getString("company", null));
            ambassadorIdentification.setPhone(traits.getString("phone", null));
            ambassadorIdentification.setCustomLabel1(traits.getString("customLabel1", null));
            ambassadorIdentification.setCustomLabel2(traits.getString("customLabel2", null));
            ambassadorIdentification.setCustomLabel3(traits.getString("customLabel3", null));
            ambassadorIdentification.setAddToGroups(traits.getString("addToGroups", null));
            ambassadorIdentification.setSandbox(traits.getBoolean("sandbox", false));

            Bundle address = traits.getBundle("address");
            if (address != null) {
                ambassadorIdentification.setStreet(traits.getString("street", null));
                ambassadorIdentification.setCity(traits.getString("city", null));
                ambassadorIdentification.setState(traits.getString("state", null));
                ambassadorIdentification.setPostalCode(traits.getString("postalCode", null));
                ambassadorIdentification.setCountry(traits.getString("country", null));
            }
        }

        if (options != null) {
            Object campaignIdObj = options.get("campaign");
            String campaignIdStr = campaignIdObj != null ? campaignIdObj.toString() : null;
            campaign.setId(campaignIdStr);
        }

        if (ambassadorIdentification.getEmail() == null && new Identify(userId).isValidEmail()) {
            ambassadorIdentification.setEmail(userId);
        }

        if (ambassadorIdentification.getEmail() == null || !(new Identify(ambassadorIdentification.getEmail()).isValidEmail())) {
            AmbIdentify.identifyType = "";
            return;
        }

        AmbIdentify.get(userId, ambassadorIdentification).execute();
    }

    /**
     * @param emailAddress a valid String email address.
     * @return boolean determining validity of the email passed.
     */
    public boolean identify(String emailAddress) {
        if (!new Identify(emailAddress).isValidEmail()) {
            AmbIdentify.identifyType = "";
            return false;
        }

        identify(emailAddress, null, null);
        return true;
    }

    /**
     * Unidentifies a user to the Ambassador SDK. Equivalent to a logout.
     * Clears cookies so webview OAuth won't re-auth automatically.
     */
    public void unidentify() {
        user.clear();
        user.setUserId(null);
        socialOAuthActivity.clearCookies();
    }

    /**
     * Registers a conversion to Ambassador.
     * @param conversionParameters object defining information about the conversion.
     * @param limitOnce boolean determining if this conversion should ever be allowed to happen more than once.
     * @param conversionStatusListener callback interface that will return status of the conversion request.
     */
    public void registerConversion(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        AmbConversion ambConversion = new AmbConversion(conversionParameters, limitOnce, conversionStatusListener);
        ambConversion.execute();
    }

    /**
     * Registers a conversion to Ambassador.
     * @param conversionParameters object defining information about the conversion.
     * @param limitOnce boolean determining if this conversion should ever be allowed to happen more than once.
     * @deprecated use {@link #registerConversion(ConversionParameters, boolean, ConversionStatusListener)} instead.
     */
    @Deprecated
    public void registerConversion(ConversionParameters conversionParameters, Boolean limitOnce) {
        registerConversion(conversionParameters, limitOnce, null);
    }

    /**
     * Tracks an event with Ambassador.
     * Currently, the only event Ambassador tracks is a conversion.
     * @param eventName an optional value for the name of the event being tracked.
     * @param properties information pertaining to the event such as campaign, revenue, etc.
     * @param options additional information that can be added to the event.
     */
    public void trackEvent(String eventName, Bundle properties, Bundle options) {
        trackEvent(eventName, properties, options, null);
    }

    /**
     * Tracks an event with Ambassador.
     * Currently, the only event Ambassador tracks is a conversion.
     * @param eventName an optional value for the name of the event being tracked.
     * @param properties information pertaining to the event such as campaign, revenue, etc.
     * @param options additional information that can be added to the event.
     * @param listener a callback interface that will be used if this event is a conversion.
     */
    public void trackEvent(String eventName, Bundle properties, Bundle options, ConversionStatusListener listener) {
        if (options.getBoolean("conversion", false)) {
            ConversionParameters conversionParameters = ConversionParametersFactory.getFromProperties(properties);
            boolean limitOnce = options.getBoolean("restrictedToInstall", false);
            registerConversion(conversionParameters, limitOnce, listener);
        }
    }

    public void presentRAF(Context context, String campaignID) {
        presentRAF(context, campaignID, new RAFOptions.Builder().build());
    }

    public void presentRAF(Context context, String campaignID, InputStream inputStream) {
        RAFOptions rafOptions;
        try {
            rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);
        } catch (Exception e) {
            rafOptions = new RAFOptions.Builder().build();
        }

        presentRAF(context, campaignID, rafOptions);
    }

    public void presentRAF(Context context, String campaignID, RAFOptions rafOptions) {
        RAFOptions.set(rafOptions);
        campaign.clear();
        campaign.setId(campaignID);
        Intent intent = new Intent(context, AmbassadorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
 
    public void presentRAF(Context context, String campaignID, String pathInAssets) {
        try {
            presentRAF(context, campaignID, context.getAssets().open(pathInAssets));
        } catch (Exception e) {
            presentRAF(context, campaignID);
            Log.e("AmbassadorSDK", pathInAssets + " not found.");
        }
    }

    /**
     * Registers an activity and callback to pass a WelcomeScreenDialog through, once the InstallReceiver
     * is used.
     * @param activity the Activity to launch the dialog from.
     * @param availabilityCallback the callback interface to pass the dialog through, once available.
     */
    @Deprecated
    public void presentWelcomeScreen(
            @NonNull final Activity activity,
            @NonNull final WelcomeScreenDialog.AvailabilityCallback availabilityCallback,
            @NonNull final WelcomeScreenDialog.Parameters parameters) {

        WelcomeScreenDialog.setActivity(activity);
        WelcomeScreenDialog.setAvailabilityCallback(availabilityCallback);
        WelcomeScreenDialog.setParameters(parameters);
    }

    /**
     * Sets colors for an NPS survey activated via a notification.
     * @param backgroundColor ColorInt to set as the background of the survey.
     * @param contentColor ColorInt to set on the content of the survey (text, lines, x, etc.).
     * @param buttonColor ColorInt to set as the submit button background color.
     */
    public void configureSurvey(
            @ColorInt int backgroundColor,
            @ColorInt int contentColor,
            @ColorInt int buttonColor) {

        SurveyModel.setDefaultColors(backgroundColor, contentColor, buttonColor);
    }

    public String getReferredByShortCode() {
        return campaign.getReferredByShortCode();
    }

    public String getCampaignIdFromShortCode(String shortCode) {
        return requestManager.getCampaignIdFromShortCode(shortCode);
    }
}
