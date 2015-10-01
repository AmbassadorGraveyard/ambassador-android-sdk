package com.example.ambassador.ambassadorsdk;

import android.os.Handler;
import android.util.Log;

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


import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by JakeDunahee on 9/29/15.
 */
public class RequestManager {
    final Handler mHandler = new Handler();

    interface RequestCompletion {
        void onSuccess(Object successResponse);
        void onFailure(Object failureResponse);
    }

    // region Helper Setup Functions
    HttpURLConnection setUpConnection(String methodType, String url, boolean useJSONForPost) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            if (methodType.equals("POST")) { connection.setDoOutput(true); }
            connection.setRequestMethod(methodType);
            if (useJSONForPost) {
                connection.setRequestProperty("Content-Type", "application/json");
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.getInstance().getUniversalID());
            connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getUniversalKey());
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
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.bulkSMSShareURL(), true);

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(BulkShareHelper.payloadObjectForSMS(BulkShareHelper.verifiedSMSList(contacts), messageToShare).toString());
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
                } catch (IOException e) {
                    e.printStackTrace();
                    completion.onFailure("Bulk SMS Share Failure due to IOExceiption - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    void bulkShareEmail(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.bulkEmailShareURL(), true);

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(BulkShareHelper.payloadObjectForEmail(BulkShareHelper.verifiedEmailList(contacts), messageToShare).toString());
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
                } catch (IOException e) {
                    e.printStackTrace();
                    completion.onFailure("Bulk Share Email Failure due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    void bulkShareTrack(final List<ContactObject> contacts, final boolean isSMS) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.shareTrackURL(), true);

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    if (isSMS) {
                        oStream.writeBytes(BulkShareHelper.contactArray(BulkShareHelper.verifiedSMSList(contacts), isSMS).toString());
                    } else {
                        oStream.writeBytes(BulkShareHelper.contactArray(BulkShareHelper.verifiedEmailList(contacts), isSMS).toString());
                    }
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    String response = getResponse(connection, responseCode);
                    Utilities.debugLog("BulkShare", "BULK SHARE TRACK Response Code = " + responseCode + " and Response = " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                    Utilities.debugLog("BulkShare", "BULK SHARE TRACK Failure due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }
    // endregion BULK SHARE

    // region CONVERSIONS
    void registerConversionRequest(final ConversionParameters conversionParameters, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.conversionURL(), true);

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(ConversionUtility.createJSONConversion(conversionParameters).toString());
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

                } catch (IOException e) {
                    e.printStackTrace();
                    completion.onFailure("REGISTER CONVERSION Failure due to IOException - " + e.getMessage());
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
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.identifyURL() + AmbassadorSingleton.getInstance().getUniversalID(), true);

                JSONObject identifyObject = new JSONObject();

                try {
                    JSONObject augurObject = new JSONObject(AmbassadorSingleton.getInstance().getIdentifyObject());
                    identifyObject.put("enroll", true);
                    identifyObject.put("campaign_id", AmbassadorSingleton.getInstance().getCampaignID());
                    identifyObject.put("email", AmbassadorSingleton.getInstance().getUserEmail());
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
                    Utilities.debugLog("Identify", "IDENTIFY CALL TO BACKEND Response Code = " + responseCode +
                            " and Response = " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                    Utilities.debugLog("Identify", "IDENTIFY CALL TO BACKEND Failure due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    void externalPusherRequest(final String url, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("GET", url, true);

                try {
                    final int responseCode = connection.getResponseCode();
                    String response = getResponse(connection, responseCode);
                    Utilities.debugLog("Pusher", "EXTERNAL PUSHER CALL Response Code = " + responseCode +
                                                                " and Response = " + response);

                    if (Utilities.isSuccessfulResponseCode(responseCode)) {
                        completion.onSuccess(response);
                    } else {
                        completion.onFailure(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    completion.onFailure("External Pusher Request failure due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }
    // endregion IDENTIFY REQUESTS

    // region TWITTER REQUESTS
    void twitterLoginRequest(final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);
                RequestToken requestToken = null;
                int responseCode;

                try {
                    requestToken = twitter.getOAuthRequestToken(AmbassadorSingleton.CALLBACK_URL);
                    Utilities.debugLog("Twitter", "TWITTER LOGIN Request Token Successfully created!");
                    responseCode = 200;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    Utilities.debugLog("Twitter", "TWITTER LOGIN Request Token Failure due to TwitterException - " + e.getMessage());
                    responseCode = 400;
                }

                final int finalResponseCode = responseCode;
                final RequestToken finalRequestToken = requestToken;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Utilities.isSuccessfulResponseCode(finalResponseCode)) {
                            completion.onSuccess(finalRequestToken);
                        } else {
                            completion.onFailure("TWITTER LOGIN Request Token Failed with exception.");
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    void twitterAccessTokenRequest(final String oauthSecret, final RequestToken requestToken, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);

                int responseCode;

                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauthSecret);
                    AmbassadorSingleton.getInstance().setTwitterAccessToken(accessToken.getToken());
                    AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret(accessToken.getTokenSecret());
                    responseCode = 200;
                } catch (twitter4j.TwitterException e) {
                    e.printStackTrace();
                    responseCode = 400;
                }

                final int finalResponseCode = responseCode;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Utilities.isSuccessfulResponseCode(finalResponseCode)) {
                            completion.onSuccess("Successfully logged into Twitter!");
                        } else {
                            completion.onFailure("Failed to log into Twitter!");
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();

    }

    void sendTweet(final String tweetString, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AccessToken accessToken = new AccessToken(AmbassadorSingleton.getInstance().getTwitterAccessToken(),
                        AmbassadorSingleton.getInstance().getTwitterAccessTokenSecret());
                final Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);
                twitter.setOAuthAccessToken(accessToken);

                int responseCode;

                try {
                    twitter.updateStatus(tweetString);
                    responseCode = 200;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    Utilities.debugLog("Twitter", "Failed to send Tweet due to TwitterException - " + e.getMessage());
                    responseCode = 400;
                }

                final int finalResponseCode = responseCode;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                       if (Utilities.isSuccessfulResponseCode(finalResponseCode)) {
                           completion.onSuccess("Successfully Posted to Twitter");
                       } else {
                           completion.onFailure("Failure Postring to Twitter");
                       }
                    }
                });
            }
        };
        new Thread(runnable).start();
    }
    // endregion TWITTER REQUESTS

    // region LINKEDIN REQUESTS
    void linkedInLoginRequest(final String code, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", "https://www.linkedin.com/uas/oauth2/accessToken", false);
                String charset = "UTF-8";
                String urlParams = null;

                // Create params to send for Access Token
                try {
                    urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(code, charset) +
                            "&redirect_uri=" + URLEncoder.encode(AmbassadorSingleton.CALLBACK_URL, charset) +
                            "&client_id=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CLIENT_ID, charset) +
                            "&client_secret=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CLIENT_SECRET, charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Utilities.debugLog("LinkedIn", "LinkedIn Login Request failed due to UnsupportedEncodingException -" + e.getMessage());
                }

                try {
                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(urlParams);
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("LinkedIn", "LINKEDIN LOGIN ResponseCode = " + responseCode + " and Response = " + response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    String accessToken = json.getString("access_token");
                                    AmbassadorSingleton.getInstance().setLinkedInToken(accessToken);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    completion.onFailure("Failure with JSONException - " + e.getMessage());
                                }

                                completion.onSuccess("Success logging into LinkedIn");
                            } else {
                                completion.onFailure("Failure logging into LinkedIn");
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    completion.onFailure("Failure with IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    void postToLinkedIn(final JSONObject objectToPost, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url  = "https://api.linkedin.com/v1/people/~/shares?format=json";

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Host", "api.linkedin.com");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + AmbassadorSingleton.getInstance().getLinkedInToken());
                    connection.setRequestProperty("x-li-format", "json");

                    DataOutputStream oStream = new DataOutputStream(connection.getOutputStream());
                    oStream.writeBytes(objectToPost.toString());
                    oStream.flush();
                    oStream.close();

                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("LinkedIn", "LINKEDIN POST ResponseCode = " + responseCode + " and Response = " + response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (Utilities.isSuccessfulResponseCode(responseCode)) {
                                completion.onSuccess("Success!");
                            } else {
                                completion.onFailure("Failure");
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    completion.onFailure("Linkedin Post FAILED due to IOException - " + e.getMessage());
                }

            }
        };
        new Thread(runnable).start();
    }
    // region LINKEDIN REQUESTS
}
