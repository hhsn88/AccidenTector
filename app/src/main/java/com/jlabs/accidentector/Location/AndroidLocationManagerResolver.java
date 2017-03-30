package com.jlabs.accidentector.Location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by hhsn8 on 3/5/2017.
 */

public class AndroidLocationManagerResolver implements LocationListener, UniversalLocationResolverInterface
{//TODO: implement activity detection simular to GooglePlayServices'
    private Location mLastLocation;
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public Location GetLastLocation() {
        return mLastLocation;
    }
}
