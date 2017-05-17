package com.imaginamos.usuariofinal.taxisya.dialogs;



import com.imaginamos.usuariofinal.taxisya.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialogos{
	
	int id_message = 0;
	AlertDialog dialog;
	
	public Dialogos(Context context,int idtext)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() 
		{
		     public void onClick(DialogInterface dialog, int id) 
		     {
		        dialog.dismiss();
		     }
		});
		
		dialog = builder.create();
		dialog.setTitle("Taxis Ya");
		dialog.setMessage(context.getResources().getString(idtext));
		dialog.show();
		
	}

	public void dismiss()
	{
		try {
			dialog.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void setCancelable()
	{
		dialog.setCancelable(false);
	}

}
