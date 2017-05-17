package com.imaginamos.usuariofinal.taxisya.items;

import org.json.JSONObject;

import com.imaginamos.usuariofinal.taxisya.models.DiaMes;
import com.imaginamos.usuariofinal.taxisya.activities.HistorialActivity;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DayView extends LinearLayout {
	private TextView dia, n_dia;
	private String id_user, uuid;
	private Context contexto;
	private Conf conf;

	public DayView(Context context) {
		super(context);
		this.contexto = context;
		inflate(context, R.layout.box_day, this);

		dia = (TextView) findViewById(R.id.dia);
		n_dia = (TextView) findViewById(R.id.ndia);

		conf = new Conf(getContext());
		id_user = conf.getIdUser();
		uuid = Utils.uuid(getContext());
	}

	public void setDay(final DiaMes day,int i) {
		dia.setText(day.getDayWeek());
		n_dia.setText(day.getDia());
		if (i > 0) {
		    n_dia.setTextColor(getResources().getColor(R.color.day_selected));
            dia.setTextColor(getResources().getColor(R.color.day_selected));			
		}
	}

}
