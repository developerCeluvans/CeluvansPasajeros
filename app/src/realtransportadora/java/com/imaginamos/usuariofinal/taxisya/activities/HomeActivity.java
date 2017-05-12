package com.imaginamos.usuariofinal.taxisya.activities;

import com.crashlytics.android.Crashlytics;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import com.carouseldemo.controls.Carousel;
import com.carouseldemo.controls.CarouselAdapter;
import com.carouseldemo.controls.CarouselAdapter.OnItemClickListener;
import com.carouseldemo.controls.CarouselAdapter.OnItemSelectedListener;
import com.carouseldemo.controls.CarouselItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.BuildConfig;
import com.imaginamos.usuariofinal.taxisya.comm.Preferencias;
import com.imaginamos.usuariofinal.taxisya.dialogs.Dialogos;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.Connectivity;
import com.imaginamos.usuariofinal.taxisya.comm.GooglePushNotification;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.models.Target;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class HomeActivity extends Activity {

    private Animation traslation, mapa_ani;
    private ImageView fondo, nombre;
    private ImageView mapa;
    private HorizontalScrollView viewMap;
    private Carousel carousel;
    private CarouselItem current_item;
    private String pass, uuid, username;
    private Conf conf;
    private AlertDialog.Builder builder;
    private String currentVersionName;
    private ProgressDialog pDialog;
    private Boolean updateAvailable = false;
    private Boolean inUse = false;
    private String id_user, service_id;
    private int status_service = 0;
    private int qualification = 0;
    private BroadcastReceiver mReceiver;

    private double latitud = 0;
    private double longitud = 0;
    private int position2 = 0;
    private Preferencias mPref;


    private static String TAG = "HOMEACTIVITY";

    public static String TYPE_USER = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate", "HomeActivity");

        super.onCreate(savedInstanceState);
        if (BuildConfig.USE_CRASHLYTICS)
            Fabric.with(this, new Crashlytics());

        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.activity_home);

        if (!Utils.checkPlayServices(this)) {
            Toast.makeText(this, getResources().getString(R.string.no_update_services), Toast.LENGTH_SHORT).show();
        }

        conf = new Conf(this);

