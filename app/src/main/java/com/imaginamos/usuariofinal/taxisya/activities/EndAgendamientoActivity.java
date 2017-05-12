package com.imaginamos.usuariofinal.taxisya.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class EndAgendamientoActivity extends Activity implements OnClickListener {


    private ProgressDialog pDialog;
    private String id_user, uuid, id_schedule;
    private Button mBtnMuyBueno, mBtnBueno, mBtnMalo;

    private ImageView muyBueno, bueno, malo;
    private String score;
    private Conf conf;

    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar);
        conf = new Conf(this);

        mBtnMuyBueno = (Button) findViewById(R.id.btnMuyBueno);
        mBtnBueno = (Button) findViewById(R.id.btnBueno);
        mBtnMalo = (Button) findViewById(R.id.btnMalo);

        mBtnMuyBueno.setOnClickListener(this);
        mBtnBueno.setOnClickListener(this);
        mBtnMalo.setOnClickListener(this);

        id_user = conf.getIdUser();
        uuid = Utils.uuid(this);
        Bundle reicieveParams = getIntent().getExtras();
        id_schedule = reicieveParams.getString("id_schedule");
    }

    public void err_calificar() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_q_service), Toast.LENGTH_SHORT).show();
    }

    public void qualifyService(String id_schedule) {

        MiddleConnect.finishSchedule(this, id_user, uuid, score, id_schedule, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                pDialog = new ProgressDialog(EndAgendamientoActivity.this);
                pDialog.setMessage(getString(R.string.enviando_calificacion));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject responsejson = new JSONObject(response);

                    String status = responsejson.getString("status");

                    if (status.equals("ok")) {

                        Intent i = new Intent(EndAgendamientoActivity.this, AgendarActivity.class);
                        startActivity(i);

                    } else {
                        err_calificar();
                    }

                } catch (Exception e) {
                    err_calificar();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                err_calificar();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMuyBueno:
                score = "3";
                break;
            case R.id.btnBueno:
                score = "2";
                break;
            case R.id.btnMalo:
                score = "1";
                break;
        }
        qualifyService(id_schedule);

    }
}
