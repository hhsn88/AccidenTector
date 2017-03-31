package com.jlabs.accidentector.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jlabs.accidentector.Listeners.ListenerBase;
import com.jlabs.accidentector.Location.LocationResolver;
import com.jlabs.accidentector.R;
import com.jlabs.accidentector.Services.AccidenTectorService;
import com.jlabs.accidentector.Utils.PermissionsUtils;

import java.util.Locale;

public class MainActivity extends Activity {

    protected final String TAG = getClass().getSimpleName();

    private BroadcastReceiver mUiUpdatesReceiver;

    private int mSensorEventsCount;
    private int mLocationEventsCount;
    // Acc
    public TextView mTextViewX;
    public TextView mTextViewY;
    public TextView mTextViewZ;
    public TextView mTextViewC;
    public TextView mTextViewT;
    // Loc
    public TextView mTextViewLat;
    public TextView mTextViewLon;
    public TextView mTextViewVel;
    public TextView mTextViewHeading;
    public TextView mTextViewLocT;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            Log.i(TAG, "onCreate()");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            InitViews();
            // Init UI update Receiver
            mUiUpdatesReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    String dataType = intent.getStringExtra("DataType");

                    if (dataType.equals("Location"))
                    {
                        String[] accValues = intent.getStringArrayExtra(LocationResolver.COPA_MESSAGE);
                        _updateLocViews(accValues);
                    }
                    else
                    {
                        String[] accValues = intent.getStringArrayExtra(ListenerBase.COPA_MESSAGE);
                        _updateAccViews(accValues);
                    }
                }
            };
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }

    private void InitViews()
    {
        // Acc
        mTextViewX = (TextView)findViewById(R.id.xAcclTB);
        mTextViewY = (TextView)findViewById(R.id.yAcclTB);
        mTextViewZ = (TextView)findViewById(R.id.zAcclTB);
        mTextViewC = (TextView)findViewById(R.id.countTB);
        mTextViewT = (TextView)findViewById(R.id.totalTB);
        // Loc
        mTextViewLat = (TextView)findViewById(R.id.latTB);
        mTextViewLon = (TextView)findViewById(R.id.lonTB);
        mTextViewVel = (TextView)findViewById(R.id.velTB);
        mTextViewHeading = (TextView)findViewById(R.id.headingTB);
        mTextViewLocT = (TextView)findViewById(R.id.locCountTB);
    }

    private void _updateLocViews(String[] pLocValues)
    {
        double lat = Double.parseDouble(pLocValues[0]);
        double lon = Double.parseDouble(pLocValues[1]);
        float vel = Float.parseFloat(pLocValues[2]);
        float heading = Float.parseFloat(pLocValues[3]);

        mLocationEventsCount++;
        mTextViewLat.setText(String.format(Locale.US, "%1$.6f", lat));
        mTextViewLon.setText(String.format(Locale.US, "%1$.6f", lon));
        mTextViewVel.setText(String.format(Locale.US, "%1$.3f", vel));
        mTextViewHeading.setText(String.format(Locale.US, "%1$.3f", heading));
        mTextViewLocT.setText(String.valueOf(mLocationEventsCount));
    }
    private void _updateAccViews(String[] pAccValues)
    {
        float x = Float.parseFloat(pAccValues[0]);
        float y = Float.parseFloat(pAccValues[1]);
        float z = Float.parseFloat(pAccValues[2]);
        double accel = Double.parseDouble(pAccValues[3]);

        mSensorEventsCount++;
        mTextViewX.setText(String.format(Locale.US, "%1$.3f", x));
        mTextViewY.setText(String.format(Locale.US, "%1$.3f", y));
        mTextViewZ.setText(String.format(Locale.US, "%1$.3f", z));
        mTextViewT.setText(String.format(Locale.US, "%1$.3f", accel));
        mTextViewC.setText(String.valueOf(mSensorEventsCount));
    }

    @Override
    protected void onStart()
    {
        try
        {
            Log.i(TAG, "onPause()");
            super.onStart();
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver((mUiUpdatesReceiver),
                    new IntentFilter(ListenerBase.COPA_RESULT));
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver((mUiUpdatesReceiver),
                    new IntentFilter(LocationResolver.COPA_RESULT));
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }
    @Override
    protected void onResume()
    {
        try
        {
            Log.i(TAG, "onResume()");
            super.onResume();

            if (PermissionsUtils.VerifyLocationPermissions(this))
            {
                // We have location permissions :D
                _startManualDetection();
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }
    @Override
    protected void onPause() {
        Log.i(TAG, "onPause()");
        super.onPause();
    }
    @Override
    protected void onStop()
    {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mUiUpdatesReceiver);
        super.onStop();
    }

    private void _startManualDetection()
    {
        try {
            Intent intent = new Intent(getApplicationContext(), AccidenTectorService.class)
                    .putExtra("action", getString(R.string.StartAccidentector));
            this.startService(intent);
        }
        catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        try
        {
            switch (requestCode)
            {
                case PermissionsUtils.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        // We have location permissions :D
                        _startManualDetection();
                    }
                    else
                    {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }
}
