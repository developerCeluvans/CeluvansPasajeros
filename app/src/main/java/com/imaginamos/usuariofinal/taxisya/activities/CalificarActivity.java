package com.imaginamos.usuariofinal.taxisya.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.adapter.BDAdapter;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.comm.Preferencias;
import com.imaginamos.usuariofinal.taxisya.io.ApiAdapter;
import com.imaginamos.usuariofinal.taxisya.io.ServiceResponse;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CalificarActivity extends Activity implements OnClickListener {

    private String mIdUser, mUuid, mServiceId;
    private Button mBtnMuyBueno, mBtnBueno, mBtnMalo;


    private String mScore;
    private Conf mConf;
    private BroadcastReceiver mReceiver;

    private BDAdapter mySQLiteAdapter;
    private Preferencias mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);

        mBtnMuyBueno = (Button) findViewById(R.id.btnMuyBueno);
        mBtnBueno = (Button) findViewById(R.id.btnBueno);
        mBtnMalo = (Button) findViewById(R.id.btnMalo);

        mBtnMuyBueno.setOnClickListener(this);
        mBtnBueno.setOnClickListener(this);
        mBtnMalo.setOnClickListener(this);

        mConf = new Conf(this);

        mIdUser = mConf.getIdUser();
        mUuid = mConf.getUuid();
        mServiceId = mConf.getServiceId();

        mySQLiteAdapter = new BDAdapter(this);
        mySQLiteAdapter.openToWrite();

        mPref = new Preferencias(this);

        Log.v("Confimacion", "id_user=" + mIdUser);
        Log.v("Confimacion", "uuid" + mUuid);
        Log.v("Confimacion", "service_id=" + mServiceId);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(CalificarActivity.this, LoginActivity.class);
                    in3.putExtra("target", 1);
                    startActivity(in3);
                    finish();
                } else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);
                }
            }
        };

        try {
            registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {

        }
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

    @Override
    protected void onDestroy() {
        Log.v("onDestroy", "CalificarActivity");
        super.onDestroy();

        if (mySQLiteAdapter != null) {
            mySQLiteAdapter.close();
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("BACK_PRESS","onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.v("BACK_PRESS","onKeyDown Back");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.v("BACK_PRESS","onBackPressed");
        return;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMuyBueno:
                mScore = "1";
                break;

            case R.id.btnBueno:
                mScore = "2";
                break;

            case R.id.btnMalo:
                mScore = "3";
                final CharSequence[] items = {"Cobro injusto", "Vehiculo en mal estado", "Conductor irrespetuoso"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Razon:");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Haz elegido la opcion: " + items[item] , Toast.LENGTH_SHORT);
                        toast.show();
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;

        }

        MiddleConnect.calificar(this, mUuid, mIdUser, mServiceId, mScore, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                        Log.v("CALIFICAR1", "onStart ");
                    }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                Log.v("CALIFICAR1", "onSuccess " + s);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody);
                Log.v("CALIFICAR1", "onFailure " + s);
                errorCalification();
            }

            @Override
            public void onFinish() {
                Log.v("CALIFICAR1", "onFinish ");
                closeCalification();
            }
        });

    }

    public void closeCalification() {

        mPref.setRootActivity("MapaActivitys");
        Intent i = new Intent(CalificarActivity.this, MapaActivitys.class);
        startActivity(i);
        finish();
    }

    public void errorCalification() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_net), Toast.LENGTH_SHORT).show();
    }

}
