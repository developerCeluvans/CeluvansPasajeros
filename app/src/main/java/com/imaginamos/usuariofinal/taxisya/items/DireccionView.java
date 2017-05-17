package com.imaginamos.usuariofinal.taxisya.items;

import com.imaginamos.usuariofinal.taxisya.models.Direccion;
import com.imaginamos.usuariofinal.taxisya.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DireccionView extends LinearLayout {

	public TextView direccion;
	public TextView nombre;

	public DireccionView(Context context) {
		super(context);
		inflate(context, R.layout.direccion_register, this);
		direccion = (TextView) findViewById(R.id.linea_direccion);
		nombre = (TextView) findViewById(R.id.type_direccion);
	}

	public void setDireccion(Direccion dir) {
		String full_dir = null;
		if (dir.getAddress()!= null) {
	       full_dir = dir.getAddress();
		}
		else {
			full_dir = dir.getIndex() + " " + dir.getComp1() + " " + dir.getComp2() + " - " + dir.getNumero() + " " + dir.getBarrio();
		}

		nombre.setText(dir.getName());
		direccion.setText(full_dir);

		direccion.setEnabled(false);
	}

}
