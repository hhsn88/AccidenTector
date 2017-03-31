package com.jlabs.accidentector.Utils;

/**
 * Created by ramihsn on 3/30/2017.
 */

import android.location.Location;
import android.util.Log;

import com.jlabs.accidentector.Listeners.CustomSensorEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonUtils {
    protected static final String TAG = "JsonUtils";
    public static String packData(List<Location> locations, List<CustomSensorEvent> sensorEvents){
        try{
            JSONObject globalJO = new JSONObject();
            for(int i=0; i<locations.size(); i++){
                JSONObject jo = new JSONObject();
                Location theLocation = locations.get(i);
                String ii = Integer.toBinaryString(i);
                jo.put("longitude_fld" + "_" + ii,theLocation.getLongitude());
                jo.put("latitude_fld" + "_" + ii, theLocation.getLatitude());
                jo.put("speed_fld" + "_" + ii,    theLocation.getSpeed());
                jo.put("bearing_fld" + "_" + ii,  theLocation.getBearing());
                jo.put("accts_fld" +" _" + ii,    theLocation.getTime());

                globalJO.put("coodr_fld" +" _" + ii,jo);
            }

            for(int i=0; i<sensorEvents.size(); i++) {
                JSONObject jo = new JSONObject();
                CustomSensorEvent thesensorEvent = sensorEvents.get(i);
                String ii = Integer.toBinaryString(i);
                jo.accumulate("accelerationX_fld"+"_"+ii, Float.toString(sensorEvents.get(i).values[0])+"_"+ii);
                jo.accumulate("accelerationY_fld"+"_"+ii, Float.toString(sensorEvents.get(i).values[1])+"_"+ii);
                jo.accumulate("accelerationZ_fld"+"_"+ii, Float.toString(sensorEvents.get(i).values[2])+"_"+ii);
                jo.accumulate("locTimeStamp_fld"+"_"+ii,  Float.toString(sensorEvents.get(i).timeStamp)+"_"+ii);
            }

            return globalJO.toString();

        }catch(JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }catch(Exception e){
            Log.e(TAG, e.toString());
            return null;
        }

    }
}
