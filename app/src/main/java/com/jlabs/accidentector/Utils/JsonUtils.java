package com.jlabs.accidentector.Utils;

/**
 * Created by ramihsn on 3/30/2017.
 */

import android.hardware.SensorEvent;
import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonUtils {
    protected static final String TAG = "JsonUtils";
    public static void packData(List<Location> locations, List<SensorEvent> sensorEvents){
        try{
            JSONObject jo = new JSONObject();
            for(int i=0; i<locations.size(); i++){
                jo.accumulate("longitude_fld"+"_"+Integer.toBinaryString(i),    Double.toString(locations.get(i).getLongitude())+"_"+Integer.toBinaryString(i));
                jo.accumulate("latitude_fld"+"_"+Integer.toBinaryString(i),     Double.toString(locations.get(i).getLatitude())+Integer.toBinaryString(i));
                jo.accumulate("speed_fld"+"_"+Integer.toBinaryString(i),        Float.toString(locations.get(i).getSpeed())+Integer.toBinaryString(i));
                jo.accumulate("bearing_fld"+"_"+Integer.toBinaryString(i),      Float.toString(locations.get(i).getBearing())+"_"+Integer.toBinaryString(i));
                jo.accumulate("accts_fld"+"_"+Integer.toBinaryString(i),    Long.toString(locations.get(i).getTime())+"_"+Integer.toBinaryString(i));
            }

            for(int i=0; i<sensorEvents.size(); i++) {
                jo.accumulate("accelerationX_fld"+"_"+Integer.toBinaryString(i), Float.toString(sensorEvents.get(i).values[0])+"_"+Integer.toBinaryString(i));
                jo.accumulate("accelerationY_fld"+"_"+Integer.toBinaryString(i), Float.toString(sensorEvents.get(i).values[1])+"_"+Integer.toBinaryString(i));
                jo.accumulate("accelerationZ_fld"+"_"+Integer.toBinaryString(i), Float.toString(sensorEvents.get(i).values[2])+"_"+Integer.toBinaryString(i));
                jo.accumulate("locTimeStamp_fld"+"_"+Integer.toBinaryString(i), Float.toString(sensorEvents.get(i).timestamp)+"_"+Integer.toBinaryString(i));
            }

            new NetUtils().doInBackground(jo.toString());

        }catch(JSONException e){
            Log.e(TAG, e.toString());
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }

    }
}
