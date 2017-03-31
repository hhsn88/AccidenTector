package com.jlabs.accidentector.Listeners;

import java.util.LinkedList;

import android.content.Intent;
import android.util.Log;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.support.v4.content.LocalBroadcastManager;

import com.jlabs.accidentector.Location.LocationResolver;
import com.jlabs.accidentector.R;
import com.jlabs.accidentector.Services.AccidenTectorService;
import com.jlabs.accidentector.Utils.JsonUtils;
import com.jlabs.accidentector.Utils.NetUtils;

/**
 * Created by hhsn8 on 3/30/2017.
 */

public abstract class ListenerBase implements SensorEventListener {

    protected final String TAG = getClass().getSimpleName();

    protected static final double ACCEL_TH = 4.0; //TODO: tentative value, needs research...

    protected static LinkedList<CustomSensorEvent> Events = new LinkedList<>();
    protected static final int MIN_NUM_OF_CON_SAMPLES = 4;
    protected int mNumOfConcicutiveSamples;

    protected int mSensorType = -1;
    protected boolean mIsProcessData = true;
    protected boolean mIsSensorActive = false; // This shall be set by the sensorListener creator

    static final public String COPA_RESULT =  "com.jlabs.backend.COPAService.DATA_ARRIVED";
    static final public String COPA_MESSAGE = "com.jlabs.backend.COPAService.DATA_SENSOR";
    protected Context mMyContext;
    protected LocalBroadcastManager mBroadcaster;

    private boolean mIsSent;
    public static boolean IsSendSOS;

    public void SetContext(Context pContext)
    {
        mMyContext = pContext;
        mBroadcaster = LocalBroadcastManager.getInstance(mMyContext);
    }

    @Override
    public void onSensorChanged(SensorEvent pEvent)
    {
        try
        {
            if (mIsProcessData &&
                pEvent.sensor.getType() == mSensorType)
            {
                // Collect Data
                if (Events.size() > 1 &&
                    Events.getLast().timeStamp - Events.getFirst().timeStamp > AccidenTectorService.MAX_EVENTS_TIME_DIFF_NS)
                {
                    if (!IsSendSOS ||
                        Events.getLast().timeStamp - Events.getFirst().timeStamp >= 2 * AccidenTectorService.MAX_EVENTS_TIME_DIFF_NS)
                    {
                        Events.remove(0);
                    }
                }
                Events.add(new CustomSensorEvent(pEvent.values, pEvent.timestamp));
                // Process Data (this is done in this.Subclasses)
//                Log.i(TAG, "Acc: " + Long.toString(Events.getLast().timeStamp - Events.getFirst().timeStamp));
            }
        }
        catch (Exception e)
        {
            Log.i(TAG, e.toString());
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

    protected void SOS()
    {
        if ( !mIsSent )
        {
            String accidentData = JsonUtils.packData(LocationResolver.Locations, Events);
            new NetUtils().execute(accidentData);
            mIsSent = true;

            // Stop detection
//            stopDetection();
        }
    }

    public boolean IsActive()
    {
        return mIsSensorActive;
    }
    public void SetActive(boolean pIsActive)
    {
        mIsSensorActive = pIsActive;
    }

    protected void notifyActivity(float pX, float pY, float pZ, double pAccel, long pTimeStamp)
    {
        Intent intent = new Intent(COPA_RESULT)
                .putExtra("DataType", "Sensor")
                .putExtra(COPA_MESSAGE, new String[] {Float.toString(pX),
                        Float.toString(pY),
                        Float.toString(pZ),
                        Double.toString(pAccel),
                        Long.toString(pTimeStamp)});
        mBroadcaster.sendBroadcast(intent);
    }

    protected void stopDetection()
    {
        try {
            Intent intent=new Intent(mMyContext.getApplicationContext(),AccidenTectorService.class)
                    .putExtra("action",mMyContext.getString(R.string.StopAccidentector));
            mMyContext.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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