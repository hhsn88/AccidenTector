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

public class JsonUtils {
    protected static final String TAG = "JsonUtils";
    public static String packData(List<Location> locations, List<CustomSensorEvent> sensorEvents){
        try
        {
            JSONObject globalJO = new JSONObject();
            JSONObject locsJO = new JSONObject();
            for(int i=0; i<Math.min(locations.size(), 10); i++){
                JSONObject jo = new JSONObject();
                Location theLocation = locations.get(i);
                String ii = Integer.toString(i);

                jo.put("lon_fld" + "_" + ii,   theLocation.getLongitude());
                jo.put("lat_fld" + "_" + ii,   theLocation.getLatitude());
                jo.put("spd_fld" + "_" + ii,   theLocation.getSpeed());
                jo.put("brng_fld" + "_" + ii,  theLocation.getBearing());
                jo.put("locTS_fld" +" _" + ii, theLocation.getTime());

                locsJO.put("Coodr_fld" +" _" + ii, jo);
            }

            JSONObject accsJO = new JSONObject();
            for(int i=0; i<Math.min(sensorEvents.size(),10); i++) {
                JSONObject jo = new JSONObject();
                CustomSensorEvent thesensorEvent = sensorEvents.get(i);
                String ii = Integer.toString(i);

                jo.put("accX_fld" + "_" + ii, thesensorEvent.values[0]);
                jo.put("accY_fld" + "_" + ii, thesensorEvent.values[1]);
                jo.put("accZ_fld" + "_" + ii, thesensorEvent.values[2]);
                jo.put("accTS_fld" + "_" + ii, thesensorEvent.timeStamp);

                accsJO.put("Acc_fld" +" _" + ii, jo);
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
