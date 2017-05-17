package com.imaginamos.usuariofinal.taxisya.comm;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.imaginamos.usuariofinal.taxisya.activities.HomeActivity;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 EJEMPLO:
 		MiddleConnect.login("usename","password",new AsyncHttpResponseHandler(){
		     @Override
		     public void onStart() { }

		     @Override
		     public void onSuccess(String response) {}

		     @Override
		     public void onFailure(Throwable e, String response) {}

		     @Override
		     public void onFinish() {}
		});
*/

public class MiddleConnect {

	public static byte LOGIN_FAIL = 1;

	public static void getPlaces(AsyncHttpResponseHandler responseHandler)
	{
	     Log.v("USER_SERVICE", "        getPlaces: ");

		Connect.post("archies/public/index.php/index.php/city", null, responseHandler);
	}

	public static void login(Context context,String user, String password,String uuid,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("type", HomeActivity.TYPE_USER);

		params.put("login", user);

		params.put("pwd", password);

		params.put("uuid", uuid);

	    Log.v("USER_SERVICE", "        login: ");
	    Log.v("USER_SERVICE", "        login: " + password + " " + user + " " + uuid);

		Connect.post(context.getResources().getString(R.string.login), params, responseHandler);
	}



	public static void registry(Context context,String name, String password,String usuario,String uuid,String phone,AsyncHttpResponseHandler responseHandler)
	{

		RequestParams params = new RequestParams();

		params.put("type", HomeActivity.TYPE_USER);
		params.put("name", name);
		params.put("lastname", ".");
		params.put("email", usuario);
		params.put("login", usuario);
		params.put("pwd", password);
		params.put("token", uuid);
		params.put("cellphone", phone);
		params.put("uuid", uuid);

	    Log.v("USER_SERVICE", "        register: ");

		Connect.post(context.getResources().getString(R.string.registry), params, responseHandler);

	}

	public static void update(Context context,String name, String password,String usuario,String uuid,String phone,AsyncHttpResponseHandler responseHandler)
	{

		RequestParams params = new RequestParams();

		params.put("type", HomeActivity.TYPE_USER);
		params.put("name", name);
		params.put("lastname", ".");
		params.put("email", usuario);
		params.put("login", usuario);
		params.put("pwd", password);
		params.put("token", uuid);
		params.put("cellphone", phone);
		params.put("uuid", uuid);

	    Log.v("USER_SERVICE", "        update: ");

		Connect.post(context.getResources().getString(R.string.update), params, responseHandler);

	}

	public static void is_logued(Context context,String user, String uuid,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("login", user);

		params.put("uuid", uuid);

	    Log.v("USER_SERVICE", "        is_logued: ");

		Connect.post(context.getResources().getString(R.string.is_logued), params, responseHandler);
	}

	public static void logout(Context context , String user, String password, AsyncHttpResponseHandler responseHandler)
	{
	    RequestParams  params  = new RequestParams();

	    params.put("login", user);

	    params.put("pwd", password);

	    Log.v("USER_SERVICE", "        logout: ");

	    Connect.post(context.getResources().getString(R.string.logout), params, responseHandler);
	}

	public static void getServices(Context context , String user_id, String uuid,String month,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);

		params.put("uuid",uuid);

		params.put("month",month);

	    Log.v("USER_SERVICE", "        getServices: ");

