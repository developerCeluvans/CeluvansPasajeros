package com.imaginamos.usuariofinal.taxisya.adapter;

import java.util.ArrayList;

import com.imaginamos.usuariofinal.taxisya.interfaces.onCancelSchedule;
import com.imaginamos.usuariofinal.taxisya.items.AgendadoView;
import com.imaginamos.usuariofinal.taxisya.models.Agendado;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class AgendadoAdapter extends BaseAdapter{

	private ArrayList<Agendado> agendados;

	private onCancelSchedule oncancelSchedule;

	public AgendadoAdapter(ArrayList<Agendado> agendados,onCancelSchedule oncancelSchedule) {
		super();
		this.agendados = agendados;
		this.oncancelSchedule = oncancelSchedule;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return agendados.size();
	}

	@Override
	public Object getItem(int arg0) {
		return agendados.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		AgendadoView view;
		if (convertView == null)
			view = new AgendadoView(parent.getContext(),oncancelSchedule);
		else
			view = (AgendadoView) convertView;

		view.setAgendado(agendados.get(position));

		return view;
	}

}
