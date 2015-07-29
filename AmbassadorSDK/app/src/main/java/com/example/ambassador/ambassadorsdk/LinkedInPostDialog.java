package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by JakeDunahee on 7/27/15.
 */
public class LinkedInPostDialog extends Dialog {
    Button btnPost, btnCancel;
    EditText etMessage;
    AmbassadorActivity activity;

    public LinkedInPostDialog(Activity activity) {
        super(activity);
        this.activity = (AmbassadorActivity)activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_linkedin_post);

        btnPost = (Button) findViewById(R.id.btnShare);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etMessage.setText(AmbassadorSingleton.getInstance().rafParameters.shareMessage);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    postToLinkedIn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void postToLinkedIn() throws IOException {
        LinkedInPostRequest linkedInPostRequest = new LinkedInPostRequest();
        SharedPreferences prefs = activity.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        String token =  prefs.getString("linkedInToken", null);
        linkedInPostRequest.token = token;
        String userMessage = etMessage.getText().toString();

        try {

            JSONObject body = new JSONObject("{" +
                    "\"comment\": \"" + userMessage + "\"," +
                    "\"visibility\": " + "{ \"code\": \"anyone\" }" +
                    "}");
            linkedInPostRequest.object = body;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        linkedInPostRequest.dialog = this;
        linkedInPostRequest.execute();
    }

    class LinkedInPostRequest extends AsyncTask<Void, Void, Void> {
        public JSONObject object;
        public Dialog dialog;
        public String token;
        public int postStatus;
        @Override
        protected Void doInBackground(Void... params) {
            String url  = "https://api.linkedin.com/v1/people/~/shares?format=json";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Host", "api.linkedin.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("x-li-format", "json");

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(object.toString());
                wr.flush();
                wr.close();

                postStatus = connection.getResponseCode();
            } catch (IOException e) {
                Toast.makeText(dialog.getOwnerActivity(), "Cannot connect to internet", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (postStatus < 300 && postStatus > 199) {
                Toast.makeText(dialog.getOwnerActivity(), "Posted successfully!", Toast.LENGTH_SHORT).show();
                dialog.hide();
            } else {
                Toast.makeText(dialog.getOwnerActivity(), "Unable to post, please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


