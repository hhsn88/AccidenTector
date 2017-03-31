package com.jlabs.accidentector.Receivers;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import com.jlabs.accidentector.R;
import com.jlabs.accidentector.Activities.MainActivity;
import com.jlabs.accidentector.Services.AccidenTectorService;

/*Summary:
 *  This class Listens to broadcasts and starts the AccidentorService
 *  in case one of the following broadcasts are received:
 *  1. RECEIVE_BOOT_COMPLETED
 *  2. com.jlabs.accidentector.START_DETECTION_MANUAL
 */

public class AccidentectorInitiationReceiver extends BroadcastReceiver {

    private final boolean IS_START_ACTIVITY = true;
    protected final String TAG = getClass().getSimpleName();


    public AccidentectorInitiationReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "onReceive: received action is: " + intent.getAction());
        switch (intent.getAction())
        {
            case android.content.Intent.ACTION_BOOT_COMPLETED:
            case "com.jlabs.accidentector.START_DETECTION_MANUAL":
                StartAccedetectorService(context, context.getString(R.string.StartAccidentector));
                break;

            default:
                break;
        }
    }

    private void StartAccedetectorService(Context context, String action)
    {
        Log.i(TAG, "StartAccedetectorService()");

        if (IS_START_ACTIVITY)
        {
            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class)
                    .putExtra("action", action);
            context.startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(context.getApplicationContext(), AccidenTectorService.class)
                    .putExtra("action", action);
            context.startService(intent);
        }
    }
}
