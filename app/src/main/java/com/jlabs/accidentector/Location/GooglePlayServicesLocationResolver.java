package com.jlabs.accidentector.Location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jlabs.accidentector.Services.ActivityRecognitionService;
import com.jlabs.accidentector.Utils.PermissionsUtils;
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

    public GooglePlayServicesLocationResolver(Context pContext,
                                              int pLocationUpdateInterval,
                                              int pLocationUpdateFastInterval,
                                              long pActivityDetectionInterval)
    {
        mMyContext = pContext;
        mLocationUpdateInterval = pLocationUpdateInterval;
        mLocationUpdateFastInterval = pLocationUpdateFastInterval;
        mActivityDetectionInterval = pActivityDetectionInterval;
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
        mLocationRequest = CreateLocationRequest(mLocationUpdateInterval, mLocationUpdateFastInterval);
        // Verify Location settings
        SettingsUtils.VerifyLocationSettings(mLocationRequest, mMyContext, mGoogleApiClient);
        //\ Verify Location setting
    }

    public Location GetLastKnownLocation()
    {
        try
        {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        catch (SecurityException ex)
        {
            Log.e(TAG, ex.toString());
            PermissionsUtils.VerifyLocationPermissions(mMyContext);
            return null;
        }
    }
    private void startLocationUpdates()
    {//TODO: start this when driving activity is recognised (need to use PendingIntent?)
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest,
                    this);
        }
        catch (SecurityException ex)
        {   // This shouldn't happen since this method is called only after verifying we've got permissions
            Log.e(TAG, ex.toString());
            PermissionsUtils.VerifyLocationPermissions(mMyContext);
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

    private LocationRequest CreateLocationRequest(int pInterval, int pFastInterval)
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
        mLastLocation = pLocation;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public Location GetLastLocation() {
        return mLastLocation;
    }
}
