package com.imaginamos.usuariofinal.taxisya.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.imaginamos.usuariofinal.taxisya.models.Historico;
import com.imaginamos.usuariofinal.taxisya.adapter.DayAdapter;
import com.imaginamos.usuariofinal.taxisya.adapter.HistoricoAdapter;
import com.imaginamos.usuariofinal.taxisya.items.HorizontalListView;
import com.imaginamos.usuariofinal.taxisya.models.DiaMes;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cz.msebera.android.httpclient.Header;

@SuppressLint("HandlerLeak")
public class HistorialActivity extends Activity implements OnClickListener {


	static ArrayList<Historico> servicios;
	static ArrayList<DiaMes> dias;
	private ListView lista;
	private HorizontalListView lista_dias;
	static HistoricoAdapter adaptador;
	static DayAdapter dayAdapter;
	String id_user, uuid;
	private ProgressDialog pDialog;
	public static JSONObject json_servicios = null;
	public static JSONArray json_dias = null;
	public String respuesta;
	private ImageView bt_back;
	public static int mYear, mMonth, mDay;
	private int log_ = 0;
	private Handler myhand;
	private  int logstatus = -1;
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

		setContentView(R.layout.activity_historial);

		conf = new Conf(this);

		id_user = conf.getIdUser();

		uuid = Utils.uuid(this);

		lista = (ListView) findViewById(R.id.listaHistorico);

		lista_dias = (HorizontalListView) findViewById(R.id.listview_dias);

		bt_back = (ImageView) findViewById(R.id.bt_back);
		servicios = new ArrayList<Historico>();
		dias = new ArrayList<DiaMes>();
		adaptador = new HistoricoAdapter(servicios);
		dayAdapter = new DayAdapter(dias);

		lista_dias.setAdapter(dayAdapter);
		lista_dias.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.v("lista_dias","selected " + String.valueOf(arg2));
				DiaMes d = dias.get(arg2);
				Log.v("lista_dias","dia " + d.getAnio() + " " + d.getMes() + " " + d.getDia());

