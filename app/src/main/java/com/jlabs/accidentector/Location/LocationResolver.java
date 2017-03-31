package com.jlabs.accidentector.Location;

import android.util.Log;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jlabs.accidentector.Services.AccidenTectorService;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is the app's location capabilities handler.
 * It provides an entry point to Android's location services.
 * By default, it uses the Google Play Services API. In case
 * it's unavailable, it resorts to Android's Location Manager service.
 */
public class LocationResolver
{
    private final String TAG = getClass().getSimpleName();
    private static final int  LOCATION_UPDATE_INTERVAL      = 1000; // 10 [s]
    private static final int  LOCATION_UPDATE_FAST_INTERVAL = 1000; // 10 [s]
    private static final long ACTIVITY_DETECTION_INTERVAL   = 1000; // 30 [s]

    public static final String COPA_RESULT =  "com.jlabs.backend.COPAService.DATA_ARRIVED";
    public static final String COPA_MESSAGE = "com.jlabs.backend.COPAService.DATA_LOCATION";

    public static LinkedList<Location> Locations  = new LinkedList<>();

    private Context mMyContext;

    private LocationHandlerType mLocationHandlerType = LocationHandlerType.NA;

    /** Location Managers */
    private LocationManager mLocationManager;
    private GooglePlayServicesLocationResolver mGoogPSLocResolver;

    /** C'tor */
    public LocationResolver(AccidenTectorService pAccidentectorService)
    {
        mMyContext = pAccidentectorService;
    }

    public void SetContext(AccidenTectorService pAccidentectorService)
    {
        mMyContext = pAccidentectorService;
    }

    public LocationHandlerType GetLocationHandlerType()
    {
        return mLocationHandlerType;
    }

    public void Init()
    {
        //TODO: Check Permissions first
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mMyContext)
                == ConnectionResult.SUCCESS)
        {
            mLocationHandlerType = LocationHandlerType.GooglePlayLocationService;

            /* Start Location Services API*/
            mLocationManager = null;
            mGoogPSLocResolver = new GooglePlayServicesLocationResolver(mMyContext,
                    LOCATION_UPDATE_INTERVAL,
                    LOCATION_UPDATE_FAST_INTERVAL,
                    ACTIVITY_DETECTION_INTERVAL);
        }
        else
        {
            mLocationHandlerType = LocationHandlerType.AndroidLocationManager;

            /* Use LoactionManager class */
            mGoogPSLocResolver = null;
            mLocationManager = (LocationManager) mMyContext.getSystemService(Context.LOCATION_SERVICE);
            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        (long)LOCATION_UPDATE_INTERVAL,
                        (float)0,
                        new AndroidLocationManagerResolver());
            }
            catch (SecurityException secEx)
            {
                Log.wtf(TAG, secEx.toString());
            }
            catch (Exception ex)
            {
                Log.e(TAG, ex.toString());
            }
        }
    }
    public void StartMonitoringTriggers()
    {
        if (mLocationHandlerType == LocationHandlerType.GooglePlayLocationService)
        {
            mGoogPSLocResolver.Connect();
        }
        else
        {

        }
    }

    public void Stop()
    {
        if (mLocationHandlerType == LocationHandlerType.GooglePlayLocationService)
        {
            mGoogPSLocResolver.Disconnect();
        }
        else
        {

        }
    }

    public enum LocationHandlerType
    {
        NA,
        AndroidLocationManager,
        GooglePlayLocationService
    }
}
