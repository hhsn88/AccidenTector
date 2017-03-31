package com.jlabs.accidentector.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */

public class ActivityRecognitionService extends IntentService
{
    private final static int ACTIVITY_CONFIDENCE_THRESHOLD = 75;
    private final static String TAG = "ActivityRecogService";

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }
    public ActivityRecognitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if(ActivityRecognitionResult.hasResult(intent)) 
        {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            
            _handleMostProbableActivity(result.getMostProbableActivity());
        }
    }

    private void _handleMostProbableActivity(DetectedActivity pMostProbableActivity)
    {
        switch( pMostProbableActivity.getType() )
        {
            case DetectedActivity.IN_VEHICLE: {
                Log.i(TAG, "In Vehicle: " + pMostProbableActivity.getConfidence() );
                if( pMostProbableActivity.getConfidence() >= ACTIVITY_CONFIDENCE_THRESHOLD )
                {
                    _notifyActivityDetected("Activity_DrivingDetected");
                }
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                Log.i(TAG, "On Bicycle: " + pMostProbableActivity.getConfidence() );
                if( pMostProbableActivity.getConfidence() >= ACTIVITY_CONFIDENCE_THRESHOLD )
                {
                    _notifyActivityDetected("Activity_OnBicycleDetected");
                }
                break;
            }
            case DetectedActivity.WALKING: {
                Log.i(TAG, "Walking: " + pMostProbableActivity.getConfidence() );
                if( pMostProbableActivity.getConfidence() >= ACTIVITY_CONFIDENCE_THRESHOLD )
                {
                    _notifyActivityDetected("Activity_WalkingDetected");
                }
                break;
            }
            case DetectedActivity.RUNNING: {
                Log.i(TAG, "Running: " + pMostProbableActivity.getConfidence() );
                if( pMostProbableActivity.getConfidence() >= ACTIVITY_CONFIDENCE_THRESHOLD )
                {
                    _notifyActivityDetected("Activity_RunningDetected");
                }
                break;
            }
            case DetectedActivity.ON_FOOT: {
                Log.i(TAG, "On Foot: " + pMostProbableActivity.getConfidence() );
                break;
            }
            case DetectedActivity.STILL: {
                Log.i(TAG, "Still: " + pMostProbableActivity.getConfidence() );
                break;
            }
            case DetectedActivity.TILTING: {
                Log.i(TAG, "Tilting: " + pMostProbableActivity.getConfidence() );
                break;
            }
            case DetectedActivity.UNKNOWN: {
                Log.i(TAG, "Unknown: " + pMostProbableActivity.getConfidence() );
                break;
            }
        }
    }

    private void _notifyActivityDetected(String pActivityName)
    {
        Log.i(TAG, pActivityName);
        Intent intent = new Intent(this, AccidenTectorService.class)
                .putExtra("action", pActivityName);
        startService(intent);
    }
}