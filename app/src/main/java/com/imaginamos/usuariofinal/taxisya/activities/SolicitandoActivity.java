package com.imaginamos.usuariofinal.taxisya.activities;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.Connectivity;
import com.imaginamos.usuariofinal.taxisya.models.Error;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SolicitandoActivity extends Activity implements OnClickListener {

	public static final int NOTIFICATION_ID = 1;

	private ImageView volver;

	private String id_user, uuid, service_id, index, comp1, comp2, no, barrio, obs, latitud, longitud;

	private ProgressDialog pDialog;

	private AnimationDrawable frameAnimation;

	private boolean isError = false;

	private AlertDialog alert;

	private Conf conf;

	private String url_cancel_current;

	private Timer myTimer = new Timer();

	private BroadcastReceiver  mBroadCastReceiver;

	private BroadcastReceiver mReceiver;

	private int reintento = 0;

	private Date fecha1;

	@SuppressLint("HandlerLeak")
	private Handler puente = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.v("SolicitandoActivity","Message msg = " + msg.toString());

			if (msg.what == 0)
			{

				animar();

				finish();

			} else if (msg.what == 2)
			{

				err_request();

			} else if (msg.what == 3) {

				try {

					Toast.makeText(getApplicationContext(),getString(R.string.error_no_driver), Toast.LENGTH_SHORT).show();

				} catch (Exception e)
				{
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(200);
					Toast.makeText(SolicitandoActivity.this,getString(R.string.error_no_driver),Toast.LENGTH_LONG).show();
					finish();
				}
			} else if (msg.what == 1500)
			{
				//Log.v("SolicitandoActivity","msg.what = 1500");
				//Toast.makeText(getApplicationContext(),getString(R.string.error_no_driver), Toast.LENGTH_SHORT).show();
				//Toast.makeText(getApplicationContext(),"msg.what = 1500", Toast.LENGTH_SHORT).show();

				AlertDialog.Builder builder = new AlertDialog.Builder(SolicitandoActivity.this);
				builder.setTitle(getString(R.string.important));
				builder.setMessage(R.string.nocancel)
						.setPositiveButton(R.string.retry,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id)
									{
										cancelService(url_cancel_current);
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										alert.dismiss();
									}
								});
				builder.setCancelable(false);

				try {
					alert = builder.create();
					alert.show();
				} catch (Exception e) {

				}

			}else if(msg.what ==  2000)
			{

				cancelByService(getResources().getString(R.string.cancel_service,id_user));
				Log.v("SolicitandoActivity","msg.what = 2000");
				Toast.makeText(getApplicationContext(),getString(R.string.error_no_driver), Toast.LENGTH_SHORT).show();
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);


		setContentView(R.layout.activity_solicitando_servicio);

		conf = new Conf(this);
		Log.v("SolicitandoActivity","onCreate");
		//ImageView load, cancelar;
		ImageView load;
		Button cancelar;


		// cancelar = (ImageView) findViewById(R.id.btn_cancelar);
		cancelar = (Button) findViewById(R.id.btn_cancelar);


		volver = (ImageView) findViewById(R.id.btn_volver);

		cancelar.setOnClickListener(this);

		volver.setOnClickListener(this);

		load = (ImageView) findViewById(R.id.carga);

		//load.setBackgroundResource(R.anim.carga_serv);

		final AnimationDrawable frameAnimation = (AnimationDrawable) load.getBackground();

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

                    Intent in3 = new Intent(SolicitandoActivity.this,LoginActivity.class);
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

		load.post(new Runnable()
		{
		    public void run()
		    {
		    	frameAnimation.start();
		    }
		});

		id_user = conf.getIdUser();

		uuid = conf.getUuid();

		if(!Connectivity.isConnected(this))
		{
			showDialog();
		}else
		{
			initWorkFlow();
		}

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

	private void showDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(SolicitandoActivity.this);

		builder.setTitle(getString(R.string.important));

		builder.setMessage(R.string.error_net)
				.setPositiveButton(R.string.retry,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								if(!Connectivity.isConnected(SolicitandoActivity.this))
								{
									showDialog();
								}else
								{
									initWorkFlow();
								}

							}
						})
						.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								alert.dismiss();
								finish();
							}
						});

		builder.setCancelable(false);

		alert = builder.create();

		alert.show();
	}

	private void initWorkFlow()
	{

		Bundle reicieveParams = getIntent().getExtras();

		if (reicieveParams != null && reicieveParams.containsKey("service_id"))
		{
			service_id = reicieveParams.getString("service_id");

			conf.setServiceId(service_id);

		} else
		{
			try {
				index = reicieveParams.getString("index");
				comp1 = reicieveParams.getString("comp1");
				comp2 = reicieveParams.getString("comp2");
				no = reicieveParams.getString("no");
				barrio = reicieveParams.getString("barrio");
				obs = reicieveParams.getString("obs");
				latitud = reicieveParams.getString("latitud");
				longitud = reicieveParams.getString("longitud");
			} catch (Exception e)
			{
				Log.e("ERROR", "ERROR EN SOLICITANDO SERVICIO!");
			}

			getService();

		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}

	public void animar() {

		try {

			frameAnimation = (AnimationDrawable) findViewById(R.id.carga).getBackground();
//			frameAnimation.addFrame(getResources().getDrawable(R.drawable.c_siete), 1000);
//			frameAnimation.addFrame(getResources().getDrawable(R.drawable.c_ocho), 1000);
			frameAnimation.start();

		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		    if(!moveTaskToBack(true))
		    {
			  Intent i = new Intent();
			  i.setAction(Intent.ACTION_MAIN);
			  i.addCategory(Intent.CATEGORY_HOME);
			  this.startActivity(i);

		    }
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	private void showDialogCancel()
	{
		AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);

		dialogo1.setTitle(getResources().getString(R.string.app_name));
		dialogo1.setMessage(getResources().getString(R.string.confirm_cancel));
		dialogo1.setCancelable(false);
		dialogo1.setPositiveButton(R.string.texto_si,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogo1, int id) {
						cancelarService();
					}
				});
		dialogo1.setNegativeButton(R.string.texto_no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogo1, int id) {
						dialogo1.cancel();
					}
				});
		dialogo1.show();
	}

	@Override
	public void onClick(final View v) {

		switch (v.getId()) {

		case R.id.btn_cancelar:

				showDialogCancel();

			break;
		case R.id.btn_volver:

			showDialogCancel();

			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if(mBroadCastReceiver!=null)
		{
			unregisterReceiver(mBroadCastReceiver);
		}

		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
		}

		if(myTimer != null)
		{
			myTimer.cancel();
			myTimer.purge();
			myTimer = null;
		}

	}

	private void cancelarService()
	{
		cancelService(getResources().getString(R.string.cancel_service,id_user));
	}

	private void getService() {

//		MiddleConnect.getService(this, id_user, latitud, longitud, index, comp1, comp2, no, barrio, obs, uuid, new AsyncHttpResponseHandler() {
		MiddleConnect.getService(this, id_user, latitud, longitud, "", comp1, comp2, no, barrio, obs, uuid, new AsyncHttpResponseHandler() {

					@Override
					public void onStart()
					{
						pDialog = new ProgressDialog(SolicitandoActivity.this);
						pDialog.setMessage(getString(R.string.texto_solicitando_servicio));
						pDialog.setIndeterminate(false);
						pDialog.setCancelable(false);
						//pDialog.show();
						Log.v("SOLICITANDO_SERVICIO","onStart " + String.valueOf(new Date()));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String response = new String(responseBody);
						try {

							Log.e("solicitando", "SUCCES: "+response);
							Log.v("SOLICITANDO_SERVICIO","onSucces " + String.valueOf(new Date()));

							JSONObject responsejson = new JSONObject(response);

							if (responsejson.getBoolean("success"))
							{

								conf.setServiceId(responsejson.getString("service_id"));

								IntentFilter intent = new IntentFilter();

								intent.addAction(Actions.CONFIRM_NEW_SERVICES);
                                intent.addAction(Actions.ACTION_USER_CLOSE_SESSION);


								mBroadCastReceiver = new BroadcastReceiver()
								{

									@Override
									public void onReceive(Context context,Intent intent)
									{
										String value = intent.getAction();

										Log.e("ACTION", value);
										Log.v("SOLICITANDO_SERVICIO","onReceive " + String.valueOf(new Date()) + " " + value);

										if(value.equals(Actions.CONFIRM_NEW_SERVICES))
										{
											Log.v("SOLICITANDO_ACTION","CONFIRM_NEW_SERVICES");
											try {

												myTimer.cancel();
												myTimer.purge();
												myTimer =  null;

							                	Intent mIntent = new Intent(getApplicationContext(),ConfirmacionActivity.class);
												mIntent.putExtra("driver", intent.getExtras().getString("driver"));
												startActivity(mIntent);
												finish();

											} catch (Exception e) {
												err_request();
												finish();
											}
										}
										else if (value.equals(Actions.ACTION_USER_CLOSE_SESSION)) {
										   Log.v("SOLICITANDO_ACTION","ACTION_CANCEL_DRIVER_SERVICE");

                                           Conf conf = new Conf(getApplicationContext());
                                           conf.setLogin(false);

                                           Intent in3 = new Intent(SolicitandoActivity.this,HomeActivity.class);
					                       startActivity(in3);
					                       finish();

										}

									}

								};

								registerReceiver(mBroadCastReceiver, intent);

								// myTimer.schedule(new TimerTask()
								// {
								// 	@Override
								// 	public void run()
								// 	{
								// 		Log.e("TIMER EJECUTANDO", "EJECUTANDO ***");

								// 		puente.sendEmptyMessage(2000);

								// 		myTimer.cancel();
								// 	}

								// }, 60000, 60000);

                                reintento = 0;
								myTimer.schedule(new TimerTask()
								{
									@Override
									public void run()
									{
										Log.e("TIMER_EJECUTANDO", "EJECUTANDO *** " + String.valueOf(reintento));
										Log.v("SOLICITANDO_SERVICIO","TIMER - EJECUTANDO " + String.valueOf(new Date()));

                                        reintento ++;

                                        try {
											checkService();
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

                                        //if (reintento >= 3) {
                                        if (reintento >= 5) {
										   Log.e("TIMER_EJECUTANDO", "FIN EJECUTANDO *** ");
										   Log.v("SOLICITANDO_SERVICIO","TIMER - FIN EJECUTANDO " + String.valueOf(new Date()));


										   puente.sendEmptyMessage(2000);

										   myTimer.cancel();
									    }
									}

								}, 2000, 10000);



							}else
							{
								if(responsejson.getInt("error") == Error.NO_DRIVER_ENABLE)
								{

					                Log.v("SOLICITANDO_SERVICIO","error - Error.NO_DRIVER_ENABLE " + String.valueOf(new Date()));

									Toast.makeText(getApplicationContext(), getString(R.string.error_no_driver),Toast.LENGTH_LONG).show();

								}else
								{
									Log.v("SOLICITANDO_SERVICIO","error_request - " + String.valueOf(new Date()));

									err_request();

								}

								finish();
							}

						} catch (Exception e)
						{
							Log.e("error_solicitando", "Problema json"+e.toString());
							Log.v("SOLICITANDO_SERVICIO","error - problemas json " + String.valueOf(new Date()));

							err_request();
							finish();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						String response = new String(responseBody);

						Log.e("error_solicitando", "FAILURE"+response);
						Log.v("SOLICITANDO_SERVICIO","onFailure " + String.valueOf(new Date()));


						err_request();

						puente.sendEmptyMessage(2000);

					}

					@Override
					public void onFinish()
					{
						try {
							//pDialog.dismiss();
						} catch (Exception e) {
						}
					}

			});

	}

    public void checkService() throws JSONException {

        service_id = conf.getServiceId();

        Log.v("checkService","id_user="+ id_user + " service_id=" + service_id );

		MiddleConnect.checkStatusService(this, id_user, service_id, "uuid", new AsyncHttpResponseHandler() {

					@Override
					public void onStart()
					{
						Log.v("checkService", "onStart");
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						String response = new String(responseBody);
						try {

							Log.v("checkService", "SUCCES: "+response);

							JSONObject responsejson = new JSONObject(response);

							//if (responsejson.getInt("status_id"))
							//{
								int status = responsejson.getInt("status_id");
					            Log.v("checkService", "status_id: " + String.valueOf(status));

					            if ((status == 2) || (status == 4) || (status == 5)) {
					            	Log.v("checkService","servicios detectado por socket, sin push");
					                Intent mIntent = new Intent(getApplicationContext(),ConfirmacionActivity.class);
					                mIntent.putExtra("driver", responsejson.getJSONObject("driver").toString());

                                    if (status == 5) {
                                        mIntent.putExtra("qualification", "1");
                                    }
                                    else {
                                        mIntent.putExtra("qualification", "0");
                                    }
									startActivity(mIntent);
									finish();
					            }
					            else {
					            	if (status >= 6) {
					            		// servicio cancelado
                                        String msg;
                                        if (status == 8) {
                                            msg = getString(R.string.servicio_cancelado_conductor);
                                        }
                                        else {
                                            msg = getString(R.string.servicio_cancelado);
                                        }
					            	}
					            }

							//}


                            /*
							if (responsejson.getBoolean("success"))
							{

								conf.setServiceId(responsejson.getString("service_id"));

							}
							*/

						} catch (Exception e)
						{
							Log.v("checkService", "Problema json"+e.toString());
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						Log.v("checkService", "onFailure");

					}

					@Override
					public void onFinish()
					{

						Log.v("checkService", "onFinish");

					}

			});

    }


	public void err_request() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		Toast.makeText(getApplicationContext(),getString(R.string.error_no_procc), Toast.LENGTH_SHORT).show();
	}

	public void service_cancel()
	{

		NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NotificationActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
													        .setSmallIcon(R.drawable.ic_launcher)
													        .setContentTitle(getString(R.string.app_name))
													        .setStyle(new NotificationCompat.BigTextStyle()
													        .bigText(getResources().getString(R.string.error_no_driver)))
													        .setAutoCancel(true)
													        .setContentText(getResources().getString(R.string.error_no_driver));

        mBuilder.setContentIntent(contentIntent);

        mBuilder.setSound(Uri.parse("android.resource://"+ getPackageName() + "/"+ R.raw.audio2), 1);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		vibrator.vibrate(250);

	}

	public void err_cancel()
	{
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_cancel_service),Toast.LENGTH_SHORT).show();
	}

	public void cancelService(final String Url) {

		url_cancel_current =  Url;

		MiddleConnect.cancelService(Url, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {

				try {
					if (!pDialog.isShowing())
					{
						pDialog = new ProgressDialog(SolicitandoActivity.this);
						pDialog.setMessage(getString(R.string.texto_cancelando_servicio));
						pDialog.setIndeterminate(false);
						pDialog.setCancelable(false);
						pDialog.show();
					}
				} catch (Exception e) {
				}

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				try {
					Log.e("solicitando", "SUCCES: "+response);

					JSONObject responsejson = new JSONObject(response);

					if (responsejson.getBoolean("success"))
					{
						try {

							isError = false;

							pDialog.dismiss();

						} catch (Exception e) {}

						finish();

					} else {

						if(responsejson.getInt("error") == 404)
						{
							try {
								pDialog.dismiss();
							} catch (Exception e) {}

							finish();

							isError = false;

						}else
						{
							isError = true;
						}

					}

				} catch (Exception e) {

					isError = true;
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.e("solicitando", "onFailure: "+response);
				isError = true;
				try {
					pDialog.dismiss();
				} catch (Exception e1) {
				}
				puente.sendEmptyMessage(1500);
			}

			@Override
			public void onFinish() {

				if (isError)
				{
					try {
						pDialog.dismiss();
					} catch (Exception e) {
					}
					puente.sendEmptyMessage(1500);
				}
			}

		});

	}

	public void cancelByService(final String Url)
	{
		service_cancel();

		url_cancel_current = Url;

		MiddleConnect.cancelServiceBySystem(Url,  new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {

				try {

					if (!pDialog.isShowing())
					{
						pDialog = new ProgressDialog(SolicitandoActivity.this);
						pDialog.setMessage(getString(R.string.texto_cancelando_servicio));
						pDialog.setIndeterminate(false);
						pDialog.setCancelable(false);
						pDialog.show();
					}
				} catch (Exception e) {}

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.e("solicitando", "SUCCES: "+response);

				try {

					JSONObject responsejson = new JSONObject(response);

					if (responsejson.getBoolean("success"))
					{
						try {

							isError = false;

							pDialog.dismiss();

						} catch (Exception e) {
						}

						finish();

					} else {

						if(responsejson.getInt("error") == 404)
						{
							try {
								pDialog.dismiss();
							} catch (Exception e) {}

							finish();

							isError = false;

						}else
						{
							isError = true;
						}
					}

				} catch (Exception e) {

					isError = true;
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.e("solicitando", "onFailure: "+response);

				isError = true;
				try {
					pDialog.dismiss();
				} catch (Exception e1) {
				}
				puente.sendEmptyMessage(1500);
			}

			@Override
			public void onFinish() {

				if (isError)
				{
					try {
						pDialog.dismiss();
					} catch (Exception e) {
					}
					puente.sendEmptyMessage(1500);
				}
			}

		});
	}

}
