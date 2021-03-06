package com.jlabs.accidentector.Utils;

/**
 * Created by ramihsn on 3/30/2017.
 */

import android.location.Location;
import android.util.Log;

import com.jlabs.accidentector.Listeners.CustomSensorEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class JsonUtils {
    protected static final String TAG = "JsonUtils";
    public static String packData(List<Location> locations, List<CustomSensorEvent> sensorEvents){
        try
        {
            JSONObject globalJO = new JSONObject();
            JSONObject locsJO = new JSONObject();
            for(int j=0, i=Math.max(locations.size()-7, 0); i<locations.size(); i++, j++)
            {
                JSONObject jo = new JSONObject();
                Location theLocation = locations.get(i);
                String ii = Integer.toString(j);

                jo.put("lon_fld",   theLocation.getLongitude());
                jo.put("lat_fld",   theLocation.getLatitude());
                jo.put("spd_fld",   String.format(Locale.US, "%1$.3f",theLocation.getSpeed()));
                jo.put("brng_fld",  theLocation.getBearing());
                jo.put("locTS_fld", theLocation.getTime());

                locsJO.put("Coodr_fld" + "_" + ii, jo);
            }

            JSONObject accsJO = new JSONObject();
            for(int j=0, i=Math.max(sensorEvents.size()-10, 0); i < sensorEvents.size(); i++, j++)
            {
                JSONObject jo = new JSONObject();
                CustomSensorEvent thesensorEvent = sensorEvents.get(i);
                String ii = Integer.toString(j);

                jo.put("accX_fld", String.format(Locale.US, "%1$.4f", thesensorEvent.values[0]));
                jo.put("accY_fld", String.format(Locale.US, "%1$.4f", thesensorEvent.values[1]));
                jo.put("accZ_fld", String.format(Locale.US, "%1$.4f", thesensorEvent.values[2]));
                jo.put("accTS_fld", thesensorEvent.timeStamp);

                accsJO.put("Acc_fld" +"_" + ii, jo);
            }

            globalJO.put("Locations", locsJO);
            globalJO.put("Accelerations", accsJO);

            String jsonString = globalJO.toString();
            Log.i(TAG, jsonString);
            return jsonString;

        }catch(JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }catch(Exception e){
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
