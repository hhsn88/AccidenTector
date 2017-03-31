package com.jlabs.accidentector.Utils;

/**
 * Created by ramihsn on 3/30/2017.
 */


import android.os.AsyncTask;

import com.jlabs.accidentector.Listeners.ListenerBase;
import com.jlabs.accidentector.Location.LocationResolver;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtils extends AsyncTask<String, String, String> {
    protected final String TAG = getClass().getSimpleName();
    private final String url = "http://rsossl.net23.net";
    private final String command = "?side=client&";

    public NetUtils()
    {
    }

    public void SendAccidentData(String pAccData)
    {
        doInBackground(pAccData);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String urlString = url + command + "json=";// URL to call
        for (int i = 0; i < params.length; i++) {
            urlString += params[i];
        }

        String resultToDisplay = "";

        InputStream in = null;
        try {

            URL url = new URL(urlString);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());


        } catch (Exception e) {

            System.out.println(e.getMessage());

            return e.getMessage();

        }
        try {
            resultToDisplay = IOUtils.toString(in, "UTF-8");
            //to [convert][1] byte stream to a string
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultToDisplay;
    }
}