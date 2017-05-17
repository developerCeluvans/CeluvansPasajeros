package com.imaginamos.usuariofinal.taxisya.adapter;

import java.util.ArrayList;

import com.imaginamos.usuariofinal.taxisya.items.DireccionView;
import com.imaginamos.usuariofinal.taxisya.models.Direccion;
import com.imaginamos.usuariofinal.taxisya.io.ApiAdapter;
import com.imaginamos.usuariofinal.taxisya.io.ServiceResponse;
import com.imaginamos.usuariofinal.taxisya.R;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DireccionAdapter extends BaseAdapter {
	private ArrayList<Direccion> direcciones;
	private final int INVALID = -1;
	protected int DELETE_POS = -1;


	public DireccionAdapter(ArrayList<Direccion> dirs) {
		this.direcciones=dirs;
		notifyDataSetChanged();
	}

	public void onSwipeItem(boolean isRight, int position) {
		// TODO Auto-generated method stub
		Log.v("SWIPE", "onSwipeItem 1 ");
		if (isRight == false) {
			DELETE_POS = position;
		} else if (DELETE_POS == position) {
			DELETE_POS = INVALID;
		}
		//
		notifyDataSetChanged();
	}

	public void deleteItem(int pos) {
		//
		String address_id = direcciones.get(pos).getId();
		direcciones.remove(pos);
		DELETE_POS = INVALID;
		notifyDataSetChanged();
		// elimina reg
		ApiAdapter.getApiService().delAddress(address_id, new Callback<ServiceResponse>() {
			@Override
			public void success(ServiceResponse data, Response response) {
				if (data.getError() == 1) {
					Log.v("DEL_ADDRESS", "Ok");
				}
				else
					Log.v("DEL_ADDRESS", "Ok");
			}

			@Override
			public void failure(RetrofitError error) {
				Log.v("DEL_ADDRESS", "fail");
			}
		});

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return direcciones.size();
	}

	@Override
	public Direccion getItem(int position) {
		// TODO Auto-generated method stub
		return direcciones.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DireccionView view;
		if (convertView == null) //NO existe, creamos uno
			view = new DireccionView(parent.getContext());
		else					//Existe, reutilizamos
			view = (DireccionView) convertView;

		TextView text = ViewHolderPattern.get(view, R.id.linea_direccion);
		Button delete = ViewHolderPattern.get(view, R.id.delete);

		if (DELETE_POS == position) {
			Log.v("SWIPE", "onSwipeItem 2 ");
			delete.setVisibility(View.VISIBLE);
		} else {
			Log.v("SWIPE", "onSwipeItem 3 ");
			delete.setVisibility(View.GONE);
		}
		delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteItem(position);
			}
		});
		/**
		 * Ahora tenemos que darle los valores correctos, para ello usamos
		 * el método setRectangulo pasándole el rectángulo a mostrar
		 * y finalmente devolvemos el view.
		 */

		view.setDireccion(direcciones.get(position));

		return view;
	}


	public static class ViewHolderPattern {
		// I added a generic return type to reduce the casting noise in client
		// code
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}
}
