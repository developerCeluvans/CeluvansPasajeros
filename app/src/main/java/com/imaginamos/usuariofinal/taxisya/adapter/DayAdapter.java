package com.imaginamos.usuariofinal.taxisya.adapter;

import java.util.ArrayList;

import com.imaginamos.usuariofinal.taxisya.items.DayView;
import com.imaginamos.usuariofinal.taxisya.models.DiaMes;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class DayAdapter extends BaseAdapter {

    private ArrayList<DiaMes> dias;
    int selectedItem;

	public DayAdapter(ArrayList<DiaMes> dias) {
		super();
		this.dias = dias;
		notifyDataSetChanged();
	}

	public int getCount() {
		return dias.size();
	}


	public Object getItem(int position) {
		return dias.get(position);
	}


	public long getItemId(int position) {
		return position; //0;
	}


	public View getView(final int position, View convertView, ViewGroup parent) {

		DayView view;
		if (convertView == null) //NO existe, creamos uno
			view = new DayView(parent.getContext());
		else					//Existe, reutilizamos
			view = (DayView) convertView;

		/**
		 * Ahora tenemos que darle los valores correctos, para ello usamos
		 * el método setRectangulo pasándole el rectángulo a mostrar
		 * y finalmente devolvemos el view.
		 */

		//view.setDay(dias.get(position));
		//view.setDay(dias.get(position));
		
		view.setOnClickListener(new OnClickListener() {
	        private int pos = position;
	        @Override
	        public void onClick(View v) {
	            selectedItem = pos;
	        }
	      });
		
		if (position == selectedItem) {
			Log.v("getView","position");
			view.setDay(dias.get(position),1);
		}
		else {
			view.setDay(dias.get(position),0);
		}
		
		return view;
	}
}
