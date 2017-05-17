package com.imaginamos.usuariofinal.taxisya.adapter;

import java.util.ArrayList;

import com.imaginamos.usuariofinal.taxisya.items.HistoricoView;
import com.imaginamos.usuariofinal.taxisya.models.Historico;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class HistoricoAdapter extends BaseAdapter {

private ArrayList<Historico> servicios;
	
	public HistoricoAdapter(ArrayList<Historico> servicios) {
		this.servicios = servicios;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return servicios.size();
	}

	public Object getItem(int position) {
		return servicios.get(position);
	}

	
	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		HistoricoView view;
		
		if (convertView == null) 
			view = new HistoricoView(parent.getContext());
		else
			view = (HistoricoView) convertView;
		
		view.setAgendado(servicios.get(position));
		
		return view;
	}
}