				cargarHistorial(d.getAnio(), d.getMes(), d.getDia());
				dayAdapter.notifyDataSetInvalidated();
/*
				TextView dia = (TextView) arg1.findViewById(R.id.dia);
		        TextView n_dia = (TextView) arg1.findViewById(R.id.ndia);

			    n_dia.setTextColor(getResources().getColor(R.color.day_selected));
                dia.setTextColor(getResources().getColor(R.color.day_selected));
*/
			}

		});

		lista.setAdapter(adaptador);
		lista.setScrollingCacheEnabled(false);

		final Calendar c = Calendar.getInstance();

		mYear  = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH)+1;
		mDay   = c.get(Calendar.DAY_OF_MONTH);

		myhand = new Handler()
		{
			  @Override
			  public void handleMessage(Message msg)
			  {
				  HistorialActivity.this.setContentView(R.layout.activity_sin_historial);
				  bt_back = (ImageView) findViewById(R.id.bt_back);
				  bt_back.setOnClickListener(HistorialActivity.this);
			  }
		};


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

                    Intent in3 = new Intent(HistorialActivity.this,LoginActivity.class);
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


		cargarHistorial(String.valueOf(mMonth));

		bt_back.setOnClickListener(this);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .defaultDisplayImageOptions(defaultOptions)
        .build();
		ImageLoader.getInstance().init(config);
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

    /*
	public void cargarHistorial(View v, String month, String day) {

		MiddleConnect.getServices(this, id_user, uuid, month, new AsyncHttpResponseHandler()
		{
		     @Override
		     public void onStart()
		     {
					pDialog = new ProgressDialog(HistorialActivity.this);
					pDialog.setMessage("Cargando HistorialActivity....");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
		     }

		     @Override
		     public void onSuccess(String response)
		     {
		    	 try {

		    		 Log.e("ERROR", "Response" + response);

		    		 JSONObject responsejson = new JSONObject(response);

		    		 respuesta = response;

		    		if (response != null && response.length() > 0)
		    		{

		    				JSONObject json_data; // creamos un objeto JSON
		    				json_servicios = null;

		    				try {

		    					json_data = responsejson;
		    					json_servicios = responsejson;
		    					json_dias = json_data.getJSONArray("dayList");

		    					logstatus = json_data.optInt("error");

		    					log_ = logstatus;

		    				} catch (JSONException e) {
		    					Log.e("Error", "Error= " + e.toString());
		    				}
		    				Log.i("Dias:", json_servicios.toString());

		    				if (logstatus != -1 || logstatus != 0 || logstatus != 401)
		    				{
		    					// [{"logstatus":"0"}]
		    					Log.e("loginstatus ", "valido");
		    					//return true;
		    					convertirDatos();

		    				} else {// [{"logstatus":"1"}]
		    					Log.e("loginstatus ", "invalido");
		    					//return false;
	    						if (log_ == 1)
	    						{
	    							myhand.sendEmptyMessage(0);
	    						} else {
	    							err_services();
	    						}
		    				}

		    		} else { // json obtenido invalido verificar parte WEB.
		    				Log.e("JSON  ", "ERROR");
		    				//return false;
		    		}

		    	 }catch(Exception e)
		    	 {
		    		 err_services();
		    	 }
		     }

		     @Override
		     public void onFailure(Throwable e, String response)
		     {
		    	 err_services();
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
	*/
	public void cargarHistorial(String year,String month, String day) {

		Log.e("RESPONSE ", ""+month+":"+day);
		MiddleConnect.getServices(this, id_user, uuid,year, month,day, new AsyncHttpResponseHandler() {

					@Override
					public void onStart()
					{
						pDialog = new ProgressDialog(HistorialActivity.this);
						pDialog.setMessage(getString(R.string.title_cargando_historial));
						pDialog.setIndeterminate(false);
						pDialog.setCancelable(false);
						pDialog.show();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String response = new String(responseBody);
						try {
							Log.e("RESPONSE ", ""+response);
							JSONObject responsejson = new JSONObject(response);

							JSONObject json_data;

							json_data = responsejson;

							HistorialActivity.json_servicios = json_data;

							HistorialActivity.json_dias = json_data.getJSONArray("dayList");

							int logstatus = responsejson.optInt("error");

							if (logstatus == 0 ) {

								//HistorialActivity.convertirDatos();
								convertirDatos();


							} else {
								err_services();
							}

						} catch (Exception e) {
							err_services();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						String response = new String(responseBody);
						Log.e("RESPONSE ", ""+response);
						err_services();
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



	public void cargarHistorial(String month) {

		Log.e("MES",month);
		MiddleConnect.getServices(this, id_user, uuid, month, new AsyncHttpResponseHandler()
		{
		     @Override
		     public void onStart()
		     {
					pDialog = new ProgressDialog(HistorialActivity.this);
					pDialog.setMessage(getString(R.string.title_cargando_historial));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
		     }

			 @Override
			 public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				 String response = new String(responseBody);
		    	 try {
		    		 Log.e("RESPONSE ", ""+response);
		    		 JSONObject responsejson = new JSONObject(response);

		    		 respuesta = response;

		    		if (response != null && response.length() > 0)
		    		{
		    			JSONObject json_data;

		    			json_servicios = null;

		    			try {

		    				json_data = responsejson;
		    				json_servicios = responsejson;
		    				json_dias = json_data.getJSONArray("dayList");

		    				logstatus = json_data.optInt("error");

		    				log_ = logstatus;


		    				if (logstatus != -1 || logstatus != 0 || logstatus != 401)
		    				{
		    					Log.e("loginstatus ", "valido");
		    					//convertirDias();
		    					convertirMes();
		    					convertirDatos();
		    				} else {
		    					Log.e("loginstatus ", "invalido");
		    					if (log_ == 1) {

		    						myhand.sendEmptyMessage(0);
		    					} else {
		    						err_services();
		    					}
		    				}

		    			} catch (Exception e) {
		    				myhand.sendEmptyMessage(0);
		    			}

		    		} else {
		    				Log.e("JSON  ", "ERROR");
		    		}

		    	 }catch(Exception e)
		    	 {
		    		 err_services();
		    	 }
		     }

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				 String response = new String(responseBody);
		    	 Log.e("RESPONSE ", ""+response);
		    	 err_services();
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

	public void err_services() {

		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		Toast.makeText(getApplicationContext(),
				getString(R.string.error_load_services), Toast.LENGTH_SHORT).show();
	}




	public void convertirDias() {

		try {

			for (int j = 0; j < json_dias.length(); j++) {
				String a = json_dias.getString(j);
				String weekDay = null;
				Calendar c = Calendar.getInstance();
				c.set(mYear, mMonth, Integer.parseInt(a));
				int dia_semana = c.get(Calendar.DAY_OF_WEEK);
				switch (dia_semana) {
				case 1:
					weekDay = getString(R.string.text_dia_dom);
					break;
				case 2:
					weekDay = getString(R.string.text_dia_lun);
					break;
				case 3:
					weekDay = getString(R.string.text_dia_mar);
					break;
				case 4:
					weekDay = getString(R.string.text_dia_mie);
					break;
				case 5:
					weekDay = getString(R.string.text_dia_jue);
					break;
				case 6:
					weekDay = getString(R.string.text_dia_vie);
					break;
				case 7:
					weekDay = getString(R.string.text_dia_sab);
					break;
				default:
					break;
				}

				DiaMes d = new DiaMes(a, String.valueOf(mMonth),String.valueOf(mYear), weekDay);
				dias.add(d);

			}
		} catch (Exception e) {

		}
	}

    // para mostrar el mes en el activity_historial, se aprovecha la funcion original convertirDias y la clase DiaMes
	public void convertirMes() {

		try {

			for (int j = 0; j < json_dias.length(); j++) {
				String a = json_dias.getString(j);


				Log.v("HISTORY1","a = " + a);
//				StringTokenizer tokens = new StringTokenizer(a, "/");
//                String strDay = tokens.nextToken();
//				String strMonth = tokens.nextToken();
				// 0123456789
				// YYYY-MM-DD
				String strYear = a.substring(0,4);
				String strMonth = a.substring(5,7);
				String strDay = a.substring(8,10);
				Log.v("JSON_DIAS"," year,month,day " + strYear + " " + strMonth + " " + strDay);
				Log.v("HISTORY1","tokens2 " + strDay + " " + strMonth);


				String monthName = null;
				Calendar c = Calendar.getInstance();
				//c.set(mYear, mMonth, Integer.parseInt(a));
				//c.set(mYear, Integer.parseInt(strMonth), Integer.parseInt(strDay));
				c.set(Integer.parseInt(strYear), Integer.parseInt(strMonth), Integer.parseInt(strDay));


				int mes = c.get(Calendar.MONTH);
				switch (mes) {
				case 1:
					monthName = getString(R.string.text_mes_ene);
					break;
				case 2:
					monthName = getString(R.string.text_mes_feb);
					break;
				case 3:
					monthName = getString(R.string.text_mes_mar);
					break;
				case 4:
					monthName = getString(R.string.text_mes_abr);
					break;
				case 5:
					monthName = getString(R.string.text_mes_may);
					break;
				case 6:
					monthName = getString(R.string.text_mes_jun);
					break;
				case 7:
					monthName = getString(R.string.text_mes_jul);
					break;
				case 8:
					monthName = getString(R.string.text_mes_ago);
					break;
				case 9:
					monthName = getString(R.string.text_mes_sep);
					break;
				case 10:
					monthName = getString(R.string.text_mes_oct);
					break;
				case 11:
					monthName = getString(R.string.text_mes_nov);
					break;
				case 12:
					monthName = getString(R.string.text_mes_dic);
					break;
				default:
					break;
				}

				//DiaMes d = new DiaMes(a, String.valueOf(mMonth),String.valueOf(mYear), monthName);
				//DiaMes d = new DiaMes(strDay, String.valueOf(strMonth),String.valueOf(mYear), monthName);
				DiaMes d = new DiaMes(strDay, String.valueOf(strMonth),strYear, monthName);
				dias.add(d);

			}
		} catch (Exception e) {

		}
	}

	public void convertirDatos() {
		Log.v("lista_dias","convertirDatos ini");
		try {

			JSONArray lista = json_servicios.getJSONArray("services");

			servicios.clear();

			for (int i = 0; i < lista.length(); i++) {

				JSONObject jsObject = lista.getJSONObject(i);
				JSONObject taxista = jsObject.getJSONObject("driver");
				//JSONObject taxi = taxista.getJSONObject("car");
                JSONObject taxi = jsObject.getJSONObject("car");

				String id_service = jsObject.getString("id");
				String id_driver = jsObject.getString("driver_id");
				String fecha_servicio = jsObject.getString("created_at");
				String cellphone = taxista.getString("cellphone");
				String foto = taxista.getString("picture");
				String nombre = taxista.getString("name");
				String apellido = taxista.getString("lastname");
				String placa = taxi.getString("placa");
				String marca = taxi.getString("car_brand");
				String modelo = taxi.getString("model");

				Historico registro = new Historico(id_service, id_driver,
						fecha_servicio, foto, nombre + " " + apellido, placa,
						marca, modelo, cellphone);
				servicios.add(registro);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}
		dayAdapter.notifyDataSetChanged();
		adaptador.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
        Log.v("onDestroy","HistorialActivity");
		super.onDestroy();

		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_back:
			finish();
			break;
		}

	}

}
