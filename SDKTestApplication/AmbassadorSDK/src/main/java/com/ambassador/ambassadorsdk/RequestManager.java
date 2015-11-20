package com.ambassador.ambassadorsdk;

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

import javax.inject.Inject;

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

    @Inject
    AmbassadorConfig ambassadorConfig;

    interface RequestCompletion {
        void onSuccess(Object successResponse);
        void onFailure(Object failureResponse);
    }

    public RequestManager() {
        AmbassadorSingleton.getComponent().inject(this);
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
                    oStream.writeBytes(BulkShareHelper.payloadObjectForSMS(BulkShareHelper.verifiedSMSList(contacts),
                            ambassadorConfig.getFullName(),
                            messageToShare).toString());
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
    void registerConversionRequest(final ConversionParameters conversionParameters, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.conversionURL());
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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorConfig.pusherChannelNameURL());
                connection.setRequestProperty("Content-Type", "application/json");

                try {
                    final int responseCode = connection.getResponseCode();
                    final String response = getResponse(connection, responseCode);
                    Utilities.debugLog("createPusherChannel", "CREATE PUSHER CHANNEL Response Code = " + responseCode + " and Response = " + response);

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
                            completion.onFailure("Create PusherSDK Channel Failure due to IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
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
    // endregion IDENTIFY REQUESTS

    // region TWITTER REQUESTS
    public void twitterLoginRequest(final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(AmbassadorConfig.TWITTER_KEY, AmbassadorConfig.TWITTER_SECRET);
                RequestToken requestToken = null;
                int responseCode;

                try {
                    requestToken = twitter.getOAuthRequestToken(AmbassadorConfig.CALLBACK_URL);
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
                twitter.setOAuthConsumer(AmbassadorConfig.TWITTER_KEY, AmbassadorConfig.TWITTER_SECRET);

                int responseCode;

                try {
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauthSecret);
                    ambassadorConfig.setTwitterAccessToken(accessToken.getToken());
                    ambassadorConfig.setTwitterAccessTokenSecret(accessToken.getTokenSecret());
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

    public void postToTwitter(final String tweetString, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AccessToken accessToken = new AccessToken(ambassadorConfig.getTwitterAccessToken(),
                        ambassadorConfig.getTwitterAccessTokenSecret());
                final Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(AmbassadorConfig.TWITTER_KEY, AmbassadorConfig.TWITTER_SECRET);
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
                final HttpURLConnection connection = setUpConnection("POST", "https://www.linkedin.com/uas/oauth2/accessToken");

                String charset = "UTF-8";
                String urlParams = null;

                // Create params to send for Access Token
                try {
                    urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(code, charset) +
                            "&redirect_uri=" + URLEncoder.encode(AmbassadorConfig.CALLBACK_URL, charset) +
                            "&client_id=" + URLEncoder.encode(AmbassadorConfig.LINKED_IN_CLIENT_ID, charset) +
                            "&client_secret=" + URLEncoder.encode(AmbassadorConfig.LINKED_IN_CLIENT_SECRET, charset);
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
                                    ambassadorConfig.setLinkedInToken(accessToken);
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
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("Failure with IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    public void postToLinkedIn(final JSONObject objectToPost, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url  = "https://api.linkedin.com/v1/people/~/shares?format=json";

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Host", "api.linkedin.com");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + ambassadorConfig.getLinkedInToken());
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
                } catch (final IOException e) {
                    e.printStackTrace();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            completion.onFailure("Linkedin Post FAILED due to IOException - " + e.getMessage());
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }
    // region LINKEDIN REQUESTS
}
