package com.jlabs.accidentector.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by hhsn8 on 3/30/2017.
 */

public class PermissionsUtils
{
    public static void VerifyLocationPermissions(final Context pContext)
    {
        if (ContextCompat.checkSelfPermission(pContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            //TODO: request permissions
        }
    }
}
