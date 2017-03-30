package com.jlabs.accidentector.Location;

import android.location.LocationManager;
import android.util.Log;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jlabs.accidentector.Services.AccidenTectionService;

/**
 * This class is the app's location capabilities handler.
 * It provides an entry point to Android's location services.
 * By default, it uses the Google Play Services API. In case
 * it's unavailable, it resorts to Android's Location Manager service.
 */
public class LocationResolver
{
    private final String TAG = getClass().getSimpleName();
    private static final int  LOCATION_UPDATE_INTERVAL      = 10000; // 10 [s]
    private static final int  LOCATION_UPDATE_FAST_INTERVAL = 10000; // 10 [s]
    private static final long ACTIVITY_DETECTION_INTERVAL   = 30000; // 30 [s]

    private Context mMyContext;

    private LocationHandlerType mLocationHandlerType = LocationHandlerType.NA;

    /** Location Managers */
    private LocationManager mLocationManager;
    private GooglePlayServicesLocationResolver mGoogPSLocResolver;

    /** C'tor */
    public LocationResolver(AccidenTectionService pAccidentectorService)
    {
        mMyContext = pAccidentectorService;

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
                Log.d(TAG, secEx.toString());
                // Handle permissions
                // ...
            }
            catch (Exception ex)
            {
                Log.e(TAG, ex.toString());
            }
        }
    }

    public void SetContext(AccidenTectionService pAccidentectorService)
    {
        mMyContext = pAccidentectorService;
    }

    public LocationHandlerType GetLocationHandlerType()
    {
        return mLocationHandlerType;
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
