package com.jlabs.accidentector.Utils;

import android.util.Log;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by hhsn8 on 3/30/2017.
 */

public class SettingsUtils
{
    private static final String TAG = "SettingsUtils";

    public static void VerifyLocationSettings(LocationRequest mLocationRequest,
                                              final Context pContext,
                                              final GoogleApiClient pGoogleApiClient)
    {
        // Get the current location settings of a user's device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // Check whether the current location settings are satisfied
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(pGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
            {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try
                        {
                            enableGPSSettings(pContext);
                        }
                        catch (Exception ex)
                        {   // TODO: Handle situation
                            Log.e(TAG, "Location enabling failed.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // TODO: Handle situation
                        Log.e(TAG, "Location enabling unavailable.");
                        break;
                }
            }
        });
    }

    private static void enableGPSSettings(Context pContext)
    {
        Settings.Secure.putInt(pContext.getContentResolver(),
                               Settings.Secure.LOCATION_MODE,
                               Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
    }
}
