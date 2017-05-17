package com.imaginamos.usuariofinal.taxisya.items;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imaginamos.usuariofinal.taxisya.activities.ReclamoActivity;
import com.imaginamos.usuariofinal.taxisya.models.Historico;
import com.imaginamos.usuariofinal.taxisya.comm.Connect;
import com.imaginamos.usuariofinal.taxisya.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HistoricoView extends LinearLayout{

	private ImageView imagen, reclamo;
	private TextView nombre,placa,marca, telefono;	
	public Intent callIntent;
	private Button btnReclamo;
	
	public HistoricoView(Context context) 
	{
		super(context);
		inflate(context, R.layout.historial_fila, this);
		imagen = (ImageView) findViewById(R.id.fotoTaxista);
		nombre = (TextView) findViewById(R.id.nombre);
		placa = (TextView) findViewById(R.id.placa);
		marca = (TextView) findViewById(R.id.marca);
		telefono = (TextView) findViewById(R.id.telefonoTaxista);
		//reclamo = (ImageView) findViewById(R.id.btn_reclamo);
		btnReclamo = (Button) findViewById(R.id.btnReclamo);
	}
	
	private void call(String number) {
	    try {
	        callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+number));
	        getContext().startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	        Log.e("Llamada:", "Error al llamar:", activityException);
	    }
	}
	
	public void setAgendado(Historico servicio) {
		
		if(servicio.getFoto()!=null && !servicio.getFoto().equals("null"))
		{
			descargarFoto(Connect.BASE_URL_IP+servicio.getFoto());
		}
		
		final String id_service = servicio.getIdService();
		nombre.setText(servicio.getNombre());
		placa.setText(servicio.getPlaca());
		marca.setText(servicio.getMarca()+" - "+servicio.getModelo());
		telefono.setText(servicio.getTelefono());
		
		telefono.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				call((String) telefono.getText());
			}
		});
		
		btnReclamo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getContext(), ReclamoActivity.class);
				i.putExtra("service_id", id_service);
				getContext().startActivity(i);
			}
		});
	}
	
	private void descargarFoto(String imageHttpAddress) {
        
		try {
			ImageLoader.getInstance().displayImage(imageHttpAddress, imagen);
		} catch (Exception e) {
			Log.e("ERROR", "ERROR CARGANDO IMAGEN");
		}
    
	}

}
