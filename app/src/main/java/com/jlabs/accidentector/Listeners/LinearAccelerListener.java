package com.jlabs.accidentector.Listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

/**
 * Created by hhsn8 on 3/30/2017.
 */

/*Summary:
 *  This class handles the LinearAccelerometer events, calculating
 *  the linear acceleration of the device along the 3 axes as well
 *  as the total acceleration.
 */

public class LinearAccelerListener extends ListenerBase {

    public static LinearAccelerListener GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new LinearAccelerListener();
        }
        return mInstance;
    }
    private static LinearAccelerListener mInstance;
    public LinearAccelerListener()
    {
        this.mSensorType = Sensor.TYPE_ACCELEROMETER;
        Log.i(TAG, "LinearAcceler Listener created!");
    }

    @Override
    public void onSensorChanged(SensorEvent pEvent) {
        super.onSensorChanged(pEvent);
        if (this.mIsProcessData)
        {   // Process Data
            float x = pEvent.values[0];
            float y = pEvent.values[1];
            float z = pEvent.values[2];
            double accel = Math.sqrt(x*x + y*y + z*z);
            //TODO: improve algo. What about flukes? what about prev samples? what about drivint detection & velocity etc...
            if (accel >= super.ACCEL_TH)
            {
                SOS();
            }
        }
    }
}
