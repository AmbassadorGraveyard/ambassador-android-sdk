package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.Secrets;
import com.ambassador.ambassadorsdk.internal.activities.ambassador.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.survey.SurveyModel;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.conversion.ConversionStatusListener;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.factories.RAFOptionsFactory;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.utils.Identify;

import net.kencochrane.raven.DefaultRavenFactory;

import java.io.InputStream;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 *
 */
public final class AmbassadorSDK {

    @Inject protected static Auth auth;
    @Inject protected static User user;
    @Inject protected static Campaign campaign;
    @Inject protected static PusherManager pusherManager;
    @Inject protected static RequestManager requestManager;

    /**
     *
     * @param context
     * @param universalToken
     * @param universalId
     */
    public static void runWithKeys(Context context, String universalToken, String universalId) {
        AmbSingleton.init(context);

        ObjectGraph objectGraph = AmbSingleton.getGraph();
        objectGraph.injectStatics();

        auth.clear();
        auth.setUniversalToken(universalToken);
        auth.setUniversalId(universalId);

        new InstallReceiver().registerWith(context);

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

        AmbConversion.attemptExecutePending();
    }

    /**
     * Identifies a user to the Ambassador SDK using an email address.
     * @param emailAddress the email address of the user being identified.
     * @return boolean determining email address parameter validity.
     */
    public static boolean identify(String emailAddress) {
        if (!new Identify(emailAddress).isValidEmail()) {
            return false;
        }

        AmbIdentify.get(emailAddress).execute();
        AmbConversion.attemptExecutePending();
        return true;
    }

    /**
     * Registers a conversion to Ambassador.
     * @param conversionParameters object defining information about the conversion.
     * @param limitOnce boolean determining if this conversion should ever be allowed to happen more than once.
     * @param conversionStatusListener callback interface that will return status of the conversion request.
     */
    public static void registerConversion(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        AmbConversion.get(conversionParameters, limitOnce, conversionStatusListener).execute();
    }

    /**
     * @deprecated use {@link #registerConversion(ConversionParameters, boolean, ConversionStatusListener)} instead.
     * Registers a conversion to Ambassador.
     * @param conversionParameters object defining information about the conversion.
     * @param limitOnce boolean determining if this conversion should ever be allowed to happen more than once.
     */
    @Deprecated
    public static void registerConversion(ConversionParameters conversionParameters, Boolean limitOnce) {
        registerConversion(conversionParameters, limitOnce, null);
    }

    public static void presentRAF(Context context, String campaignID) {
        if (context.getResources().getIdentifier("homeWelcomeTitle", "color", context.getPackageName()) != 0) {
            try {
                presentRAF(context, campaignID, RAFOptionsFactory.decodeCustomValues(context));
                return;
            } catch (Exception e) {
                // catch all to go to defaults
            }
        }

        presentRAF(context, campaignID, new RAFOptions.Builder().build());
    }

    public static void presentRAF(Context context, String campaignID, InputStream inputStream) {
        RAFOptions rafOptions;
        try {
            rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);
        } catch (Exception e) {
            rafOptions = new RAFOptions.Builder().build();
        }

        presentRAF(context, campaignID, rafOptions);
    }

    public static void presentRAF(Context context, String campaignID, RAFOptions rafOptions) {
        RAFOptions.set(rafOptions);
        campaign.clear();
        campaign.setId(campaignID);
        Intent intent = new Intent(context, AmbassadorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
 
    public static void presentRAF(Context context, String campaignID, String pathInAssets) {
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
    public static void presentWelcomeScreen(
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
    public static void configureSurvey(
            @ColorInt int backgroundColor,
            @ColorInt int contentColor,
            @ColorInt int buttonColor) {

        SurveyModel.setDefaultColors(backgroundColor, contentColor, buttonColor);
    }

}
