package com.jlabs.accidentector.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.jlabs.accidentector.Listeners.ListenerBase;
import com.jlabs.accidentector.R;
import com.jlabs.accidentector.Location.LocationResolver;
import com.jlabs.accidentector.Listeners.AccelerometerListener;
import com.jlabs.accidentector.Listeners.LinearAccelerListener;

public class AccidenTectorService extends Service {
    /**TODO s (unordered):
     * Check driving mode
     * Manage sensor sampling accuracy/frequency according to strong/weak indication of driving
     */
    public static final long SEC_TO_NS = 1000000000;
    public static final int  MAX_EVENTS_TIME_DIFF = 10; // [sec]
    public static final long MAX_EVENTS_TIME_DIFF_NS = MAX_EVENTS_TIME_DIFF * SEC_TO_NS; // [nano sec]

    protected final String TAG = getClass().getSimpleName();

    private final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    /** Sensors */
    private SensorManager mSensorManager;
    private Sensor mActiveSensor;
    private ListenerBase mActiveSensorListener;

    /** Location */
    private LocationResolver mLocationResolver;

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate(): service created");
    }

    /**
     * Hook method called each time a Started Service is sent an
     * Intent via startService().
     */
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null)
        {
            Log.i(TAG, "onStartCommand: action is: " + intent.getAction());

            if (intent.getStringExtra("action").equals(getString(R.string.StartAccidentector)))
            /* Start Accidentector */
            {
                if (mActiveSensorListener == null)
                {
                    /* First run */
                    _getAccSensor();
                    if (mActiveSensor != null)
                    {
                        mLocationResolver = new LocationResolver(this);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mLocationResolver.Init();
                                mLocationResolver.StartMonitoringTriggers();
                            }
                        }).start();
                    }
                    else
                    {
                        /* No HW Accelerometer! App cannot run on this device */
                        this.stopSelf();
                        return Service.START_NOT_STICKY;
                    }
                }
            }

            if (intent.getStringExtra("action").equals(getString(R.string.StopAccidentector)))
            /* Stop Accidentector */
            {
                if (mActiveSensorListener != null
                    && mActiveSensorListener.IsActive())
                {
                    mActiveSensorListener.SetActive(false);
                    mLocationResolver.Stop();
                }
            }

            if (intent.getStringExtra("action").startsWith("Activity_"))
            /* Activity detected */
            {
                _handleActivityDetected(intent.getStringExtra("action"));
            }
        }

        if (intent == null)
        {
            // Service's process was kiled by the system and was automatically restarted
            if (mLocationResolver != null)
            {
                mLocationResolver.SetContext(this);
            }
            else
            {
                //TODO: re-initiate location resolver
            }
        }

        /* Restart the Service automatically if its process is killed while it's running. */
        return Service.START_STICKY;
    }

    private void _handleActivityDetected(String pDetectedActivity)
    {
        switch (pDetectedActivity)
        {
            case "Activity_DrivingDetected":
                _startSensor();
                break;
            case "Activity_OnBicycleDetected":
            case "Activity_WalkingDetected":
            case "Activity_RunningDetected":
                //TODO: perioically check location. if near driving zone, start monitoring
                break;
            default:
                break;
        }
    }

    private SensorManager _getSensorManager()
    {
        if (mSensorManager == null)
        {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        return mSensorManager;
    }

    private void _getAccSensor()
    {
        Sensor linearAcceler = _getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (linearAcceler != null)
        {
            /* Use Linear Accelerometer */
            mActiveSensor = linearAcceler;
            mActiveSensorListener = new LinearAccelerListener();
        }
        else
        {
            Sensor accelerometer = _getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null)
            {
                /* Use Accelerometer */
                mActiveSensor = accelerometer;
                mActiveSensorListener = new AccelerometerListener();
            }
            else
            {
                /* No HW Accelerometer! App cannot run on this device */
                mActiveSensor = null;
                mActiveSensorListener = null;
            }
        }
        mActiveSensorListener.SetContext(getApplicationContext());
    }

    private void _startSensor()
    {
        if ( mActiveSensorListener.IsActive() )
        {
            // Already active, no need for action...
            return;
        }

        // Make active
        _getSensorManager().registerListener(mActiveSensorListener,
                mActiveSensor,
                SENSOR_DELAY);
        mActiveSensorListener.SetActive(true);
    }

    private void _stopSensor()
    {
        if ( !mActiveSensorListener.IsActive() )
        {
            // Inactive, no need for action...
            return;
        }

        // Make inactive
        _getSensorManager().unregisterListener(mActiveSensorListener, mActiveSensor);
        mActiveSensorListener.SetActive(false);
    }

    /** Make Service unstoppable: method 1 */ //TODO: explore other options...!!
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);
    }
    /** Make Service unstoppable: method 2 */ //TODO: explore other options...!!
    @Override
    public void onDestroy()
    {
        Intent intent = new Intent("com.jlabs.accidentector.START_DETECTION_MANUAL");
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent)
    {   // It's a started service
        return null;
    }
}
