package com.imaginamos.usuariofinal.taxisya.activities;

import org.json.JSONObject;

import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import cz.msebera.android.httpclient.Header;


public class ReclamoActivity extends Activity implements OnClickListener {


    public ImageView bt_atras, bt_enviar;
    public Button btnSend;
    public String service_id, uuid;
    private ProgressDialog pDialog;
    public EditText reclamo;
    private Conf conf;
    private BroadcastReceiver mReceiver;

    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reclamo);

        bt_atras = (ImageView) findViewById(R.id.btn_volver);

        btnSend = (Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(this);

        reclamo = (EditText) findViewById(R.id.txt_reclamo);

        bt_atras.setOnClickListener(this);

        Bundle reicieveParams = getIntent().getExtras();

        service_id = reicieveParams.getString("service_id");

        conf = new Conf(this);

        uuid = conf.getUuid();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);
        intentFilter.addAction(Actions.CONFIRM_NEW_SERVICES);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Log.v("USER_CLOSE_SESSION", "Sesión cerrada - confirmación");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(ReclamoActivity.this, LoginActivity.class);
                    in3.putExtra("target", 1);
                    startActivity(in3);
                    finish();
                } else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                } else if (intent.getAction().equals(Actions.CONFIRM_NEW_SERVICES)) {
                    Log.v("CONFIRMATION", "onReceive() service_id = " + intent.getExtras().getString("service_id"));
                    Log.v("CONFIRMATION", "onReceive() kind_id = " + intent.getExtras().getString("kind_id"));

                    Intent mIntent = new Intent(getApplicationContext(), ConfirmacionActivity.class);

                    conf.setServiceId(intent.getExtras().getString("service_id"));

                    mIntent.putExtra("driver", intent.getExtras().getString("driver"));
                    mIntent.putExtra("kind_id", intent.getExtras().getString("kind_id"));

                    startActivity(mIntent);
                    finish();

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
        Log.v("onDestroy", "ReclamoActivity");
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_volver:
                finish();
                break;

            case R.id.btnSend:
                complainService(v);
                break;
        }
    }

    public void err_complain() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_net), Toast.LENGTH_SHORT).show();
    }

    public void complainService(View view) {

        MiddleConnect.reclamo(this, uuid, service_id, reclamo.getText().toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog = new ProgressDialog(ReclamoActivity.this);
                pDialog.setMessage(getString(R.string.text_enviando_informacion));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {

                    JSONObject responsejson = new JSONObject(response);

                    if (responsejson != null && response.length() > 0) {
                        if (responsejson.getInt("error") == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.thank_r), Toast.LENGTH_SHORT).show();
                            reclamo.setText("");
                        } else {
                            err_complain();
                        }

                    } else {
                        err_complain();
                    }

                } catch (Exception e) {
                    err_complain();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                err_complain();
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

}
