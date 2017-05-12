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
import android.os.Handler;
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent (HomeActivity.this,MapaActivitys.class);
                startActivity(intent);

            }
        },2000);


    }
}
