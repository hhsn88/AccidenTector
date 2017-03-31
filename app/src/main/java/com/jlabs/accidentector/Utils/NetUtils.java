package com.jlabs.accidentector.Utils;

/**
 * Created by ramihsn on 3/30/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtils extends AsyncTask<String, String, String> {
    protected final String TAG = getClass().getSimpleName();
    private final String url = "http://rsossl.net23.net";
    private final String command = "/?side=client&";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params)
    {
        String urlString = url + command + "json=";// URL to call
        for (int i = 0; i < params.length; i++) {
            urlString += params[i];
        }
        try
        {
            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            Log.i("NetUtils", "HTTP response: " + Integer.toString(responseCode));
            return null;
        }
        catch (Exception e)
        {
            Log.d("NetUtils", e.toString());
        }
        return null;
    }
}