package com.jlabs.accidentector.Listeners;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.renderscript.Float3;
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
            notifyActivity(x, y, z, accel);

            if (accel >= ACCEL_TH && mIsDriving)
            {
                //TODO: improve algo. What about flukes? what about prev samples? what about driving detection & velocity etc...
                SOS();
            }
        }
    }

    private void notifyActivity(float pX, float pY, float pZ, double pAccel)
    {
        Intent intent = new Intent(COPA_RESULT)
                .putExtra("DataType", "Sensor")
                .putExtra(COPA_MESSAGE, new String[] {Float.toString(pX),
                                                      Float.toString(pY),
                                                      Float.toString(pZ),
                                                      Double.toString(pAccel)});
        mBroadcaster.sendBroadcast(intent);
    }
}