		Connect.post(context.getResources().getString(R.string.services_user), params, responseHandler);
	}

	public static void getServices(Context context , String user_id, String uuid,String year,String month,String day,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);

		params.put("uuid",uuid);

		params.put("year",year);

		params.put("month",month);

		params.put("day",day);

	    Log.v("USER_SERVICE", "        getServices: ");

		Connect.post(context.getResources().getString(R.string.services_user), params, responseHandler);
	}

	public static void getAddress(Context context,String  user_id,String  uuid, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);

		params.put("uuid",uuid);

	    Log.v("USER_SERVICE", "        getAddress: " + context.getResources().getString(R.string.getaddress) + " " + user_id + " " + uuid);

		Connect.post(context.getResources().getString(R.string.getaddress), params, responseHandler);
	}

	public static void delAddress(Context context,String id, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("id", id);

	    Log.v("USER_SERVICE", "        delAddress: " + context.getResources().getString(R.string.deladdress) + " " + id);

		Connect.post(context.getResources().getString(R.string.deladdress), params, responseHandler);
	}

	public static void addAddress(Context context, String index_id, String comp1, String comp2, String no, String barrio, String obs, String user_id,
		String user_pref_order, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("index_id", index_id);

		params.put("comp1", comp1);

		params.put("comp2", comp2);

		params.put("no", no);

		params.put("barrio", barrio);

		params.put("obs", obs);

		params.put("user_id", user_id);

		params.put("user_pref_order", user_pref_order);

	    Log.v("USER_SERVICE", "        addAddress: " + context.getResources().getString(R.string.addaddress) + " " + params.toString());

		Connect.post(context.getResources().getString(R.string.addaddress), params, responseHandler);
	}

	public static void cancelService(String url_cancel_current, AsyncHttpResponseHandler responseHandler)
	{
		Log.v("USER_SERVICE", "        cancelService: ");

		Connect.post(url_cancel_current, null, responseHandler);
	}

	public static void cancelServiceBySystem(String url_cancel_current, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("by_system", "true");

	    Log.v("USER_SERVICE", "        cancelServiceBySystem: ");

		Connect.post(url_cancel_current, params, responseHandler);
	}

	public static void getService(Context context,String user_id,
										String latitud, String longitud,String index,String comp1, String comp2 ,
												String no,String barrio,String obs,String uuid,
													 AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("lat", latitud);

		params.put("lng", longitud);

		params.put("index_id", index);

		params.put("comp1", comp1);

		params.put("comp2", comp2);

		params.put("no", no);

		params.put("barrio", barrio);

		params.put("obs", obs);

		params.put("uuid", uuid);

		Log.v("USER_SERVICE", "        params " + params.toString());

		Log.v("USER_SERVICE", "        getService: ");

		Connect.post(context.getResources().getString(R.string.request_service,user_id), params, responseHandler);
	}


	public static void getServiceAddress(Context context,String user_id,String latitud, String longitud,String address,String barrio,String uuid, String payType,String payReference,String userEmail, String cardReference, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("lat", latitud);

		params.put("lng", longitud);

		params.put("address", address);

		params.put("barrio", barrio);

		params.put("uuid", uuid);

		params.put("pay_type",payType);

		params.put("pay_reference",payReference);

		params.put("user_email",userEmail);

		params.put("user_card_reference",cardReference);

		Log.v("USER_SERVICE", "        params " + params.toString());

		Log.v("USER_SERVICE", "        getService: ");

		Connect.post(context.getResources().getString(R.string.request_service_address,user_id), params, responseHandler);
	}


	public static void checkStatusService(Context context, String user_id,String service_id,String uuid, AsyncHttpResponseHandler responseHandler) throws JSONException
	{
		RequestParams params = new RequestParams();
        Log.v("checkStatusService","service_id = " + service_id + " user_id = " + user_id);
        if (service_id != null ) {
		    params.put("user_id", user_id);
		    params.put("service_id", service_id);

	        Log.v("USER_SERVICE", "        checkStatusService: ");

			Connect.post(context.getResources().getString(R.string.checkstatuservice), params, responseHandler);

			// try {
			// 	Connect.post(context.getResources().getString(R.string.checkstatuservice), params, responseHandler);
			// }
			// catch(Exception e) {

			// }
        }
        else {
        	JSONObject user = new JSONObject();
            user.put("user_id", user_id);
            Log.e("USER_ID", user.toString());

	        Log.v("USER_SERVICE", "        checkStatusService: service_id = null");

   		    Connect.sendJson(context, context.getResources().getString(R.string.checkstatuservice),params, user, responseHandler);
        }
	}

	public static void finishSchedule(Context context , String user_id, String id_schedule,String uuid,String score,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);
		params.put("schedule_id", id_schedule);
		params.put("uuid", uuid);
		params.put("qualification", score);

	    Log.v("USER_SERVICE", "        finishSchedule: ");

		Connect.post(context.getResources().getString(R.string.finish_schedule), params, responseHandler);

	}

	public static void reclamo(Context context,String uuid,String service_id,String description, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("uuid", uuid);

		params.put("service_id", service_id);

		params.put("descript", description);

	    Log.v("USER_SERVICE", "        reclamo:");

		Connect.post(context.getResources().getString(R.string.reclamo), params, responseHandler);
	}

	public static void calificar(Context context,String uuid,String id_user,String service_id,String score, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", id_user);

		params.put("service_id", service_id);

		params.put("qualification", score);

		params.put("uuid", uuid);
		Log.v("CALIFICA","user_id="+id_user + " service_id=" + service_id + " qualification=" + score + " uuid=" + uuid);

	    Log.v("USER_SERVICE", "        activity_calificar: ");

		Connect.post(context.getResources().getString(R.string.calificar), params, responseHandler);
	}

