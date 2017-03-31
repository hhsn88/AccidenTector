package com.jlabs.accidentector.Listeners;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.jlabs.accidentector.Services.AccidenTectorService;

/**
 * Created by hhsn8 on 3/30/2017.
 */

/*Summary:
 *  This class handles the LinearAccelerometer events, calculating
 *  the linear acceleration of the device along the 3 axes as well
 *  as the total acceleration.
 */

public class LinearAccelerListener extends ListenerBase {

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

            // Notify activity so UI is updated
            notifyActivity(x, y, z, accel, pEvent.timestamp);

            if (accel >= ACCEL_TH)
            {
                mNumOfConcicutiveSamples++;
            }
            else
            {
                mNumOfConcicutiveSamples = 0;
            }

            if (mNumOfConcicutiveSamples > MIN_NUM_OF_CON_SAMPLES)
            {
                IsSendSOS = true;
            }

            if (Events.size() > 1 && IsSendSOS &&
                    Events.getLast().timeStamp - Events.getFirst().timeStamp > 2 * AccidenTectorService.MAX_EVENTS_TIME_DIFF_NS)
            {
                SOS();
            }
        }
    }
}
