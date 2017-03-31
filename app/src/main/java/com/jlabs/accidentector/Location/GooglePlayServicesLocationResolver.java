package com.jlabs.accidentector.Location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jlabs.accidentector.Services.AccidenTectorService;
import com.jlabs.accidentector.Services.ActivityRecognitionService;
import com.jlabs.accidentector.Utils.SettingsUtils;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by hhsn8 on 3/5/2017.
 */

public class GooglePlayServicesLocationResolver implements GoogleApiClient.ConnectionCallbacks,
                                                           GoogleApiClient.OnConnectionFailedListener,
                                                           LocationListener,
                                                           UniversalLocationResolverInterface
{
    private final String TAG = getClass().getSimpleName();
    private int  mLocationUpdateInterval;     // [ms]
    private int  mLocationUpdateFastInterval; // [ms]
    private long mActivityDetectionInterval;  // [ms]

    private Context mMyContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    protected LocalBroadcastManager mBroadcaster;

    public GooglePlayServicesLocationResolver(Context pContext,
                                              int pLocationUpdateInterval,
                                              int pLocationUpdateFastInterval,
                                              long pActivityDetectionInterval)
    {
        mMyContext = pContext;
        mLocationUpdateInterval = pLocationUpdateInterval;
        mLocationUpdateFastInterval = pLocationUpdateFastInterval;
        mActivityDetectionInterval = pActivityDetectionInterval;
        mBroadcaster = LocalBroadcastManager.getInstance(mMyContext);
    }

    public void Connect()
    {
        buildGoogleApiClient();
        ConnectLocationService();
    }
    public void Disconnect()
    {
        DisonnectLocationService();
    }

    private synchronized void buildGoogleApiClient()
    {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(mMyContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }
    private void ConnectLocationService()
    {
        if ( !mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.connect();
        }
    }

    private void DisonnectLocationService()
    {
        if ( mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean GetApiConnectionStatus()
    {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        /* Handle Activity API *///TODO: request location data only after activity detection?
        Intent intent = new Intent(mMyContext,
                ActivityRecognitionService.class);

        PendingIntent pendingIntent = PendingIntent.getService(mMyContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient,
                mActivityDetectionInterval,
                pendingIntent);

        // Handle Location API
        mLocationRequest = _createLocationRequest(mLocationUpdateInterval, mLocationUpdateFastInterval);
        // Verify Location settings
        SettingsUtils.VerifyLocationSettings(mLocationRequest, mMyContext, mGoogleApiClient);
        //\ Verify Location setting
        StartLocationUpdates();
    }

    public Location GetLastKnownLocation()
    {
        try
        {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch (SecurityException ex)
        {
            Log.wtf(TAG, ex.toString());
            return null;
        }
    }

    public void StartLocationUpdates()
    {//TODO: start this when driving activity is recognised (need to use PendingIntent?)
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    this);
        }
        catch (SecurityException ex)
        {   // This shouldn't happen since this method is called only after verifying we've got permissions
            Log.wtf(TAG, ex.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.e(TAG, "connection to GoogleApiClient failed. Error: " + connectionResult.getErrorMessage());
    }

    private LocationRequest _createLocationRequest(int pInterval, int pFastInterval)
    {
        return new LocationRequest()
                .setInterval(pInterval)
                .setFastestInterval(pFastInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Location mLastLocation;
    private String mLastUpdateTime;
    @Override
    public void onLocationChanged(Location pLocation)
    {
        try
        {
            mLastLocation = pLocation;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            notifyActivity(mLastLocation);
            // Update location array
            // Collect Data
            if (LocationResolver.Locations.size() > 1 &&
                    LocationResolver.Locations.get(0).getTime() -
                            LocationResolver.Locations.get(LocationResolver.Locations.size()-1).getTime() > AccidenTectorService.MAX_EVENTS_TIME_DIFF_NS)
            {
                LocationResolver.Locations.clear();
            }

            LocationResolver.Locations.add(pLocation);
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }
    private void notifyActivity(Location pLocation)
    {
        Intent intent = new Intent(LocationResolver.COPA_RESULT)
                .putExtra("DataType", "Location")
                .putExtra(LocationResolver.COPA_MESSAGE, new String[] {Double.toString(pLocation.getLatitude()),
                                                                       Double.toString(pLocation.getLongitude()),
                                                                       Float.toString(pLocation.getSpeed()),
                                                                       Float.toString(pLocation.getBearing())});
        mBroadcaster.sendBroadcast(intent);
    }

    @Override
    public Location GetLastLocation() {
        return mLastLocation;
    }
}
