package com.imaginamos.usuariofinal.taxisya.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Conf {

	public static String tag_shared = "taxisya";
	private SharedPreferences prefs;
	private Editor editor;

	private String ISLOGIN =  "ISLOGIN";

	private String USER = "USER";
	private String PASS = "PASS";
	private String UUID = "uuid";
	private String IDUSER="IDUSER";
	private String NAME = "NAME";
	private String PHONE = "PHONE";
	private String CARD_DEFAULT = "CARD_DEFAULT";
	private String USER_EMAIL = "USER_EMAIL"; // para transaccion

	private String ISFIRST = "isFirst";
	private String IDSERVICE ="IDSERVICE";
	private String IDDRIVER ="IDDRIVER";

	private String APPVERSION =  "APPVERSION";

	public Conf(Context context)
	{
		prefs = context.getSharedPreferences(Conf.tag_shared,Context.MODE_PRIVATE);
		editor = prefs.edit();
	}

	public void setLogin(boolean is)
	{
		editor.putBoolean(ISLOGIN, is);
		editor.commit();
	}

	public boolean getLogin()
	{
		return prefs.getBoolean(ISLOGIN, false);
	}

	public void setUser(String user)
	{
		editor.putString(USER, user);
		editor.commit();
	}

	public String getUser()
	{
		return prefs.getString(USER, null);
	}

	public void setPass(String pass)
	{
		editor.putString(PASS, pass);
		editor.commit();
	}

	public String getPass()
	{
		return prefs.getString(PASS, null);
	}

	public void setName(String name)
	{
		editor.putString(NAME, name);
		editor.commit();
	}

	public String getName()
	{
		return prefs.getString(NAME, null);
	}

	public void setPhone(String phone)
	{
		editor.putString(PHONE, phone);
		editor.commit();
	}

	public String getPhone()
	{
		return prefs.getString(PHONE, null);
	}

	public void setUuid(String uuid)
	{
		editor.putString(UUID, uuid);
		editor.commit();
	}

	public String getUuid()
	{
		return prefs.getString(UUID, null);
	}
	public void setIdUser(String iduser)
	{
		editor.putString(IDUSER, iduser);
		editor.commit();
	}

	public String getIdUser()
	{
		return prefs.getString(IDUSER, null);
	}

	public void setIsFirst(boolean isFirst)
	{
		editor.putBoolean(ISFIRST, isFirst);
		editor.commit();
	}

	public boolean getIsFirst()
	{
		return prefs.getBoolean(ISFIRST, true);
	}

	public String getServiceId()
	{
		return prefs.getString(IDSERVICE, null);
	}

	public void setServiceId(String idservice)
	{
		editor.putString(IDSERVICE, idservice);
		editor.commit();
	}
	public int getAppVersion()
	{
		return prefs.getInt(APPVERSION, 0);
	}

	public void setAppVersion(int appversion)
	{
		editor.putInt(APPVERSION, appversion);
		editor.commit();
	}
	public String getDriverId()
	{

		return prefs.getString(IDDRIVER, null);
	}

	public void setDriverId(String iddriver)
	{
		editor.putString(IDDRIVER, iddriver);
		editor.commit();
	}

	// card default
	public String getCardDefault() {
		return prefs.getString(CARD_DEFAULT, null);
	}

	public void setCardDefault(String card) {
		editor.putString(CARD_DEFAULT, card);
		editor.commit();
	}

	public String getUserEmail() {
		return prefs.getString(USER_EMAIL, null);
	}

	private void setUserEmail(String email) {
		editor.putString(USER_EMAIL, email);
		editor.commit();
	}

}
