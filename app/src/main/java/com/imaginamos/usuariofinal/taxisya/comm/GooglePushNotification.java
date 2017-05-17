package com.imaginamos.usuariofinal.taxisya.comm;

import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.imaginamos.usuariofinal.taxisya.models.Conf;

public class GooglePushNotification {
	
	private static String SENDER_ID = "239425727079";
	
	private Context context;
	
	public  GooglePushNotification(Context context) 
	{
		this.context =  context;
	    new Registry().execute("");
	}
	
	public int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager() .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	class Registry extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {
			
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
			
			Conf conf = new Conf(context);
			

				try {
					
					String uuid =  gcm.register(SENDER_ID);
					
					Log.e("UUID GENERADO", uuid);
					
					conf.setAppVersion(getAppVersion(context));
					
					conf.setUuid(uuid);
					
				} catch (IOException e) {
					
					Log.e("ERROR UUID", e.toString()+"");

				}

			
			return null;
		}
		
	}

}
