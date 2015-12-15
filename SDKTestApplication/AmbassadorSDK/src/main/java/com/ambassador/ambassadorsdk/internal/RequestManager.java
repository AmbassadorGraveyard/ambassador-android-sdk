package com.ambassador.ambassadorsdk.internal;

import android.os.Handler;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.api.linkedIn.LinkedInApi;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 9/29/15.
 */
public class RequestManager {

    final Handler mHandler = new Handler();

    @Inject
    AmbassadorConfig ambassadorConfig;

    public interface RequestCompletion {
        void onSuccess(Object successResponse);
        void onFailure(Object failureResponse);
    }

    public RequestManager() {
        AmbassadorSingleton.getComponent().inject(this);
        IdentifyApi.init();
        LinkedInApi.init();
    }

    // region Helper Setup Functions
    HttpURLConnection setUpConnection(String methodType, String url) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            if (methodType.equals("POST")) { connection.setDoOutput(true); }
            connection.setRequestMethod(methodType);
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", ambassadorConfig.getUniversalID());
            connection.setRequestProperty("Authorization", ambassadorConfig.getUniversalKey());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.toString());
        }

        return connection;
    }

    String getResponse(HttpURLConnection connection, int responseCode) {
        InputStream iStream = null;
        try {
            iStream = (Utilities.isSuccessfulResponseCode(responseCode)) ? connection.getInputStream() : connection.getErrorStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = new BufferedReader(new InputStreamReader(iStream));
        String line;
        StringBuilder response = new StringBuilder();

        try {
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
    // endregion Helper Setup Functions


    // region BULK SHARE
    void bulkShareSms(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.bulkSMSShareURL());
                connection.setRequestProperty("Content-Type", "application/json");

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(BulkShareHelper.payloadObjectForSMS(BulkShareHelper.verifiedSMSList(contacts), ambassadorConfig.getUserFullName(), messageToShare).toString());
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("BulkShare", "BULK SHARE SMS Response Code = " + responseCode + " and Response = " + response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
                                completion.onSuccess(response);
                            } else {
                                completion.onFailure(response);
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("Bulk SMS Share Failure due to IOExceiption - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    void bulkShareEmail(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.bulkEmailShareURL());
                connection.setRequestProperty("Content-Type", "application/json");

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(BulkShareHelper.payloadObjectForEmail(BulkShareHelper.verifiedEmailList(contacts),
                            ambassadorConfig.getReferrerShortCode(),
                            ambassadorConfig.getEmailSubjectLine(),
                            messageToShare).toString());
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("BulkShare", "BULK SHARE EMAIL Response Code = " + responseCode + " and Response = " + response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
                                completion.onSuccess(response);
                            } else {
                                completion.onFailure(response);
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("Bulk Share Email Failure due to IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    void bulkShareTrack(final List<ContactObject> contacts, final BulkShareHelper.SocialServiceTrackType shareType) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.shareTrackURL());
                connection.setRequestProperty("Content-Type", "application/json");

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());

                    switch (shareType) {
                        case SMS:
                            oStream.writeBytes(BulkShareHelper.contactArray(BulkShareHelper.verifiedSMSList(contacts), shareType, ambassadorConfig.getReferrerShortCode()).toString());
                            break;
                        case EMAIL:
                            oStream.writeBytes(BulkShareHelper.contactArray(BulkShareHelper.verifiedEmailList(contacts), shareType, ambassadorConfig.getReferrerShortCode()).toString());
                            break;
                        default:
                            oStream.writeBytes(BulkShareHelper.contactArray(shareType, ambassadorConfig.getReferrerShortCode()).toString());
                    }

                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    String response = getResponse(connection, responseCode);
                    Utilities.debugLog("BulkShare", "BULK SHARE TRACK for " + shareType.toString() + " Response Code = " + responseCode + " and Response = " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                    Utilities.debugLog("BulkShare", "BULK SHARE TRACK Failure for " + shareType.toString() + " due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    // Overloaded bulkShareTrack for instances where no contact list is passed
    public void bulkShareTrack(final BulkShareHelper.SocialServiceTrackType shareType) {
        bulkShareTrack(null, shareType);
    }
    // endregion BULK SHARE

    // region CONVERSIONS
    public void registerConversionRequest(final ConversionParameters conversionParameters, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.conversionURL() + ambassadorConfig.getUniversalID());
                connection.setRequestProperty("Content-Type", "application/json");

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(ConversionUtility.createJSONConversion(conversionParameters, ambassadorConfig.getIdentifyObject()).toString());
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("Conversion", "REGISTER CONVERSION Response Code = " + responseCode + " and Response = " + response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
                                completion.onSuccess(response);
                            } else {
                                completion.onFailure(response);
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("REGISTER CONVERSION Failure due to IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
    // endregion CONVERSIONS


    // region IDENTIFY REQUESTS
    public void identifyRequest() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PusherChannel.setRequestId(System.currentTimeMillis());

                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.identifyURL() + ambassadorConfig.getUniversalID());
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("X-Mbsy-Client-Session-ID", PusherChannel.getSessionId());
                connection.setRequestProperty("X-Mbsy-Client-Request-ID", String.valueOf(PusherChannel.getRequestId()));

                JSONObject identifyObject = new JSONObject();
                try {
                    JSONObject augurObject = null;
                    try {
                        augurObject = new JSONObject(ambassadorConfig.getIdentifyObject());
                    }
                    catch (NullPointerException e) {
                        Utilities.debugLog("IdentifyRequest", "augurObject NULL");
                    }
                    identifyObject.put("enroll", true);
                    identifyObject.put("campaign_id", ambassadorConfig.getCampaignID());
                    identifyObject.put("email", ambassadorConfig.getUserEmail());
                    identifyObject.put("source", "android_sdk_pilot");
                    identifyObject.put("mbsy_source", "");
                    identifyObject.put("mbsy_cookie_code", "");
                    identifyObject.put("fp", augurObject);

                    Utilities.debugLog("Identify", "Identify JSON Object = " + identifyObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(identifyObject.toString());
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    String response = getResponse(connection, responseCode);
                    Utilities.debugLog("Identify", "IDENTIFY CALL TO BACKEND Response Code = " + responseCode + " and Response = " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                    Utilities.debugLog("Identify", "IDENTIFY CALL TO BACKEND Failure due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    public void updateNameRequest(final String email, final String firstName, final String lastName, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.identifyURL() + ambassadorConfig.getUniversalID());
                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject dataObject = new JSONObject();
                JSONObject nameObject = new JSONObject();

                try {
                    dataObject.put("email", email);
                    nameObject.put("first_name", firstName);
                    nameObject.put("last_name", lastName);
                    dataObject.put("update_data", nameObject);

                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(dataObject.toString());
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("Identify", "UPDATING INFO IDENTIFY ResponseCode = " + responseCode + " and Response = " + response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
                                completion.onSuccess(response);
                            } else {
                                completion.onFailure(response);
                            }
                        }
                    });
                } catch (final JSONException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("Update call failed with JSONException - " + e.getMessage());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("Update call failed with IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    void createPusherChannel(final RequestCompletion completion) {
        IdentifyApi.createPusherChannel(completion);
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.pusherChannelNameURL());
//                connection.setRequestProperty("Content-Type", "application/json");
//
//                try {
//                    final int responseCode = connection.getResponseCode();
//                    final String response = getResponse(connection, responseCode);
//                    Utilities.debugLog("createPusherChannel", "CREATE PUSHER CHANNEL Response Code = " + responseCode + " and Response = " + response);
//
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
//                                completion.onSuccess(response);
//                            } else {
//                                completion.onFailure(response);
//                            }
//                        }
//                    });
//                } catch (final IOException e) {
//                    e.printStackTrace();
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            completion.onFailure("Create PusherSDK Channel Failure due to IOException - " + e.getMessage());
//                        }
//                    });
//                }
//            }
//        };
//        new Thread(runnable).start();
    }

    void externalPusherRequest(final String url, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("GET", url);
                connection.setRequestProperty("Content-Type", "application/json");

                try {
                    final int responseCode = connection.getResponseCode();
                    String response = getResponse(connection, responseCode);
                    Utilities.debugLog("PusherSDK", "EXTERNAL PUSHER CALL Response Code = " + responseCode +
                            " and Response = " + response);

                    if (Utilities.isSuccessfulResponseCode(responseCode)) {
                        completion.onSuccess(response);
                    } else {
                        completion.onFailure(response);
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("External PusherSDK Request failure due to IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    public void postToTwitter(final String tweetString, final RequestCompletion completion) {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(twitterSession);
        twitterApiClient.getStatusesService().update(tweetString, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                completion.onSuccess("Successfully posted to Twitter");
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException e) {
                if (e.toString().toLowerCase().contains("no authentication")) {
                    completion.onFailure("auth");
                } else {
                    completion.onFailure("Failure Postring to Twitter");
                }
            }
        });
    }

    public void linkedInLoginRequest(final String code, final RequestCompletion completion) {
        String charset = "UTF-8";
        String urlParams = null;

        try {
            urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(code, charset) +
                    "&redirect_uri=" + URLEncoder.encode(AmbassadorConfig.CALLBACK_URL, charset) +
                    "&client_id=" + URLEncoder.encode(AmbassadorConfig.LINKED_IN_CLIENT_ID, charset) +
                    "&client_secret=" + URLEncoder.encode(AmbassadorConfig.LINKED_IN_CLIENT_SECRET, charset);
        } catch (UnsupportedEncodingException e) {
            Utilities.debugLog("LinkedIn", "LinkedIn Login Request failed due to UnsupportedEncodingException -" + e.getMessage());
        }

        LinkedInApi.login(urlParams, completion, new LinkedInAuthorizedListener() {
            @Override
            public void linkedInAuthorized(String accessToken) {
                ambassadorConfig.setLinkedInToken(accessToken);
            }
        });
    }

    public interface LinkedInAuthorizedListener {
        void linkedInAuthorized(String accessToken);
    }

    public void postToLinkedIn(LinkedInApi.LinkedInPostRequest requestBody, final RequestCompletion completion) {
        LinkedInApi.post(ambassadorConfig.getLinkedInToken(), requestBody, completion);
    }

    public void getProfileLinkedIn(final RequestCompletion completion) {
        LinkedInApi.getProfile(ambassadorConfig.getLinkedInToken(), completion);
    }

}
