package com.imaginamos.usuariofinal.taxisya.comm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.imaginamos.usuariofinal.taxisya.BuildConfig;
import com.imaginamos.usuariofinal.taxisya.comm.GcmIntentServices;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean usePush = false;
        if (BuildConfig.USE_PUSH) {
            // Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentServices.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
            usePush = true;
        }
        Log.e("SERVICE_CMS", "GCM BROADCAST_RECEIVER PUSH "  + (usePush ? "ENABLED" : "DISABLED - DEBUG"));
    }
}
