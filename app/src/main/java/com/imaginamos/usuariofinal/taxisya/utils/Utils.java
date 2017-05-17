package com.imaginamos.usuariofinal.taxisya.utils;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.imaginamos.usuariofinal.taxisya.activities.HomeActivity;
import com.imaginamos.usuariofinal.taxisya.models.Conf;

public class Utils {

	public static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

	public Utils() {}

	public static String uuid(Context context)
	{
		return new Conf(context).getUuid();
	}

	public static void log(String Tag,String log)
	{
		if(log != null && log != "")
		{
			Log.e(Tag, log);
		}
	}

	public static String md5(String s) {

		MessageDigest m = null;

		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		m.update(s.getBytes(), 0, s.length());
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}

	public static void showErrorDialog(int code,Activity activity)
	{
		GooglePlayServicesUtil.getErrorDialog(code, activity, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

	public static boolean checkPlayServices(Activity activity)
	{
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (status != ConnectionResult.SUCCESS)
		{

			if (GooglePlayServicesUtil.isUserRecoverableError(status))
			{
				Utils.showErrorDialog(status,activity);
			} else
			{
				Toast.makeText(activity, "This device is not supported.",Toast.LENGTH_LONG).show();
			}

			return false;

		}
		return true;
	}

	public static List<Address> getFromLocation(double lat, double lng, int maxResult){

		String latlng = lat+"#"+lng;

		latlng =  latlng.replace(",", ".");

		latlng =  latlng.replace("#", ",");

		Log.e("getFromLocation", "latlng " + latlng );

		String address = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng+"&sensor=true&language="+Locale.getDefault().getCountry();

		Log.e("getFromLocation","getFromLocation " + address);

		HttpGet httpGet  = new HttpGet(address);

		HttpParams httpParameters = new BasicHttpParams();

		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient client = new DefaultHttpClient(httpParameters);

		HttpResponse response;

		StringBuilder stringBuilder = new StringBuilder();

		List<Address> retList = null;

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1)
			{
				stringBuilder.append((char) b);
			}

			JSONObject jsonObject = new JSONObject();

			jsonObject = new JSONObject(stringBuilder.toString());

			retList = new ArrayList<Address>();


			if("OK".equalsIgnoreCase(jsonObject.getString("status")))
			{
				JSONArray results = jsonObject.getJSONArray("results");

				for (int i=0;i<results.length();i++ )
				{
					JSONObject result = results.getJSONObject(i);
					String indiStr 	  = result.getString("formatted_address");
					Address addr 	  = new Address(Locale.getDefault());
					addr.setAddressLine(0, indiStr);
					retList.add(addr);
				}
			}


		} catch (Exception e) {
			retList = null;
			Log.e(Utils.class.getName(), "Error parsing Google geocode webservice response.", e);
		}
		return retList;
	}
}
