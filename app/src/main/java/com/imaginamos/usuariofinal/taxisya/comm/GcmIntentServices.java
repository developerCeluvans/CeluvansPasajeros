package com.imaginamos.usuariofinal.taxisya.comm;

import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.imaginamos.usuariofinal.taxisya.activities.ConfirmacionActivity;
import com.imaginamos.usuariofinal.taxisya.activities.NotificationActivity;
import com.imaginamos.usuariofinal.taxisya.adapter.BDAdapter;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import java.util.Calendar;

public class GcmIntentServices extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private String mServiceId;
    private int mStatusOld;
    private int mStatusNew;

    private BDAdapter mySQLiteAdapter;
    private Cursor mCursor;

    public GcmIntentServices() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        for (String key : intent.getExtras().keySet()) {
            Object value = intent.getExtras().get(key);
            Log.e("push", String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
        }

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                Log.e("PUSH", "Received: " + extras.toString());
                Log.e("CNF_SRV", "GcmIntentService Received: " + extras.toString());
                Log.e("SERVICE_CMS","GCM");
                String message = null;

                try {

                    message = intent.getExtras().getString("message");
                    Log.v("PUSH2", "message = " + message);


                    // open sqlite
                    mySQLiteAdapter = new BDAdapter(this);
                    mySQLiteAdapter.openToWrite();


                    final JSONObject extra = new JSONObject(intent.getExtras().getString("extra"));
                    Log.e("CNF_SRV", "GcmIntentService extra: " + extra.toString());

                    if (extra.has("push_type")) {
                        Log.e("CNF_SRV", "push_type = " + String.valueOf(extra.getInt("push_type")));
                        Log.e("SERVICE_CMS","GCM type " + String.valueOf(extra.getInt("push_type")));



                        switch (extra.getInt("push_type")) {

                            case Actions.CONFIRM_SERVICE:
                                Log.e("CNF_SRV", "GcmIntentService service_id= " + extra.getString("service_id") + " PUSH CONFIRM_SERVICE");
                                Log.e("SERVICE_CMS", "GCM CONFIRM_SERVICE service_id= " + extra.getString("service_id"));

                                // verifica si el servicio con el estado ya fue procesado
                                mServiceId = extra.getString("service_id");
                                Log.v("SQLITE", "PUSH CONFIRM_SERVICE service_id " + mServiceId);

                                boolean changeStatus = false;

                                mCursor = mySQLiteAdapter.filterService(mServiceId);
                                if (mCursor.getCount() > 0) {
                                    mCursor.moveToPosition(0);
                                    mStatusOld = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(BDAdapter.SRV_STA)));
                                    mStatusNew = Integer.valueOf(extra.getString("status_id"));
                                    Log.e("SERVICE_CMS", "GCM CONFIRM_SERVICE SQLITE service_id " + mServiceId + " old_status " + String.valueOf(mStatusOld));
                                    Log.e("SERVICE_CMS", "GCM CONFIRM_SERVICE SQLITE service_id " + mServiceId + " new status " + String.valueOf(mStatusNew));

                                    mCursor.close();

                                    if (mStatusNew > mStatusOld) {
                                        mySQLiteAdapter.updateStatusService(mServiceId, String.valueOf(mStatusNew));
                                        changeStatus = true;
                                    }
                                } else {
                                    Log.v("SQLITE", "PUSH CONFIRM_SERVICE create service_id " + mServiceId);
                                    Log.e("SERVICE_CMS", "GCM CONFIRM_SERVICE create service_id= " + mServiceId);

                                    final Calendar c = Calendar.getInstance();
                                    long actualDate = c.getTimeInMillis();
                                    mySQLiteAdapter.insertService(mServiceId, "2", "", "", extra.getString("driver"), actualDate);
                                    changeStatus = true;
                                }

                                if (changeStatus) {
                                    if (extra.getString("kind_id").equals("2")) {
                                        Intent myintent = new Intent(Actions.CONFIRM_NEW_SERVICES);
                                        myintent.putExtra("driver", extra.getString("driver"));
                                        myintent.putExtra("service_id", extra.getString("service_id"));
                                        myintent.putExtra("kind_id", extra.getString("kind_id"));

                                        sendBroadcast(myintent);
                                        sendNotificationConfirm(message, extra.getString("driver"));

                                    } else {
                                        Intent myintent = new Intent(Actions.CONFIRM_NEW_SERVICES);
                                        myintent.putExtra("driver", extra.getString("driver"));
                                        myintent.putExtra("service_id", extra.getString("service_id"));
                                        myintent.putExtra("kind_id", "0");

                                        sendBroadcast(myintent);
                                        sendNotification(message);
                                    }
                                }
                                break;

                            case Actions.TAXI_ARRIVED:

                                mServiceId = extra.getString("service_id");
                                Log.v("SQLITE", "PUSH TAXI_ARRIVED service_id " + mServiceId);
                                Log.e("SERVICE_CMS", "GCM TAXI_ARRIVED service_id= " + mServiceId);

                                mCursor = mySQLiteAdapter.filterService(mServiceId);
                                if (mCursor.getCount() > 0) {
                                    mCursor.moveToPosition(0);
                                    mStatusOld = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(BDAdapter.SRV_STA)));
                                    mStatusNew = Integer.valueOf(extra.getString("status_id"));

                                    Log.v("SQLITE", "PUSH CONFIRM_SERVICE 1 status " + String.valueOf(mStatusOld));
                                    Log.v("SQLITE", "PUSH CONFIRM_SERVICE 2 status " + String.valueOf(mStatusNew));

                                    Log.e("SERVICE_CMS", "GCM TAXI_ARRIVED SQLITE service_id " + mServiceId + " old_status " + String.valueOf(mStatusOld));
                                    Log.e("SERVICE_CMS", "GCM TAXI_ARRIVED SQLITE service_id " + mServiceId + " new status " + String.valueOf(mStatusNew));

                                    mCursor.close();

                                    if (mStatusNew > mStatusOld) {
                                        // almacena el nuevo estado
                                        mySQLiteAdapter.updateStatusService(mServiceId, String.valueOf(mStatusNew));

                                        Log.e("CNF_SRV", "TAXI_ARRIVED");
                                        Log.e("CNF_SRV", "GcmIntentService service_id= " + extra.getString("service_id") + " PUSH TAXI_ARRIVED " + message);
                                        Log.e("SERVICE_CMS", "GCM TAXI_ARRIVED change status service_id= " + extra.getString("service_id") + " " + message);

                                        Intent i = new Intent(Actions.ACTION_TAXI_ARRIVED);
                                        i.putExtra("service_id", extra.getString("service_id"));
                                        i.putExtra("message", message);
                                        sendBroadcast(i);
                                        sendNotification(message);
                                    }
                                }
                                break;

                            case Actions.SERVICE_ENDED:
                                Log.e("CNF_SRV", "PUSJ SERVICE_ENDEDi service_id= " + extra.getString("service_id"));
                                Log.e("SERVICE_CMS", "GCM SERVICE_ENDED service_id " + mServiceId + " old_status " + String.valueOf(mStatusOld));

                                mCursor = mySQLiteAdapter.filterService(mServiceId);
                                if (mCursor.getCount() > 0) {
                                    mCursor.moveToPosition(0);
                                    mStatusOld = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(BDAdapter.SRV_STA)));
                                    mStatusNew = Integer.valueOf(extra.getString("status_id"));

                                    Log.v("SQLITE", "PUSH SERVICE ENDED 1 status " + String.valueOf(mStatusOld));
                                    Log.v("SQLITE", "PUSH SERVICE_ENDED 2 status " + String.valueOf(mStatusNew));

                                    Log.e("SERVICE_CMS", "GCM SERVICE_ENDED SQLITE service_id " + mServiceId + " old_status " + String.valueOf(mStatusOld));
                                    Log.e("SERVICE_CMS", "GCM SERVICE_ENDED SQLITE service_id " + mServiceId + " new status " + String.valueOf(mStatusNew));


                                    mCursor.close();

                                    if (mStatusNew > mStatusOld) {
                                        Log.e("SERVICE_CMS", "GCM SERVICE_ENDED change status service_id= " + extra.getString("service_id") + message);

                                        Intent si = new Intent(Actions.ACTION_SERVICE_ENDED);
                                        si.putExtra("service_id", extra.getString("service_id"));
                                        si.putExtra("pay_type", extra.getString("pay_type"));
                                        si.putExtra("units", extra.getString("units"));
                                        si.putExtra("charge1", extra.getString("charge1"));
                                        si.putExtra("charge2", extra.getString("charge2"));
                                        si.putExtra("charge3", extra.getString("charge3"));
                                        si.putExtra("charge4", extra.getString("charge4"));
                                        si.putExtra("value", extra.getString("value"));

                                        si.putExtra("message", message);
                                        sendBroadcast(si);
                                        sendNotification(message);
                                    }
                                }
                                break;

                            case Actions.TAXI_GO:
                                //Log.e("CNF_SRV", "PUSH TAXI_GO service_id= " + extra.getString("service_id"));
                                Log.e("SERVICE_CMS", "GCM TAXI_GO  " + extra.getString("service"));
                                // Log.e("SERVICE_CMS", "GCM TAXI_GO service_id= " + extra.getString("service_id"));
                                // mStatusOld = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(BDAdapter.SRV_STA)));
                                // mStatusNew = Integer.valueOf(extra.getString("status_id"));
                                // Log.v("SQLITE", "PUSH TAXI_GO 1 status " + String.valueOf(mStatusOld));
                                // Log.v("SQLITE", "PUSH TAXI_GO 2 status " + String.valueOf(mStatusNew));

                                Intent go = new Intent(Actions.ACTION_TAXI_GO);
                                go.putExtra("service", extra.getString("service"));
                                //go.putExtra("message", message);
                                sendBroadcast(go);
                                //sendNotification(message);
                                break;

                            case Actions.DRIVER_CANCEL_SERVICE:
                                Log.v("SERVICE_STATUS", "GcmIntentService service_id= " + extra.getString("service_id") + " PUSH DRIVER_CANCEL_SERVICE");
                                Log.e("SERVICE_CMS", "GCM DRIVER_CANCEL_SERVICE service_id= " + extra.getString("service_id"));


                                mCursor = mySQLiteAdapter.filterService(mServiceId);
                                if (mCursor.getCount() > 0) {
                                    mCursor.moveToPosition(0);
                                    mStatusOld = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(BDAdapter.SRV_STA)));
                                    mStatusNew = Integer.valueOf(extra.getString("status_id"));

                                    Log.v("SERVICE_STATUS", "PUSH DRIVER_CANCEL_SERVICE 1 status " + String.valueOf(mStatusOld));
                                    Log.v("SERVICE_STATUS", "PUSH DRIVER_CANCEL_SERVICE 2 status " + String.valueOf(mStatusNew));

                                    mCursor.close();

                                    if (mStatusNew > mStatusOld) {
                                        Intent mi = new Intent(Actions.ACTION_CANCEL_DRIVER_SERVICE);
                                        mi.putExtra("message", message);
                                        mi.putExtra("service_id", extra.getString("service_id"));

                                        sendBroadcast(mi);
                                        sendNotification(message);
                                    }
                                }
                                break;


                            case Actions.OP_CANCEL_SERVICE:
                                Log.v("SERVICE_STATUS", "GcmIntentService service_id= " + extra.getString("service_id") + " PUSH OP_CANCEL_SERVICE");

                                mCursor = mySQLiteAdapter.filterService(mServiceId);
                                if (mCursor.getCount() > 0) {
                                    mCursor.moveToPosition(0);
                                    mStatusOld = Integer.valueOf(mCursor.getString(mCursor.getColumnIndex(BDAdapter.SRV_STA)));
                                    mStatusNew = Integer.valueOf(extra.getString("status_id"));

                                    Log.v("SERVICE_STATUS", "PUSH OP_CANCEL_SERVICE 1 status " + String.valueOf(mStatusOld));
                                    Log.v("SERVICE_STATUS", "PUSH OP_CANCEL_SERVICE 2 status " + String.valueOf(mStatusNew));

                                    mCursor.close();

                                    if (mStatusNew > mStatusOld) {
                                        Intent mis = new Intent(Actions.ACTION_CANCEL_OP_SERVICES);
                                        mis.putExtra("service_id", extra.getString("service_id"));
                                        sendBroadcast(mis);
                                        sendNotification(message);
                                    }
                                }
                                break;

                            case Actions.MESSAGE_MASSIVE:
                                Log.e("CNF_SRV", "GcmIntentService message=" + message + " PUSH MESSAGE_MASSIVE");
                                Log.e("PUSH", "Mensaje masivo");
                                Log.e("PUSH", message);
                                Intent im = new Intent(Actions.ACTION_MESSAGE_MASSIVE);
                                im.putExtra("message", message);
                                sendBroadcast(im);
                                sendNotification(message);
                                //muestraMessage(message);
                                break;

                            case Actions.USER_CLOSE_SESSION:
                                Log.e("CNF_SRV", "GcmIntentService PUSH USER_CLOSE_SESSION");
                                Intent ic = new Intent(Actions.ACTION_USER_CLOSE_SESSION);
                                Conf conf = new Conf(getApplicationContext());
                                conf.setLogin(false);

                                sendBroadcast(ic);
                                sendNotification("Sesión cerrada por otro dispositivo");

                                Log.e("PUSH", "Sesíón: cerrada por otro dispositivo");

                                break;

                        }
                        ;
                    } else {
                        sendNotification(message);
                    }

                    // close sqlite
                    if (mySQLiteAdapter != null) {
                        mySQLiteAdapter.close();
                    }


                } catch (Exception e) {
                    Log.e("PUSH", "ERROR: " + e.toString());

                    if (message != null) {
                        sendNotification(message);
                    }
                }

            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void muestraMessage(final String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Información importante");
        builder.setMessage(message);
        builder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH", "mensaje: " + message);
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        //builder.create();
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();

    }

    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NotificationActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.audio2), 1);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    private void sendNotificationConfirm(String msg, String extra) {

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

//        Intent intent = new Intent(this, ConfirmacionActivity.class);
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("driver", extra);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.audio2), 1);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}
