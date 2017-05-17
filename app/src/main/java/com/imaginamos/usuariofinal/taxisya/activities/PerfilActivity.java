package com.imaginamos.usuariofinal.taxisya.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.models.Target;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class PerfilActivity extends Activity implements OnClickListener {

    private ImageView mBack;

    private Button mBtnActualizar, mBtnClose, mBtnPagos;

    private EditText mEdtName, mEdtUser, mEdtPassword, mEdtCellphone;

    private String mUser, mPassword, mName, mCellphone, mUuid;

    private int target_option = 0;

    private ProgressDialog pDialog;

    private Conf mConf;

    private String index;

    private String comp1;

    private String comp2;

    private String no;

    private String barrio;

    private String obs;

    private String latitud;

    private String longitud;

    private BroadcastReceiver mReceiver;

    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.activity_perfil);

        mUuid = Utils.uuid(this);

        mConf = new Conf(this);

        mBtnActualizar = (Button) findViewById(R.id.btnActualizar);
        mBtnActualizar.setOnClickListener(this);

        mBtnClose = (Button) findViewById(R.id.btnClose);
        mBtnClose.setOnClickListener(this);

        mBtnPagos = (Button) findViewById(R.id.btnPayments);
        mBtnPagos.setOnClickListener(this);

        mBack = (ImageView) findViewById(R.id.btn_volver);
        mBack.setOnClickListener(this);

        mEdtName = (EditText) findViewById(R.id.editTxtNombre);
        mEdtUser = (EditText) findViewById(R.id.editTxtUsuario);
        mEdtPassword = (EditText) findViewById(R.id.editTxtContrasena);
        mEdtCellphone = (EditText) findViewById(R.id.editTxtCelular);

        // Seterar datos almacenados
        // no permitir editar email
        mEdtName.setText(mConf.getName());
        mEdtUser.setText(mConf.getUser());
        mEdtCellphone.setText(mConf.getPhone());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);
        //intentFilter.addAction(Actions.CONFIRM_NEW_SERVICES);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Log.v("USER_CLOSE_SESSION", "Sesión cerrada - confirmación");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(PerfilActivity.this, LoginActivity.class);
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

                    mConf.setServiceId(intent.getExtras().getString("service_id"));

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

        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this);
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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnActualizar:
                // llamar al servicio de actualizar
                updateProfile();
                break;

            case R.id.btnClose:
                // eliminar
                dialogoCerrarSesion();
                break;

            case R.id.btnPayments:
                Intent i = new Intent(PerfilActivity.this, PaymentsActivity.class);
                startActivity(i);
                break;

            case R.id.btn_volver:
                finish();
                break;
        }

    }

    public void dialogoCerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this);
        builder.setTitle(R.string.titulo_cierre_sesion);
        builder.setMessage(R.string.confirmacion_seguro_cierra_sesion);

        builder.setPositiveButton(R.string.text_btn_si,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

/*
                        Conf conf = new Conf(getApplicationContext());
                        conf.setLogin(false);

                        Intent in3 = new Intent(PerfilActivity.this,LoginActivity.class);
                        in3.putExtra("target", 0);
				        startActivity(in3);
				        finish();
*/

                        // call servicio log
                        String usr = mConf.getUser();
                        String pwd = mConf.getPass();

                        MiddleConnect.logout(PerfilActivity.this, usr, pwd, new AsyncHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                pDialog = new ProgressDialog(PerfilActivity.this);
                                pDialog.setMessage(getString(R.string.mensaje_cerrando_sesion));
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String response = new String(responseBody);
                                try {

                                    JSONObject responsejson = new JSONObject(response);

                                    Log.v("logout", "response = " + response);
                                    Log.v("logout", "response json = " + responsejson.toString());

                                    if ((responsejson.getInt("error") == 0) || (responsejson.getInt("error") == 1)) {
                                        mConf.setLogin(false);

                                        pDialog.dismiss();


                                        Intent in3 = new Intent(PerfilActivity.this, LoginActivity.class);
                                        in3.putExtra("target", 0);
                                        startActivity(in3);
                                        finish();

                                        Toast.makeText(getApplicationContext(), R.string.mensaje_session_cerrada_con_exito, Toast.LENGTH_LONG).show();
                                        // goToActivity(target_option);
                                    } else {
                                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(200);
                                        Toast.makeText(getApplicationContext(), R.string.mensaje_erro_cerrando_sesion, Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    err_close_session();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                String response = new String(responseBody);
                                Log.v("logout", "failure response = " + response);
                                err_close_session();
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
                });
        builder.setNegativeButton(R.string.text_btn_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void err_close_session() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.close_session_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            goToActivity(intent.getExtras().getInt("target_for", 0));
        }

    }

    private void updateProfile() {
        mName = mEdtName.getText().toString();

        mUser = mEdtUser.getText().toString();

        if (mEdtPassword.getText().toString().length() > 1) {
            mPassword = mEdtPassword.getText().toString();

            mPassword = Utils.md5(mPassword);

        } else {
            // enviar el password actual
            mPassword = mConf.getPass();
        }

        mCellphone = mEdtCellphone.getText().toString();

        if (mUuid != null) {
            if (checkregisterdata(mName, ".", mUser, mPassword, mCellphone)) {

                MiddleConnect.update(this, mName, mPassword, mUser, mUuid, mCellphone, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        pDialog = new ProgressDialog(PerfilActivity.this);
                        pDialog.setMessage(getString(R.string.text_actualizando));
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        try {

                            JSONObject responsejson = new JSONObject(response);

                            if (responsejson.getInt("error") == 0) {
                                mConf.setUser(mUser);
                                mConf.setName(mName);
                                mConf.setPass(Utils.md5(mPassword));
                                mConf.setIsFirst(false);
                                mConf.setLogin(true);
                                mConf.setIdUser(responsejson.getString("id"));

                                if (mEdtPassword.getText().toString().length() > 1) {
                                    mEdtPassword.setText("");
                                }
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_ok), Toast.LENGTH_LONG).show();
                                // goToActivity(target_option);
                            } else {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(200);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_exist, mUser), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            err_update();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        err_update();
                    }

                    @Override
                    public void onFinish() {
                        try {
                            pDialog.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });

            } else {
                err_update();
            }
        } else {
            mUuid = Utils.uuid(this);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_net), Toast.LENGTH_SHORT).show();
        }

    }

    protected void goToActivity(int target_option) {


        switch (target_option) {

            case Target.TAXI_TARGET:

                // Intent i = new Intent(PerfilActivity.this, SolicitandoActivity.class);

                // i.putExtra("index", index);
                // i.putExtra("comp1", comp1);
                // i.putExtra("comp2", comp2);
                // i.putExtra("no", no);
                // i.putExtra("barrio", barrio);
                // i.putExtra("obs", obs);
                // i.putExtra("latitud", String.valueOf(latitud));
                // i.putExtra("longitud", String.valueOf(longitud));
                // i.putExtra("target", Target.TAXI_TARGET);

                // startActivity(i);
                // finish();

                break;
            case Target.HISTORY_TARGET:
                Intent i2 = new Intent(PerfilActivity.this, HistorialActivity.class);
                startActivity(i2);
                finish();
                break;
            case Target.AGEND_TARGET:
                Intent i3 = new Intent(PerfilActivity.this, AgendarActivity.class);
                startActivity(i3);
                finish();
                break;
            case Target.MAP_TARGET:
                Intent i4 = new Intent(PerfilActivity.this, MapaActivitys.class);
                startActivity(i4);
                finish();
                break;
            case Target.ADDRESS_TARGET:
                Intent i5 = new Intent(PerfilActivity.this, MapaActivitys.class);
                setResult(2, i5);
                finish();
        }
    }

    private void err_update() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_error), Toast.LENGTH_SHORT).show();
    }

    private boolean checkregisterdata(String nombre, String apellido, String username, String password, String phone) {

        if (nombre.trim().equals("") || username.trim().equals("") || password.trim().equals("") || phone.trim().equals("")) {
            return false;

        } else {

            return true;
        }

    }

    @Override
    protected void onDestroy() {
        Log.v("onDestroy", "PerfilActivity");
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

}
