package com.imaginamos.usuariofinal.taxisya.comm;

import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.imaginamos.usuariofinal.taxisya.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.entity.StringEntity;

public class Connect {

	  public static final String BASE_URL_IP = BuildConfig.HOST + "/";
	  public static final String BASE_URL = BuildConfig.HOST + "/public/";
	  public static final String BASE_NODE = BuildConfig.HOST_NODE;
	  private static final String CONNECTIVITY_QUALITY_CHECKING = "http://www.taxisya.co/dev/";

	  private static final int timeout=40;

	  public static AsyncHttpClient syncHttpClient= new SyncHttpClient();
	  public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	  {
		  getClient().setTimeout(timeout * 1000);
		  getClient().get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	  {
		  getClient().setTimeout(timeout * 1000);
		  getClient().post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void sendJson(Context context, String url, RequestParams params, JSONObject bodyAsJson,
			AsyncHttpResponseHandler responseHandler) {
		try {
			//ByteArrayEntity entity = new ByteArrayEntity(bodyAsJson.toString().getBytes("UTF-8"));
			StringEntity stringEntity = new StringEntity(bodyAsJson.toString());
			Log.v("sendJson","String Json "+bodyAsJson.toString());
			//client.post(context,getAbsoluteUrl(url), entity, "application/json", responseHandler);
			getClient().post(context,getAbsoluteUrl(url), stringEntity, "application/json", responseHandler);
		} catch (Exception e) {
			Log.e("sendJson error", e.toString());
		}
	  }

	  public static void connectivityQualityTest(AsyncHttpResponseHandler responseHandler)
	  {
		  getClient().setTimeout(timeout * 1500);
		  getClient().get(CONNECTIVITY_QUALITY_CHECKING, null,responseHandler);
	  }

	  public static void postNode(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	  {
		  getClient().setTimeout(timeout * 1000);
		  getClient().post(getAbsoluteUrlNode(url), params, responseHandler);
	  }


	  private static String getAbsoluteUrlNode(String relativeUrl)
	  {
		  Log.e("BASE_NODE", BASE_NODE + relativeUrl);
	      return BASE_NODE + relativeUrl;
	  }

	  private static String getAbsoluteUrl(String relativeUrl)
	  {
		  Log.e("BASE_URL", BASE_URL + relativeUrl);
	      return BASE_URL + relativeUrl;
	  }

	  public static void getWithAbsoluteURl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	  {
		  getClient().setTimeout(timeout * 1000);
		  getClient().get(url, params, responseHandler);
	  }

	private static AsyncHttpClient getClient()
	{
		// Return the synchronous HTTP client when the thread is not prepared
		if (Looper.myLooper() == null)
			return syncHttpClient;
		return asyncHttpClient;
	}
}
