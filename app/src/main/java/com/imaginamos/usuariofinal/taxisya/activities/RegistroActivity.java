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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.models.Target;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class RegistroActivity extends Activity implements OnClickListener {

	private ImageView volver;

	private EditText name, user, pass, cellphone;

	private String usuario,password,nombre,phone,uuid;

	private int target_option = 0;

	private ProgressDialog pDialog;

	private Conf conf;

	private String index;

	private String comp1;

	private String comp2;

	private String no;

	private String barrio;

	private String obs;

	private String latitud;

	private String longitud;

	private Button btnRegister;

	private Button btnLogin;

	@Override
	public void onRestart() {
		super.onRestart();
		overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

		setContentView(R.layout.activity_registro);

		uuid = Utils.uuid(this);

		conf  = new Conf(this);

		volver = (ImageView) findViewById(R.id.btn_volver);
		volver.setOnClickListener(this);

		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(this);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(this);

		name = (EditText) findViewById(R.id.editTxtNombre);
		user = (EditText) findViewById(R.id.editTxtUsuario);
		pass = (EditText) findViewById(R.id.editTxtContrasena);
		cellphone = (EditText) findViewById(R.id.editTxtCelular);

		Bundle reicieveParams = getIntent().getExtras();

		target_option = reicieveParams.getInt("target");

		if(target_option == Target.TAXI_TARGET)
		{

			index = reicieveParams.getString("index");
			comp1 = reicieveParams.getString("comp1");
			comp2 = reicieveParams.getString("comp2");
			no = reicieveParams.getString("no");
			barrio = reicieveParams.getString("barrio");
			obs = reicieveParams.getString("obs");
			latitud = reicieveParams.getString("latitud");
			longitud = reicieveParams.getString("longitud");
		}

	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.btnRegister:
				registerService();
				Intent intent1 = new Intent(RegistroActivity.this, MapaActivitys.class);
				intent1.putExtra("target", target_option);
				startActivityForResult(intent1, 0);
				break;
			case R.id.btn_volver:
				Intent i = new Intent(RegistroActivity.this, HomeActivity.class);
				startActivity(i);
				finish();
				break;
			case R.id.btnLogin:
				Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
				intent.putExtra("target", target_option);
				startActivityForResult(intent, 0);
				finish();
				break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK)
		{
			goToActivity(intent.getExtras().getInt("target_for", 0));
		}

	}

	private void registerService() {

		nombre = name.getText().toString();

		usuario = user.getText().toString();

		password = pass.getText().toString();

		phone = cellphone.getText().toString();

		if(uuid != null)
		{
			if (checkregisterdata(nombre, ".", usuario, password, phone))
			{

				MiddleConnect.registry(this, nombre, password, usuario, uuid, phone, new AsyncHttpResponseHandler() {

							@Override
							public void onStart()
							{
								pDialog = new ProgressDialog(RegistroActivity.this);
								pDialog.setMessage(getString(R.string.texto_registrando));
								pDialog.setIndeterminate(false);
								pDialog.setCancelable(false);
								pDialog.show();
							}

							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								String response = new String(responseBody);
								try {

									JSONObject responsejson = new JSONObject(response);

									if(responsejson.getInt("error") == 0)
									{
										conf.setName(nombre);
										conf.setUser(usuario);
										conf.setPhone(phone);
										conf.setPass(Utils.md5(password));
										conf.setUuid(uuid);
										conf.setIsFirst(false);
										conf.setLogin(true);
										conf.setIdUser(responsejson.getString("id"));
										//goToActivity(target_option);

									}else
									{
										Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
										vibrator.vibrate(200);
										Toast.makeText(getApplicationContext(),getResources().getString(R.string.user_exist,usuario),Toast.LENGTH_LONG).show();
									}
								} catch (Exception e) {
									err_register();
								}

							}

							@Override
							public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
								err_register();
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

			} else {
				err_register();
			}
		}else{
			uuid = Utils.uuid(this);
			Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_net), Toast.LENGTH_SHORT).show();
		}


	}

	protected void goToActivity(int target_option) {


		switch (target_option)
		{

			case Target.TAXI_TARGET:

				// Intent i = new Intent(RegistroActivity.this,SolicitandoActivity.class);

				// i.putExtra("index", index);
				// i.putExtra("comp1", comp1);
				// i.putExtra("comp2", comp2);
				// i.putExtra("no", no);
				// i.putExtra("barrio",barrio);
				// i.putExtra("obs", obs);
				// i.putExtra("latitud", String.valueOf(latitud));
				// i.putExtra("longitud", String.valueOf(longitud));
				// i.putExtra("target", Target.TAXI_TARGET);

				// startActivity(i);
				finish();

				break;
			case Target.HISTORY_TARGET:
				Intent i2 = new Intent(RegistroActivity.this, HistorialActivity.class);
				startActivity(i2);
				finish();
				break;
			case Target.AGEND_TARGET:
				Intent i3 = new Intent(RegistroActivity.this, AgendarActivity.class);
				startActivity(i3);
				finish();
				break;
			case Target.MAP_TARGET:
				Intent i4 = new Intent(RegistroActivity.this, MapaActivitys.class);
				startActivity(i4);
				finish();
				break;
			case Target.ADDRESS_TARGET:
				Intent i5 = new Intent(RegistroActivity.this, MapaActivitys.class);
				setResult(2, i5);
				finish();
		}
	}

	private void err_register() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		Toast.makeText(getApplicationContext(),getResources().getString(R.string.registry_error), Toast.LENGTH_SHORT).show();
	}

	private boolean checkregisterdata(String nombre, String apellido,String username, String password, String phone)
	{

		if (nombre.trim().equals("") || username.trim().equals("") || password.trim().equals("") || phone.trim().equals(""))
		{
			return false;

		} else {

			return true;
		}

	}

}
