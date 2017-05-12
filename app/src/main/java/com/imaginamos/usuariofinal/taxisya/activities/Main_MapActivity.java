package com.imaginamos.usuariofinal.taxisya.activities;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaginamos.usuariofinal.taxisya.comm.Connectivity;
import com.imaginamos.usuariofinal.taxisya.comm.NetworkChangeReceiver;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.R;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RelativeLayout;

//public class Main_MapActivity extends FragmentActivity implements OnClickListener {
public class Main_MapActivity extends Activity implements OnClickListener, NetworkChangeReceiver.NetworkReceiverListener,
        Connectivity.ConnectivityQualityCheckListener, OnMapReadyCallback {


    //private ImageView ver_taxista;
    private Button btnVerTaxista;

    private GoogleMap map;
    private ArrayList<LatLng> markerPoints;
    private double latitud, longitud;
    private BroadcastReceiver mReceiver;
    private MarkerOptions options = new MarkerOptions();
    private Marker marker;
    private RelativeLayout mNoConnectivityPanel;
    private  LatLng mTaxi;

    private NetworkChangeReceiver mNetworkMonitor;

    private Connectivity connectivityChecker = new Connectivity(this);


    @Override
    public void onRestart() {

        super.onRestart();

        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);

    }

    @Override
    protected void onResume() {

        super.onResume();
        displayConnectivityPanel(!Connectivity.isConnected(this) && !connectivityChecker.getConnectivityCheckResult());
        connectivityChecker.startConnectivityMonitor();
        mNetworkMonitor = new NetworkChangeReceiver(this);
        registerReceiver(mNetworkMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityChecker.stopConnectivityMonitor();
        unregisterReceiver(mNetworkMonitor);
    }

    @Override
    protected void onDestroy() {

        Log.v("onDestroy", "Main_MapActivity");
        super.onDestroy();

    }

    private void toFinish() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapa_taxista);

        //ver_taxista = (ImageView) findViewById(R.id.ver_taxista);

        btnVerTaxista = (Button) findViewById(R.id.btnVerTaxista);

        mNoConnectivityPanel = (RelativeLayout) findViewById(R.id.layout_no_connectivity);

        //ver_taxista.setOnClickListener(this);

        btnVerTaxista.setOnClickListener(this);

        Bundle reicieveParams = getIntent().getExtras();

        latitud = reicieveParams.getDouble("latitud");

        longitud = reicieveParams.getDouble("longitud");

//		SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //map = fm.getMap();
        fm.getMapAsync(this);

//        map.setMyLocationEnabled(true);

        markerPoints = new ArrayList<LatLng>();

        mTaxi = new LatLng(latitud, longitud);

        markerPoints.add(mTaxi);

        options.position(mTaxi);

        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.taxista_carrito));

//        marker = map.addMarker(options);

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mTaxi, 17.0f));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_TAXI_ARRIVED);
        intentFilter.addAction(Actions.ACTION_TAXI_GO);
        intentFilter.addAction(Actions.ACTION_CANCEL_OP_SERVICES);
        intentFilter.addAction(Actions.ACTION_CANCEL_DRIVER_SERVICE);
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);
        //intentFilter.addAction(Actions.CONFIRM_NEW_SERVICES);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(Actions.ACTION_TAXI_ARRIVED) || action.equals(Actions.ACTION_CANCEL_DRIVER_SERVICE)
                        || action.equals(Actions.ACTION_CANCEL_OP_SERVICES)) {
                    toFinish();

                } else if (action.equals(Actions.ACTION_TAXI_GO)) {
                    try {
                        JSONObject position = new JSONObject(intent.getExtras().getString("service"));
                        latitud = position.getDouble("lat");
                        longitud = position.getDouble("lng");
                        dibujarTaxi(latitud, longitud);

                    } catch (Exception e) {
                        Log.e("ERROR", e.toString() + "");
                    }
                } else if (action.equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Log.v("USER_CLOSE_SESSION", "Sesión cerrada");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(Main_MapActivity.this, LoginActivity.class);
                    in3.putExtra("target", 1);
                    startActivity(in3);
                    finish();

                } else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                }
                /*
				else if (intent.getAction().equals(Actions.CONFIRM_NEW_SERVICES)) {
                    Log.v("CONFIRMATION","onReceive() service_id = " + intent.getExtras().getString("service_id"));
                    Log.v("CONFIRMATION","onReceive() kind_id = " + intent.getExtras().getString("kind_id"));

                    Intent mIntent = new Intent(getApplicationContext(),ConfirmacionActivity.class);

                    conf.setServiceId(intent.getExtras().getString("service_id"));

                    mIntent.putExtra("driver", intent.getExtras().getString("driver"));
                    mIntent.putExtra("kind_id", intent.getExtras().getString("kind_id"));

                    startActivity(mIntent);
                    finish();

                }
                */


            }
        };


        registerReceiver(mReceiver, intentFilter);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        map.setMyLocationEnabled(true);

        marker = map.addMarker(options);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mTaxi, 17.0f));


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

    public void dibujarTaxi(double latitud, double longitud) {
        markerPoints = new ArrayList<LatLng>();
        LatLng taxi = new LatLng(latitud, longitud);
        marker.setPosition(taxi);
        map.animateCamera(CameraUpdateFactory.newLatLng(taxi));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("Main_MapActivity1", "onKeyDown");

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            toFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void err_driver() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onClick(View v) {
        Log.v("Main_MapActivity1", "onClick");
        toFinish();
    }

    public void onBackPressed() {
        Log.v("Main_MapActivity1", "onBackPressed");
    }


    private void displayConnectivityPanel(boolean display) {
        mNoConnectivityPanel.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onNetworkConnectivityChange(boolean connected) {
        displayConnectivityPanel(!connected);
    }

    @Override
    public void onConnectivityQualityChecked(boolean Optimal) {
        displayConnectivityPanel(!Optimal);
    }
}