//	public static void agend(Context context,String user_id, String uuid,String fecha_hora,
//												int motivo,String indice_direccion,String
//													comp1,String comp2,String no
//														,String Barrio ,String obs,String
//															destination,AsyncHttpResponseHandler responseHandler)
	public static void agend(Context context,String user_id, String uuid,String fecha_hora,
							 int motivo, String address,String Barrio ,String obs,String
									 destination,String lat, String lng, AsyncHttpResponseHandler responseHandler)

	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);

		params.put("uuid", uuid);

		params.put("service_date_time", fecha_hora);

		params.put("schedule_type", String.valueOf(motivo));

//		params.put("address_index", indice_direccion);
//
//		params.put("comp1", comp1);
//
//		params.put("comp2", comp2);
//
//		params.put("no", no);

		params.put("address", address);

		params.put("city_lat", lat);

		params.put("city_lng", lng);

		params.put("barrio", Barrio);

		params.put("obs", obs);

		if (motivo == 2 || motivo == 3)
		{
			params.put("destination", destination);
		}

		Log.v("USER_SERVICE", "        agend: ");
//		Log.v("USER_SERVICE_AGEND", " -d user_id=" + user_id + " -d uuid=" + uuid + " -d service_date_time" + fecha_hora + " -d schedule_type=" + String.valueOf(motivo) + " -d address_index=" + indice_direccion + " -d comp1=" + comp1 + " -d comp2=" + comp2 + " -d no=" + no + " -d barrio=" + Barrio + " -d obs=" + obs);
		Log.v("USER_SERVICE_AGEND", " -d user_id=" + user_id + " -d uuid=" + uuid + " -d service_date_time" + fecha_hora + " -d schedule_type=" + String.valueOf(motivo) + " -d address=" + address  + " -d barrio=" + Barrio + " -d obs=" + obs);

		//Connect.postNode(context.getResources().getString(R.string.agend), params, responseHandler);
		Connect.post(context.getResources().getString(R.string.agend), params, responseHandler);
	}

	public static void myAgend(Context context ,String user_id, String uuid,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);

		params.put("uuid",uuid);

	    Log.i("USER_SERVICE", "        myAgend: "+uuid+" user_id: "+user_id);

		Connect.post(context.getResources().getString(R.string.myanged), params, responseHandler);
	}

	public static void cancelSchedule(Context context ,String user_id,String uuid,String id_schedule,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("user_id", user_id);

		params.put("uuid",uuid);

		params.put("schedule_id", id_schedule);

		Log.v("USER_SERVICE", "        cancelSchedule: ");

		Connect.postNode(context.getResources().getString(R.string.cancel_schedule), params, responseHandler);
	}

	public static void getDriver(Context context,String driver_id,String user_id,String uuid,AsyncHttpResponseHandler responseHandler)
	{

		RequestParams params = new RequestParams();

		params.put("driver_id", driver_id);

		params.put("id", user_id);

		params.put("uuid", uuid);

		Log.v("USER_SERVICE", "        getDriver: ");

		Connect.post(context.getResources().getString(R.string.driver), params, responseHandler);
	}

	public static void sendMailReset(Context context,String email,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("email", email);

	    Log.v("USER_SERVICE", "        sendMailReset: ");

		Connect.post(context.getResources().getString(R.string.resetpass), params, responseHandler);
	}

	public static void sendMailResetConfirm(Context context,String email,String token,String password,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();

		params.put("email", email);
		params.put("token", token);
		params.put("password", password);

	    Log.v("USER_SERVICE", "        sendMailResetConfirm: ");
	    Log.v("USER_SERVICE", "        sendMailResetConfirm: " + password);

		Connect.post(context.getResources().getString(R.string.code_pass), params, responseHandler);
	}

	public static void getAddressFromLatLng(String url,String latlng,AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		params.put("latlng", latlng);
		params.put("sensor", "false");

		Log.v("USER_SERVICE", "        getAddressFromLatLng: ");

		Connect.getWithAbsoluteURl(url, params, responseHandler);
	}

	public static void getAppVersions(Context context ,AsyncHttpResponseHandler responseHandler)
	{
	    RequestParams params = new RequestParams();

		params.put("app", "");

		Log.v("USER_SERVICE", "        getAppVersions: ");

		Connect.post(context.getResources().getString(R.string.app_versions), params, responseHandler);
	}

	public static void testConnectivityQuality(AsyncHttpResponseHandler responseHandler){
		Log.v("USER_SERVICE", "        ConnectivityQualityTest: ");
		Connect.connectivityQualityTest(responseHandler);
	}

	public static void confirmTicker(Context context,String ticket,AsyncHttpResponseHandler responseHandler) {

		RequestParams params = new RequestParams();
		params.put("ticket", ticket);
		Log.v("USER_SERVICE", "confirmTicket: ");
		Connect.post(context.getResources().getString(R.string.aceptar), params, responseHandler);
	}

}
