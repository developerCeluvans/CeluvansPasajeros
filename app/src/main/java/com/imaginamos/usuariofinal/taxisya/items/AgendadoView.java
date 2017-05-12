package com.imaginamos.usuariofinal.taxisya.items;

import org.json.JSONObject;

import com.imaginamos.usuariofinal.taxisya.interfaces.onCancelSchedule;
import com.imaginamos.usuariofinal.taxisya.models.Agendado;
import com.imaginamos.usuariofinal.taxisya.activities.EndAgendamientoActivity;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class AgendadoView extends LinearLayout {

	private TextView direccion, obs, destino, datetime, cancelar,espera, fin;
	private ImageView b_espera, b_cancel, b_fin, type;
	private String id_user, uuid;
	private ProgressDialog pDialog;
	private Context contexto;
	private Conf conf;
	private onCancelSchedule oncancelSchedule;

	public AgendadoView(Context context,onCancelSchedule oncancelSchedule) {

		super(context);

		contexto = context;

		this.oncancelSchedule =oncancelSchedule;

		inflate(context, R.layout.mis_agendamientos, this);

		conf = new Conf(getContext());
		id_user = conf.getIdUser();
		uuid = Utils.uuid(getContext());

		direccion = (TextView) findViewById(R.id.txt_direccion);
		obs = (TextView) findViewById(R.id.txt_obs);
		destino = (TextView) findViewById(R.id.txt_destino);
		datetime = (TextView) findViewById(R.id.txt_datetime);

		cancelar = (TextView) findViewById(R.id.txt_cancel);
		fin = (TextView) findViewById(R.id.txt_fin);
		espera = (TextView) findViewById(R.id.txt_espera);
		b_espera = (ImageView) findViewById(R.id.btn_espera);
		b_cancel = (ImageView) findViewById(R.id.btn_cancel);
		b_fin = (ImageView) findViewById(R.id.btn_fin);
		type = (ImageView) findViewById(R.id.img_tipo);

	}

	public void setAgendado(Agendado agn) {

		final String id_agendamiento = agn.getId();

		//direccion.setText(agn.getIndex() + " " + agn.getComp1() + " "+ agn.getComp2() + " " + agn.getNo());
		if(!TextUtils.isEmpty(agn.getFullAddress())) {
			direccion.setText(agn.getFullAddress());
		} else {
			direccion.setText(agn.getIndex() + " " + agn.getComp1() + " "+ agn.getComp2() + " " + agn.getNo());
		}
		if(!TextUtils.isEmpty(agn.getObservaciones())) {
			obs.setText(agn.getObservaciones());
			obs.setVisibility(View.VISIBLE);
		} else {
			obs.setVisibility(View.GONE);
		}
		//barrio.setText(agn.getBarrio());
		datetime.setText(agn.getDateTime());

		int tipo = Integer.parseInt(agn.getType());

		switch (tipo) {
		case 1:
			destino.setText(getResources().getString(R.string.set_agendado_aeropuerto));
			type.setImageResource(R.drawable.agn_aeropuerto);
			break;
		case 2:
			destino.setText(String.format("%s: %s ",getResources().getString(R.string.set_agendado_fuera_bogota),agn.getDestinationAddress()));
			//destino.setText(getResources().getString(R.string.set_agendado_fuera_bogota));
			type.setImageResource(R.drawable.agn_fuera);
			break;
		case 3:
			destino.setText(String.format("%s: %s ",getResources().getString(R.string.set_agendado_mensajeria),agn.getDestinationAddress()));
			//destino.setText(getResources().getString(R.string.set_agendado_mensajeria));
			type.setImageResource(R.drawable.agn_mensajeria);
			break;
		case 4:
			destino.setText(getResources().getString(R.string.set_agendado_servicio_horas));
			type.setImageResource(R.drawable.agn_horas);
			break;
		}

		Log.i("Estado Agendamiento: ", agn.getEstado());

		int status = Integer.parseInt(agn.getEstado());
		switch (status) {
		case 1:
		    cancelar.setVisibility(VISIBLE);
            b_cancel.setVisibility(VISIBLE);
            b_espera.setVisibility(VISIBLE);
			espera.setVisibility(VISIBLE);
			b_fin.setVisibility(INVISIBLE);
			fin.setVisibility(INVISIBLE);



			cancelar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder dialogo1 = new AlertDialog.Builder(contexto);
					dialogo1.setTitle("Importante");
					dialogo1.setMessage("¿Confirma cancelar el agendamiento?");
					dialogo1.setCancelable(false);
					dialogo1.setPositiveButton("Si",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									cancelService(id_agendamiento);

								}
							});
					dialogo1.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									dialogo1.cancel();
								}
							});
					dialogo1.show();
				}
			});



			b_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder dialogo1 = new AlertDialog.Builder(contexto);
					dialogo1.setTitle("Importante");
					dialogo1.setMessage("¿Confirma cancelar el agendamiento?");
					dialogo1.setCancelable(false);
					dialogo1.setPositiveButton("Si",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									cancelService(id_agendamiento);

								}
							});
					dialogo1.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									dialogo1.cancel();
								}
							});
					dialogo1.show();
				}
			});

			break;
		case 2:

            b_espera.setVisibility(VISIBLE);
			espera.setVisibility(VISIBLE);
			b_fin.setVisibility(INVISIBLE);
			fin.setVisibility(INVISIBLE);

			//b_espera.setImageResource(R.drawable.agn_fin);
			//espera.setText("Finalizado");
			b_espera.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder dialogo1 = new AlertDialog.Builder(
							contexto);
					dialogo1.setTitle("Importante");
					dialogo1.setMessage("¿Confirma finalizar el agendamiento?");
					dialogo1.setCancelable(false);
					dialogo1.setPositiveButton("Si",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									Intent i = new Intent(contexto,
											EndAgendamientoActivity.class);
									i.putExtra("id_schedule", id_agendamiento);
									contexto.startActivity(i);

								}
							});
					dialogo1.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									dialogo1.cancel();
								}
							});
					dialogo1.show();

				}
			});

			b_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder dialogo1 = new AlertDialog.Builder(
							contexto);

					dialogo1.setTitle("Importante");
					dialogo1.setMessage("¿Confirma cancelar el agendamiento?");
					dialogo1.setCancelable(false);
					dialogo1.setPositiveButton("Si",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									cancelService(id_agendamiento);

								}
							});
					dialogo1.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogo1,
										int id) {
									dialogo1.cancel();
								}
							});
					dialogo1.show();
				}
			});
			break;
		case 3:

			break;
		case 4:
			b_fin.setImageResource(R.drawable.cancel_agn);
			fin.setText("Cancelado");
			b_fin.setVisibility(VISIBLE);
			fin.setVisibility(VISIBLE);
			b_espera.setVisibility(INVISIBLE);
			espera.setVisibility(INVISIBLE);
			cancelar.setVisibility(INVISIBLE);
			b_cancel.setVisibility(INVISIBLE);
			break;
		case 5:
			fin.setText("Finalizado");
			b_espera.setVisibility(INVISIBLE);
			espera.setVisibility(INVISIBLE);
			cancelar.setVisibility(INVISIBLE);
			b_cancel.setVisibility(INVISIBLE);
			b_fin.setVisibility(VISIBLE);
			fin.setVisibility(VISIBLE);
			break;
		}

	}

	public void err_cancelar() {
		Vibrator vibrator = (Vibrator) contexto
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		Toast.makeText(contexto, "Hubo un error al cancelar el agendamiento",
				Toast.LENGTH_SHORT).show();
	}

	public void cancelService(String id_schedule) {


		MiddleConnect.cancelSchedule(getContext(), id_user, uuid, id_schedule, new AsyncHttpResponseHandler()
		{
		     @Override
		     public void onStart()
		     {
					pDialog = new ProgressDialog(contexto);
					pDialog.setMessage("Cancelando Agendamiento....");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
		     }

			 @Override
			 public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				 String response = new String(responseBody);
		    	 try {

		    		 Log.e("RESPONSE",response);

		    		 JSONObject responsejson = new JSONObject(response);

		    		 if(responsejson != null && response.length()>0)
		    		 {
		    			 if(responsejson.has("id"))
		    			 {
		    				oncancelSchedule.onCancel();
		    			 }else
		    			 {
							 Log.i("CANCELING SCHEDULE", "Bad Response");
		    				 err_cancelar();
		    			 }
		    		 }else
		    		 {
						 Log.i("CANCELING SCHEDULE", "Null Response ");
		    			 err_cancelar();
		    		 }


		    	 }catch(Exception e)
		    	 {
					 Log.i("CANCELING SCHEDULE", "Excetion "+e.getMessage());
		    		 err_cancelar();
		    	 }
		     }

			 @Override
			 public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				 String response = new String(responseBody);
				 Log.i("CANCELING SCHEDULE", "Response "+response);
				 err_cancelar();
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

}