//        GooglePushNotification googlepush = new GooglePushNotification(this);
//
//        googlepush.getAppVersion(this);
//
//        uuid = Utils.uuid(this);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("HomeActivity", "Refreshed token: " + refreshedToken);
        uuid = refreshedToken;

        conf.setUuid(uuid);

        username = conf.getUser();

        pass = conf.getPass();

        mapa = (ImageView) findViewById(R.id.mapa);

        viewMap = (HorizontalScrollView) findViewById(R.id.mapa_la);

        fondo = (ImageView) findViewById(R.id.fondo);

        nombre = (ImageView) findViewById(R.id.nombre);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_TAXI_ARRIVED);
        intentFilter.addAction(Actions.ACTION_TAXI_GO);
        intentFilter.addAction(Actions.ACTION_CANCEL_OP_SERVICES);
        intentFilter.addAction(Actions.ACTION_CANCEL_DRIVER_SERVICE);
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);
        intentFilter.addAction(Actions.CONFIRM_NEW_SERVICES);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Actions.CONFIRM_NEW_SERVICES)) {
                    Log.v("CONFIRMATION", "onReceive() service_id = " + intent.getExtras().getString("service_id"));
                    Log.v("CONFIRMATION", "onReceive() kind_id = " + intent.getExtras().getString("kind_id"));
                    //try {
                    //JSONObject position = new JSONObject(intent.getExtras().getString("service"));

                    //conf.setServiceId(position.getString("service_id"));
                    //Log.v("CONFIRMATION","onReceive() position = " + position.toString());

                    mPref.setRootActivity("ConfirmacionActivity");

                    Intent mIntent = new Intent(getApplicationContext(), ConfirmacionActivity.class);

                    conf.setServiceId(intent.getExtras().getString("service_id"));

                    mIntent.putExtra("driver", intent.getExtras().getString("driver"));
                    mIntent.putExtra("kind_id", intent.getExtras().getString("kind_id"));

                    startActivity(mIntent);
                    finish();

                    //}
                    //catch (JSONException e) { }

/*
                    try {
                       Log.v("CONFIRMATION","antes de checkService()");

                       checkService();

                       Log.v("CONFIRMATION","luego de checkService()");

                    }
                    catch (JSONException e) { }
                    Log.v("CONFIRMATION","antes de finish()");

                    finish();
*/
                }
                /*
                else if (action.equals(Actions.ACTION_TAXI_ARRIVED)) {
                    //Intent mIntent = new Intent(getApplicationContext(),ConfirmacionActivity.class);
                    mIntent.putExtra("driver", intent.getExtras().getString("driver"));
                    startActivity(mIntent);
                    finish();
                }
                else if(action.equals(Actions.ACTION_CANCEL_DRIVER_SERVICE)
                        || action.equals(Actions.ACTION_CANCEL_OP_SERVICES)) {

                    try {
                       checkService();
                    }
                    catch (JSONException e) { }

                }else if(action.equals(Actions.ACTION_TAXI_GO)) {

                    JSONObject position = new JSONObject(intent.getExtras().getString("service"));
                    Log.v("CONFIRMATION","onReceive() position = " + position.toString());

                    try {
                       checkService();
                    }
                    catch (JSONException e) { }

                }
                */

                else if (action.equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Log.v("USER_CLOSE_SESSION", "Sesión cerrada - confirmación");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    mPref.setRootActivity("LoginActivity");

                    Intent in3 = new Intent(HomeActivity.this, LoginActivity.class);
                    in3.putExtra("target", 1);
                    startActivity(in3);
                    finish();
                } else if (action.equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                }
            }
        };

        try {
            registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {

        }

        if (Connectivity.isConnected(this)) {
            if (conf.getLogin()) {

                MiddleConnect.login(this, username, pass, uuid, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        Utils.log(TAG, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        String response = new String(responseBody);
                        Utils.log(TAG, "response: " + response);
                        Utils.log(TAG, getResources().getString(R.string.error_login));
                    }

                });
            }

        } else {
            new Dialogos(HomeActivity.this, R.string.error_net);
        }

        try {
            setView();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // almacena activity actual
        // Store our shared preference
        mPref = new Preferencias(this);
        mPref.setRootActivity("HomeActivity");

    }

    void mostrarMensaje(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.informacion_importante);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.text_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH", "mensaje: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void setView() throws JSONException {

        viewMap.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        traslation = AnimationUtils.loadAnimation(this, R.anim.pull_out_to_top);

        AnimationSet traslation2 = new AnimationSet(true);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        TranslateAnimation a = new TranslateAnimation(0, 0, nombre.getTop(), -(metrics.heightPixels / 2f - nombre.getHeight()));

        a.setDuration(900);

        traslation2.addAnimation(a);


        mapa_ani = AnimationUtils.loadAnimation(this, R.anim.mapa_main);

        traslation.setFillAfter(true);

        traslation2.setFillAfter(true);

        checkAppVersions();

        mapa.setAnimation(mapa_ani);

        nombre.setAnimation(traslation2);

        fondo.setAnimation(traslation);

        carousel = (Carousel) findViewById(R.id.carouseles);

        carousel.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(CarouselAdapter<?> parent, final View view, final int position, long id) {
                Log.v("Carousel", "onItemClick() position " + String.valueOf(position));
                Log.v("Carousel", "onItemClick() carousel item position " + String.valueOf(carousel.getSelectedItemPosition()));
                int selectedId = carousel.getSelectedItemPosition();
                position2 = position;
                if (position2 != selectedId)
                    position2 = selectedId;

                if (carousel.getSelectedItemPosition() == position2) {
                    Log.v("Carousel", "carousel.getSelectedItemPosition() == position");
                    final Animation fade_out_item = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.fade_out_item);

                    final CarouselItem item = (CarouselItem) parent.getChildAt(position2);

                    item.getImage().setAnimation(fade_out_item);

                    fade_out_item.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationEnd(Animation arg0) {

                            current_item = item;

                            item.setVisibility(View.INVISIBLE);

                            switch (position2) {

                                case 0:
                                    Log.v("Carousel", "position 0");

                                    if (conf.getLogin()) {

                                        mPref.setRootActivity("HomeActivity");

                                        Intent in2 = new Intent(HomeActivity.this, AgendarActivity.class);
                                        startActivity(in2);

                                    } else {
                                        mPref.setRootActivity("HomeActivity");

                                        Intent in = new Intent(HomeActivity.this, RegistroActivity.class);
                                        in.putExtra("target", Target.AGEND_TARGET);
                                        startActivity(in);
                                    }
                                    break;

                                case 1:
                                    Log.v("Carousel", "position 1");
                                    if (updateAvailable)
                                        showDialog();
                                    else {
                                        mPref.setRootActivity("HomeActivity");
                                        Intent in3 = new Intent(HomeActivity.this, MapaActivitys.class);
                                        startActivity(in3);                                    //}
                                    }
                                    break;

                                case 2:
                                    Log.v("Carousel", "position 2");
                                    if (updateAvailable)
                                        showDialog();
                                    else {
                                        if (conf.getLogin()) {
                                            mPref.setRootActivity("HomeActivity");

                                            Intent in = new Intent(HomeActivity.this, HistorialActivity.class);
                                            startActivity(in);

                                        } else {
                                            mPref.setRootActivity("HomeActivity");

                                            Intent in = new Intent(HomeActivity.this, RegistroActivity.class);
                                            in.putExtra("target", Target.HISTORY_TARGET);
                                            startActivity(in);

                                        }
                                    }
                                    break;

                                case 3:
                                    Log.v("Carousel", "position 3");
                                    if (updateAvailable)
                                        showDialog();
                                    else {
                                        if (conf.getLogin()) {
                                            mPref.setRootActivity("HomeActivity");

                                            Intent in = new Intent(HomeActivity.this, PerfilActivity.class);
                                            startActivity(in);
                                        } else {
                                            mPref.setRootActivity("HomeActivity");

                                            Intent in = new Intent(HomeActivity.this, RegistroActivity.class);
                                            in.putExtra("target", Target.HISTORY_TARGET);
                                            startActivity(in);
                                        }
                                    }
                                    break;
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                        }

                        @Override
                        public void onAnimationStart(Animation arg0) {
                        }

                    });
                } else {
                    Log.v("Carousel", "<> position");
                    carousel.setSelection(position2, true);
                }

            }

        });

        carousel.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(CarouselAdapter<?> parent, View view, int position, long id) {
                Log.v("Carousel", "onItemSelectedListener() = try");
                try {

                    CarouselItem item = (CarouselItem) parent.getChildAt(position);

                    item.setOverItem();

                    for (int i = 0; i <= 3; i++) {
                        if (i != position) {
                            Log.v("Carousel", "item position " + String.valueOf(i));
                            item = (CarouselItem) parent.getChildAt(i);
                            item.setNormalItem();
                        }

                    }
                } catch (Exception e) {

                }
            }

            public void onNothingSelected(CarouselAdapter<?> parent) {
                Log.v("Carousel", "onItemSelectedListener() onNothingSelected");


            }

        });

        // si esta logueado chequea servicio
        Log.v("checkService", "antes de conf.getLogin()");
        if (conf.getLogin()) {
            Log.v("checkService", "luego de conf.getLogin()");
            checkService();
        }


    }

    private void checkAppVersions() {

        MiddleConnect.getAppVersions(this, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                try {
                    pDialog = new ProgressDialog(HomeActivity.this);
                    pDialog.setMessage(getString(R.string.verificando));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                } catch (Exception e) {
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.e(TAG, "" + response);

                try {
                    JSONObject repsonsejson = new JSONObject(response);
                    Log.d(TAG, "appVersions = " + repsonsejson.toString());
                    if (repsonsejson.has("userVersions")) {
                        JSONArray arrayApp = repsonsejson.getJSONArray("userVersions");
                        Float remote = 0.0f;
                        for (int i = 0; i < arrayApp.length(); i++) {
                            if (arrayApp.getJSONObject(i).getString("device_type").equals("Android")) {
                                remote = Float.parseFloat(arrayApp.getJSONObject(i).getString("version"));
                            }
                        }

                        // get versión app instalada
                        PackageInfo pckginfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
                        currentVersionName = pckginfo.versionName;
                        float current = Float.valueOf(currentVersionName);
                        Log.d(TAG, "current = " + String.valueOf(current) + " remote = " + remote);
                        if (current < remote) {
                            Log.d(TAG, "la versión instalada esta desactualizada");
                            updateAvailable = true;

                            // builder = new AlertDialog.Builder(HomeActivity.this);
                            //    builder.setMessage(getString(R.string.message_app_version))
                            //            .setTitle(getString(R.string.title_app_version))
                            //            .setPositiveButton(getString(R.string.button_app_version), new DialogInterface.OnClickListener() {
                            //                public void onClick(DialogInterface dialog, int id) {
                            //                    dialog.cancel();
                            //                    final String appPackageName = getApplicationContext().getPackageName();
                            //                    try {
                            //                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            //                    }
                            //                    catch (android.content.ActivityNotFoundException anfe) {
                            //                         startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            //                    }
                            //                }
                            //            });

                            //    builder.create();

                            //    builder.show();

                        } else {
                            Log.d(TAG, "la versión instalada es la última");
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e(TAG, "" + response);

            }

            @Override
            public void onFinish() {
                try {
                    pDialog.dismiss();
                } catch (Exception e) {
                }
            }
        });

    }

    private void showDialog() {
        builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage(getString(R.string.message_app_version))
                .setTitle(getString(R.string.title_app_version))
                .setPositiveButton(getString(R.string.button_app_version), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        final String appPackageName = getApplicationContext().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });

        builder.create();

        builder.show();
    }

    public boolean checkService() throws JSONException {

        //service_id = conf.getServiceId();
        id_user = conf.getIdUser();

        service_id = null;
        Log.v("checkService", "ini");
        Log.v("checkService", "id_user=" + id_user + " service_id=" + service_id);

        MiddleConnect.checkStatusService(this, id_user, service_id, "uuid", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.v("checkService", "onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    //Log.v("checkService", "SUCCES: "+response);
                    JSONObject responsejson = new JSONObject(response);
                    //if (responsejson.getInt("status_id"))
                    //{
                    status_service = responsejson.getInt("status_id");

                    Log.v("checkService", "status_id: " + String.valueOf(status_service));
                    // si hay un servicio asignado lo recupera
                    if ((status_service == 2) || (status_service == 4) || (status_service == 5)) {
                        service_id = responsejson.getString("id");
                        conf.setServiceId(service_id);

                        Log.v("HomeActivity", "checkService() servicio recuperado - status_service " + String.valueOf(status_service) + " service_id=" + service_id);
                        Log.v("HomeActivity", "checkService() servicio recuperado - driver " + responsejson.getJSONObject("driver").toString());

                        Log.v("CNF_SRV1", "HomeActivity before call ConfirmacionActivity.class");

                        mPref.setRootActivity("HomeActivity");

                        Intent mIntent = new Intent(getApplicationContext(), ConfirmacionActivity.class);
                        mIntent.putExtra("driver", responsejson.getJSONObject("driver").toString());
                        //if (responsejson.isNull("qualification")) {
                        if (status_service == 5) {
                            mIntent.putExtra("qualification", "1");
                        } else {
                            mIntent.putExtra("qualification", "0");
                        }
                        startActivity(mIntent);
                    }
                                /*
                                if ((status_service == 2) || (status_service == 4)) {
                                    Log.v("HomeActivity", "checkService() servicio asignado recuperado");
                                    Log.v("HomeActivity", "responsejson = " + responsejson.toString());
                                    Log.v("HomeActivity", "responsejson = " + responsejson.getJSONObject("driver").toString());
                                    //Log.v("HomeActivity", "responsejson = " + responsejson.getJSONArray("driver").toString());
                                    service_id = responsejson.getString("id");
                                    conf.setServiceId(service_id);

                                    Intent mIntent = new Intent(getApplicationContext(),ConfirmacionActivity.class);
									mIntent.putExtra("driver", responsejson.getJSONObject("driver").toString());
                                    mIntent.putExtra("qualification", "0");
									startActivity(mIntent);
									//finish();
		                        }
                                else if (status_service == 5) {
                                    if (responsejson.isNull("qualification")) {
                                        Log.v("HomeActivity", "checkService() servicio asignado recuperado sin activity_calificar");

                                        service_id = responsejson.getString("id");
                                        conf.setServiceId(service_id);

                                        Intent mIntent = new Intent(getApplicationContext(),ConfirmacionActivity.class);
                                        mIntent.putExtra("driver", responsejson.getJSONObject("driver").toString());
                                        mIntent.putExtra("qualification", "1");
                                        startActivity(mIntent);
                                    }
                                }
                                */
                    else {
                        Log.v("HomeActivity", "checkService() no tenia servicio para recuperar");
                        Log.v("HomeActivity", "responsejson = " + responsejson.getJSONObject("driver").toString());
                    }
                    //}
                } catch (JSONException e) {
                    Log.v("checkService", "Problema json " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.v("checkService", "onFailure = " + response);

            }

            @Override
            public void onFinish() {

                Log.v("checkService", "onFinish");

            }

        });
        if (status_service == 2)
            return true;
        else
            return false;

    }


    // checkServiceInCurse
    public boolean checkServiceInOtherDevice() throws JSONException {

        //service_id = conf.getServiceId();
        id_user = conf.getIdUser();

        service_id = null;
        Log.v("checkServiceInOtherDev", "id_user=" + id_user + " service_id=" + service_id);

        MiddleConnect.checkStatusService(HomeActivity.this, id_user, service_id, "uuid", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.v("checkServiceInOtherDev", "onStart");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    //Log.v("checkService", "SUCCES: "+response);
                    JSONObject responsejson = new JSONObject(response);
                    //if (responsejson.getInt("status_id"))
                    //{
                    status_service = responsejson.getInt("status_id");

                    Log.v("checkService", "status_id: " + String.valueOf(status_service));


                    // si hay un servicio asignado lo recupera
                    if (status_service == 2) {
                        Log.v("HomeActivity", "checkServiceInOtherDevice() servicio asignado recuperado");
                        Log.v("HomeActivity", "responsejson = " + responsejson.toString());
                        Log.v("HomeActivity", "responsejson = " + responsejson.getJSONObject("driver").toString());

                        String uuid_service = responsejson.getJSONObject("user").getString("uuid");

                        if (!uuid_service.equals(uuid)) {
                            // log mostrar aviso que hay un servicio en otro dispositivo con el mismo user_id
                            Toast.makeText(HomeActivity.this, "Hay un servicio en curso en otro dispositivo", Toast.LENGTH_SHORT).show();

                            inUse = true;
                        } else inUse = false;
                    }

                } catch (JSONException e) {
                    Log.v("checkServiceInOtherDev", "Problema json " + e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.v("checkServiceInOtherDev", "onFailure = " + response);

            }

            @Override
            public void onFinish() {

                Log.v("checkServiceInOtherDev", "onFinish");

            }

        });

        return inUse;

    }

    @Override
    protected void onDestroy() {
        Log.v("onDestroy", "HomeActivity");
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onStop() {
        Log.v("onStop", "HomeActivity");
        super.onStop();
    }

    @Override
    public void onRestart() {

        super.onRestart();

        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);

        if (current_item != null) {
            current_item.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
