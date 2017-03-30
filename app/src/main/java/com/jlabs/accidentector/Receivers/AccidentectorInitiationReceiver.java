package com.jlabs.accidentector.Receivers;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import com.jlabs.accidentector.R;
import com.jlabs.accidentector.Services.AccidenTectionService;

/*Summary:
 *  This class Listens to broadcasts and starts the AccidentorService
 *  in case one of the following broadcasts are received:
 *  1. RECEIVE_BOOT_COMPLETED
 *  2. com.jlabs.accidentector.START_DETECTION_MANUAL
 */

public class AccidentectorInitiationReceiver extends BroadcastReceiver {

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
            case ACTION_BOOT_COMPLETED:
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
        Intent intent = new Intent(context, AccidenTectionService.class)
                                  .putExtra("action", action);
        context.startService(intent);
    }
}
