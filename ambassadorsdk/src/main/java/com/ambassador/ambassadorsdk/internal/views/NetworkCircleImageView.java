package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 *
 */
public class NetworkCircleImageView extends CircleImageView {

    public NetworkCircleImageView(Context context) {
        super(context);
        init();
    }

    public NetworkCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NetworkCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     *
     */
    private void init() {
        Bitmap image = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888);
        image.eraseColor(android.graphics.Color.WHITE);
    }

    public void load(String url) {
        new LoadImageTask(this).execute(url);
    }

    protected static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected WeakReference<NetworkCircleImageView> networkCircleImageViewWeakReference;

        public LoadImageTask(NetworkCircleImageView networkCircleImageView) {
            networkCircleImageViewWeakReference = new WeakReference<NetworkCircleImageView>(networkCircleImageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(params[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException();
                }

                InputStream inputStream = response.body().byteStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            NetworkCircleImageView networkCircleImageView = networkCircleImageViewWeakReference.get();
            if (networkCircleImageView != null) {
                networkCircleImageView.setImageBitmap(bitmap);
            }
        }

    }

}
