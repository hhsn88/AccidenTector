package com.jlabs.accidentector.Utils;

/**
 * Created by hhsn8 on 3/30/2017.
 */


import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtils extends AsyncTask<String, String, String> {
    protected final String TAG = getClass().getSimpleName();
    private final String url = "http://rsossl.net23.net";
    private final String command = "?SET=";

    public NetUtils() {
        //set context variables if required
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        String urlString = url + command ;// URL to call
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

/*    @Override
    protected void onPostExecute(String result) {
        //Update the UI
    }

    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            Log.w(TAG, e.toString());
        } catch (IOException e) {
           Log.wtf(TAG, e.toString());
        }
    }
    public static void main(String[]args){
        String url = "http://rsossl.net23.net";
        String f1 = "?wtf=";
        String data = "Rami The King";
        tst t = new tst();
        t.doInBackground(url, f1, data);
    }*/

