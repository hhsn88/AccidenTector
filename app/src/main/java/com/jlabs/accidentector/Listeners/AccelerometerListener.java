package com.jlabs.accidentector.Listeners;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.jlabs.accidentector.Services.AccidenTectorService;

/*Summary:
 *  This class handles Accelerometer events.
 *  It's also responsible for filtering the Accelerometer samples
 *  in order to first separate the force of Gravity (using a LPF)
 *  and then remove it (using a HPF).
 */
public class AccelerometerListener extends ListenerBase {

    private boolean mIsFirstSample = true;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    public AccelerometerListener()
    {
        this.mSensorType = Sensor.TYPE_ACCELEROMETER;
        Log.i(TAG, "Accelerometer Listener created!");
    }

    @Override
    public void onSensorChanged(SensorEvent pEvent)
    {
        super.onSensorChanged(pEvent);

        if (this.mIsProcessData)
        {   // Process Data
            filterGravity(pEvent);

            float x = linear_acceleration[0];
            float y = linear_acceleration[1];
            float z = linear_acceleration[2];
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

    private void filterGravity(SensorEvent pEvent)
    {
        if (mIsFirstSample)
        {
            gravity[0] = pEvent.values[0];
            gravity[1] = pEvent.values[1];
            gravity[2] = pEvent.values[2];
            mIsFirstSample = false;
        }
        //TODO: improve (dynamic alpha? other filter(s)? initial gravity values(=initial sample?)...)
        // Alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.
        final float alpha = 0.7f;
        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * pEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * pEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * pEvent.values[2];
        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = pEvent.values[0] - gravity[0];
        linear_acceleration[1] = pEvent.values[1] - gravity[1];
        linear_acceleration[2] = pEvent.values[2] - gravity[2];
    }
}

// Other filtering approaches:
/* *1*
// If you have a fixed frame rate
smoothedValue += (newValue - smoothedValue) / smoothing
// If you have a varying frame rate
smoothedValue += timeSinceLastUpdate * (newValue - smoothedValue) / smoothing
*/
