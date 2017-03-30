package com.jlabs.accidentector.Listeners;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

/**
 * Created by hhsn8 on 3/30/2017.
 */

public abstract class ListenerBase implements SensorEventListener {

    protected final String TAG = getClass().getSimpleName();

    private static final long SEC_TO_NS = 1000000000;
    private static final int  MAX_EVENTS_TIME_DIFF = 10; // [sec]
    private static final long MAX_EVENTS_TIME_DIFF_NS = MAX_EVENTS_TIME_DIFF * SEC_TO_NS; // [sec]

    protected static final double ACCEL_TH = 2.0; //TODO: tentative value, needs research...

    protected static List<SensorEvent> Events = Collections.synchronizedList(new ArrayList<SensorEvent>());

    protected int mSensorType = -1;
    protected boolean mIsProcessData = true;
    protected boolean mIsDriving = false; //TODO: implement updating logic (new class? android location service reseaerch needed.)
    protected boolean mIsSensorActive = false; // This shall be set by the sensorListener creator

    @Override
    public void onSensorChanged(SensorEvent pEvent)
    {
        if (mIsProcessData &&
            pEvent.sensor.getType() == mSensorType)
        {
            // Collect Data
            if (Events.size() > 1 &&
                Events.get(0).timestamp - Events.get(Events.size()-1).timestamp > MAX_EVENTS_TIME_DIFF_NS)
            {
                Events.clear();
            }
            Log.i(TAG, "SensorEvent catched @ " +
                        DateFormat.getDateTimeInstance().format(new Date()) +
                        "using: " +
                        pEvent.sensor.getName());

            Events.add(pEvent);
            // Process Data (this is done in this.Subclasses)
        }
    }
    @Override
    public void onAccuracyChanged(Sensor pSenseor, int pAccu)
    {
        if (pSenseor.getType() == mSensorType) {
            // Stop processing data in case sensor becomes unreliable
            mIsProcessData = pAccu == SensorManager.SENSOR_STATUS_ACCURACY_HIGH   ||
                             pAccu == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ||
                             pAccu == SensorManager.SENSOR_STATUS_ACCURACY_LOW    ;
            // LOG
            String msg = "Accelerometer Accuracy Changed to: " + pAccu;
            if (mIsProcessData) Log.i(TAG, msg);
            else                Log.w(TAG, msg);
        }
    }

    protected void SOS() {
        //TODO: implement
    }

    public boolean IsActive()
    {
        return mIsSensorActive;
    }
    public void SetActive(boolean pIsActive)
    {
        mIsSensorActive = pIsActive;
    }
}
/*TODO: IDEAS to ponder:
    Accident verification in multiple dimensions:
        voice (use microphone data...)?

    Implementation Level:
        App level, or Sensors level (check AOSP for implementation of TYPE_LINEAR_ACCELERATION_SENSOR

    App priority:
        Elevate App priority when suspecting an accident?

    Startup:
        Who starts me??
 */