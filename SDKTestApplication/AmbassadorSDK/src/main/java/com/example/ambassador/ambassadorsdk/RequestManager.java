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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by JakeDunahee on 9/29/15.
 */
public class RequestManager {
    private static RequestManager mInstance = null;
    final Handler mHandler = new Handler();


    static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }

        return mInstance;
    }

    interface RequestCompletion {
        void onSuccess(String successResponse);
        void onFailure(String failureResponse);
    }

    HttpURLConnection setUpConnection(String methodType, String url) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            if (methodType.equals("POST")) { connection.setDoOutput(true); }
            connection.setRequestMethod(methodType);
            connection.setRequestProperty("Content-Type", "application/json");
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

    // region BULK SHARE
    void bulkShareSms(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.bulkSMSShareURL());

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
                    completion.onFailure(e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    void bulkShareEmail(final List<ContactObject> contacts, final String messageToShare, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.bulkEmailShareURL());

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
                    completion.onFailure(e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    void bulkShareTrack(final List<ContactObject> contacts, final boolean isSMS) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.shareTrackURL());

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
                }
            }
        };
        new Thread(runnable).start();
    }
    // endregion BULK SHARE

    void registerConversionRequest(final ConversionParameters conversionParameters, final RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.conversionURL());

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
                    completion.onFailure(e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    public void identifyRequest() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HttpURLConnection connection = setUpConnection("POST", AmbassadorSingleton.identifyURL() + AmbassadorSingleton.getInstance().getUniversalID());

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
                    completion.onFailure(e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }
}
