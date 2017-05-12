package com.imaginamos.usuariofinal.taxisya.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity implements OnClickListener {

    private ImageView volver;
    private EditText user, pass;
    private String uuid, id_user;
    private Button reset_pass;
    private Button btnIngresar;
    private Button btnRegister;
    private ProgressDialog pDialog;
    private int target_option = 0;

    private Conf conf;

    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.activity_login);

        volver = (ImageView) findViewById(R.id.btn_volver);

        reset_pass = (Button) findViewById(R.id.reset_pass);

        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        reset_pass.setOnClickListener(this);
        btnIngresar.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        volver.setOnClickListener(this);

        user = (EditText) findViewById(R.id.usuario);
        pass = (EditText) findViewById(R.id.contrasena);

        uuid = Utils.uuid(this);

        target_option = getIntent().getExtras().getInt("target");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnIngresar:
                loginService();
                break;

            case R.id.btnRegister:
                finish();
                break;

            case R.id.btn_volver:
                finish();
                break;
            case R.id.reset_pass:
                Intent i = new Intent(getApplicationContext(), ResetPassActivity.class);
                startActivity(i);
                break;
        }
    }

    private void loginService() {

        final String email = user.getText().toString();
        final String password = pass.getText().toString();
        final String crypted_pass = Utils.md5(password);

        if (checklogindata(email, password)) {
            if (uuid == null) {
                Toast.makeText(this, getString(R.string.error_net),
                        Toast.LENGTH_LONG).show();

                uuid = Utils.uuid(this);
                Log.e("ERROR", "loginService uuid " + uuid);

            } else {

                MiddleConnect.login(this, email, crypted_pass, uuid,
                        new AsyncHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                // Initiated the request
                                pDialog = new ProgressDialog(LoginActivity.this);
                                pDialog.setMessage(getString(R.string.text_autenticando));
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(false);
                                pDialog.show();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String response = new String(responseBody);
                                Log.e("ERROR", response);
                                try {
                                    JSONObject responsejson = new JSONObject(
                                            response);

                                    int error = responsejson.getInt("error");

                                    if (error == 0) {

                                        id_user = responsejson.getString("id");
                                        String name = responsejson.getString("name");
                                        String cellphone = responsejson.getString("cellphone");

                                        conf = new Conf(getApplicationContext());

                                        conf.setLogin(true);

                                        conf.setName(name);

                                        conf.setPhone(cellphone);

                                        conf.setIdUser(id_user);

                                        conf.setPass(crypted_pass);

                                        conf.setUser(email);

                                        conf.setUuid(uuid);

                                        conf.setIsFirst(false);

                                        if (target_option > 0) {
                                            Log.v("LoginActivity", "target_option > 0");
                                            Intent in3 = new Intent(LoginActivity.this, HomeActivity.class);
                                            in3.putExtra("target", 0);
                                            startActivity(in3);
                                            finish();
                                        } else {
                                            Log.v("LoginActivity", "target_option > 0");
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra("target_for",
                                                    target_option);
                                            setResult(RESULT_OK, returnIntent);
                                            finish();
                                        }

                                    } else {
                                        err_login(1);
                                    }

                                } catch (Exception e) {
                                    err_login(1);
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                err_login(1);
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

        } else {

            err_login(0);

        }
    }

    public void err_login(int i) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(),
                getString(R.string.error_login), Toast.LENGTH_LONG).show();
    }

    public boolean checklogindata(String username, String password) {

        if (username.equals("") || password.equals("")) {
            return false;
        } else {
            return true;
        }

    }

}
