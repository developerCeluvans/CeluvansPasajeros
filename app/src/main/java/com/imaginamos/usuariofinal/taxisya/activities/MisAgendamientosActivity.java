package com.imaginamos.usuariofinal.taxisya.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.imaginamos.usuariofinal.taxisya.interfaces.onCancelSchedule;
import com.imaginamos.usuariofinal.taxisya.adapter.AgendadoAdapter;
import com.imaginamos.usuariofinal.taxisya.models.Agendado;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class MisAgendamientosActivity extends Activity{

	private ListView agenda;
	private String id_user, uuid;
	private ArrayList<Agendado> agendados;
	private AgendadoAdapter adapter;
	private ProgressDialog pDialog;
	private JSONObject json_schedule;
	private ImageView btn_atras;
	private Conf conf;
	private BroadcastReceiver mReceiver;

	@Override
	public void onRestart()
	{
		super.onRestart();
		overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.activity_misagendamientos);

        conf = new Conf(this);

		agenda = (ListView) findViewById(R.id.lista_agendados);

		id_user = conf.getIdUser();

		uuid = Utils.uuid(this);

        btn_atras = (ImageView) findViewById(R.id.btn_volver);

        btn_atras.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});


		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
		intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.ACTION_USER_CLOSE_SESSION)) {
					Log.v("USER_CLOSE_SESSION","Sesión cerrada - confirmación");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(MisAgendamientosActivity.this,LoginActivity.class);
                    in3.putExtra("target", 1);
					startActivity(in3);
					finish();
				}
				else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

					Log.v("MESSAGE_MASSIVE","mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

				}
			}
		};

		try {
			registerReceiver(mReceiver, intentFilter);
		} catch (Exception e) {

		}


        agendados = new ArrayList<Agendado>();
        adapter =  new AgendadoAdapter(agendados, new onCancelSchedule() {

			@Override
			public void onCancel() {
				cargarAgenda();
			}
		});
        agenda.setAdapter(adapter);
		cargarAgenda();
	}

    void mostrarMensaje(final String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.informacion_importante);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.text_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH","mensaje: " + message);
			}
		});
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

	public void cargarAgenda() {

		MiddleConnect.myAgend(this, id_user, uuid, new AsyncHttpResponseHandler()
		{
		     @Override
		     public void onStart()
		     {
		            pDialog = new ProgressDialog(MisAgendamientosActivity.this);
		            pDialog.setMessage(getString(R.string.titulo_cargando_agendamientos));
		            pDialog.setIndeterminate(false);
		            pDialog.setCancelable(false);
		            pDialog.show();
		     }

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
		    	 try {

		    		 JSONObject responsejson = new JSONObject(response);

		    		if (responsejson != null && responsejson.length() > 0)
		    		{
		    			if(responsejson.getInt("error") == 0)
		    			{

		    				json_schedule = responsejson;

		    				convertirDatos();

		    			}else if(responsejson.getInt("error") == 1)
		    			{
		    				err_schedule();
		    			}


		    		} else {
		    			 err_schedule();
		    		}

		    	 }catch(Exception e)
		    	 {
		    		 err_schedule();
		    	 }
		     }

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				 err_schedule();
		     }

		     @Override
		     public void onFinish()
		     {
		    	 try {
		    		 pDialog.dismiss();
				} catch (Exception e) {
				}

		     }
		});

	}

	public void err_schedule()
	{
    	Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.vibrate(200);
	    Toast.makeText(getApplicationContext(),getString(R.string.error_net), Toast.LENGTH_SHORT).show();
    }

    public void convertirDatos(){
    	try {

	    	JSONArray lista = json_schedule .getJSONArray("schedules");

	    	agendados.clear();

	    	for (int i = 0; i < lista.length(); i++) {

	    	    JSONObject jsObject = lista.getJSONObject(i);

	    	    String id_service = jsObject.getString("id");
	    	    String id_driver = jsObject.getString("driver_id");
    	    	String datetime = jsObject.getString("service_date_time");
    	    	String tipo = jsObject.getString("schedule_type");
    	    	String index = jsObject.getString("address_index");
    	    	String comp1 = jsObject.getString("comp1");
    	    	String comp2 = jsObject.getString("comp2");
    	    	String no = jsObject.getString("no");
    	    	String barrio = jsObject.getString("barrio");
    	    	String obs = jsObject.getString("obs");
    	    	String estado = jsObject.getString("status");
				String fullAddress = jsObject.getString("address");
				String destinationAddress = jsObject.getString("destination");

    	    	int st = Integer.parseInt(estado);
    	    	Agendado a = null;
    	    	switch (st) {
    	    	case 1:
    	    		a = new Agendado(id_service, id_driver, datetime, tipo, index, comp1, comp2, no, barrio, obs, estado,fullAddress, destinationAddress);
    	    		break;
				case 2:
					JSONObject taxista = jsObject.getJSONObject("driver");
					JSONObject taxi = taxista.getJSONObject("car");
					String foto = taxista.getString("picture");
					String name = taxista.getString("name");
					String lastname = taxista.getString("lastname");
					String placa = taxi.getString("placa");
					String marca = taxi.getString("car_brand");
					String modelo = taxi.getString("model");
					a = new Agendado(id_service, id_driver, datetime, tipo, index, comp1, comp2, no, barrio, obs, estado, foto, name, lastname, marca, modelo, placa, fullAddress, destinationAddress);
					break;
				case 4:
					a = new Agendado(id_service, id_driver, datetime, tipo, index, comp1, comp2, no, barrio, obs, estado, fullAddress, destinationAddress);
					break;
				case 5:

					if(!jsObject.isNull("driver"))
					{
						JSONObject taxista2 = jsObject.getJSONObject("driver");
						JSONObject taxi2 = taxista2.getJSONObject("car");

						String foto2 = taxista2.getString("picture");
						String name2 = taxista2.getString("name");
						String lastname2 = taxista2.getString("lastname");
						String placa2 = taxi2.getString("placa");
						String marca2 = taxi2.getString("car_brand");
						String modelo2 = taxi2.getString("model");
						a = new Agendado(id_service, id_driver, datetime, tipo, index, comp1, comp2, no, barrio, obs, estado, foto2, name2, lastname2, marca2, modelo2, placa2, fullAddress, destinationAddress);

					}else
					{
						a = new Agendado(id_service, id_driver, datetime, tipo, index, comp1, comp2, no, barrio, obs, estado, fullAddress, destinationAddress);
					}

					break;
				default:
					break;
				}
	    	    agendados.add(a);
	    	}
    	} catch (JSONException e) {
    		e.printStackTrace();
		}
    	adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        Log.v("onDestroy","MisAgendamientosActivity");
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

}
