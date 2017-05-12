package com.imaginamos.usuariofinal.taxisya.activities;

import java.util.Calendar;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.comm.MiddleConnect;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;
import com.imaginamos.usuariofinal.taxisya.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.imaginamos.usuariofinal.taxisya.utils.MyDatePicker;

import cz.msebera.android.httpclient.Header;

public class AgendarActivity extends Activity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,  GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private ImageView iv, iv2, aeropuerto, fuera, horas, mensajeria;

    private Button btnAgendarServicio;
    private Button btnMisAgendamientos;
    private Button btnPickUpAddress;
    private Button btnDropOffAddress;
    private LinearLayout layoutDestination;

    private ImageView btn_clear1, btn_clear2, btn_clear3, btn_clear4, btn_clear5, bt_back;
    private TextView fecha, hora;
    //private EditText dir_uno, dir_dos, dir_tres, dir_barrio, dir_observaciones;
    private EditText dir_barrio, dir_observaciones;
    private EditText mAddress;
    private String id_user, uuid;

    private int mYear = 2000;
    private int mMonth = 1;
    private int mDay = 1;
    private int mHour = 12;
    private int mMinute = 00;
    private int motivo = 0; /*1: Aeropuerto, 2: Fuera de Bogota, 3: Mensajeria, 4: Horas */
    private Spinner sp;
    private String indice_direccion = "Calle";
    private ProgressDialog pDialog;

    private MyDatePicker datePickerDialog;
    private TimePickerDialog pick;
    private String destino = null;
    private RelativeLayout content;
    private ImageView conte_img;
    private Conf conf;
    private EditText content_text;
    private BroadcastReceiver mReceiver;
    private Calendar mCurrentDate;

    private double mLatitudeFrom = 0;
    private double mLongitudeFrom = 0;

    private double mLatitudeTo = 0;
    private double mLongitudeTo = 0;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            mHour = hourOfDay;

            mMinute = minute;

            if(validaDatosFecha()) {
                updateDisplay();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler hand = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(AgendarActivity.this);

            builder.setTitle(getString(R.string.important));

            builder.setMessage(getString(R.string.last_15_minutes));

            builder.setNeutralButton(R.string.text_ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(getApplicationContext(), R.string.servicio_agendado_correctamente, Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(AgendarActivity.this, MisAgendamientosActivity.class);

                    startActivity(i);
                }
            });

            builder.setCancelable(false);

            builder.create();

            builder.show();
        }

    };


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("SEGUIMIENTO", "onStart - MainActivity");
///		mLocationClient.connect();
///        if (mGoogleApiClient != null) {
///            mGoogleApiClient.connect();
///        }
        if (mLocationRequest == null) {
            mLocationRequest = createLocationRequest();
        }
    }


    @Override
    public void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.hold, R.anim.pull_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.activity_agendar2);

        conf = new Conf(this);

        id_user = conf.getIdUser();

        uuid = Utils.uuid(this);

//        content = (RelativeLayout) findViewById(R.id.conten_destin);
//        conte_img = (ImageView) findViewById(R.id.imageView19);
//        iv = (ImageView) findViewById(R.id.ver_agendamientos);
//        iv2 = (ImageView) findViewById(R.id.agenservicio);

        btnMisAgendamientos = (Button) findViewById(R.id.btnMisAgendamientos);
        btnAgendarServicio = (Button) findViewById(R.id.btnAgendarServicio);

        fuera = (ImageView) findViewById(R.id.fuera_bogota);
        horas = (ImageView) findViewById(R.id.servicio_horas);
//        btn_clear1 = (ImageView) findViewById(R.id.clear1);
//        btn_clear2 = (ImageView) findViewById(R.id.clear2);
//        btn_clear3 = (ImageView) findViewById(R.id.clear3);
//        btn_clear4 = (ImageView) findViewById(R.id.clear4);
//        btn_clear5 = (ImageView) findViewById(R.id.clear5);
//
//        btn_clear1.setVisibility(View.INVISIBLE);
//        btn_clear2.setVisibility(View.INVISIBLE);
//        btn_clear3.setVisibility(View.INVISIBLE);
//        btn_clear4.setVisibility(View.INVISIBLE);
//        btn_clear5.setVisibility(View.INVISIBLE);

        mensajeria = (ImageView) findViewById(R.id.servicio_mensajeria);
        aeropuerto = (ImageView) findViewById(R.id.aeropuerto_uno);
        bt_back = (ImageView) findViewById(R.id.bt_back);
        fecha = (TextView) findViewById(R.id.fechayhora);
        hora = (TextView) findViewById(R.id.hora);
        mAddress = (EditText) findViewById(R.id.text_address);

        btnPickUpAddress = (Button) findViewById(R.id.btn_pick_up_address);
        btnDropOffAddress = (Button) findViewById(R.id.btn_drop_off_address);
        layoutDestination = (LinearLayout) findViewById(R.id.layout_scheduled_destination);
//        dir_uno = (EditText) findViewById(R.id.direccion_uno);
//        dir_dos = (EditText) findViewById(R.id.direccion_dos);
//        dir_tres = (EditText) findViewById(R.id.direccion_tres);
        dir_barrio = (EditText) findViewById(R.id.barrio);
        dir_observaciones = (EditText) findViewById(R.id.observaciones);
        content_text = (EditText) findViewById(R.id.text_desti);

        btnPickUpAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPref.setRootActivity("HomeActivity");
                Intent intent = new Intent(AgendarActivity.this, MapaActivitys.class);
                intent.putExtra("FromActivity", "Schedule");
                intent.putExtra("PickUp", true);
                startActivityForResult(intent, 555);
            }
        });

        btnDropOffAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPref.setRootActivity("HomeActivity");
                Intent intent = new Intent(AgendarActivity.this, MapaActivitys.class);
                intent.putExtra("FromActivity", "Schedule");
                intent.putExtra("PickUp", false);
                startActivityForResult(intent, 556);
            }
        });

//        dir_uno.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true) {
//                    btn_clear2.setVisibility(View.VISIBLE);
//                } else {
//                    btn_clear2.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        dir_dos.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true) {
//                    btn_clear1.setVisibility(View.VISIBLE);
//                } else {
//                    btn_clear1.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        dir_tres.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true) {
//                    btn_clear3.setVisibility(View.VISIBLE);
//                } else {
//                    btn_clear3.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        dir_barrio.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true) {
//                    btn_clear5.setVisibility(View.VISIBLE);
//                } else {
//                    btn_clear5.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//        dir_observaciones.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus == true) {
//                    btn_clear4.setVisibility(View.VISIBLE);
//                } else {
//                    btn_clear4.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

        aeropuerto.setOnClickListener(this);
        fuera.setOnClickListener(this);
        horas.setOnClickListener(this);
        mensajeria.setOnClickListener(this);

//        btn_clear1.setOnClickListener(this);
//        btn_clear2.setOnClickListener(this);
//        btn_clear3.setOnClickListener(this);
//        btn_clear4.setOnClickListener(this);
//        btn_clear5.setOnClickListener(this);

//        iv.setOnClickListener(this);
//        iv2.setOnClickListener(this);

        btnMisAgendamientos.setOnClickListener(this);
        btnAgendarServicio.setOnClickListener(this);

        fecha.setOnClickListener(this);
        hora.setOnClickListener(this);

        bt_back.setOnClickListener(this);

//        sp = (Spinner) findViewById(R.id.spinner);
//
//        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.opciones, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        sp.setAdapter(adapter);
//
//        sp.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                indice_direccion = parentView.getItemAtPosition(position).toString();
//            }
//
//            public void onNothingSelected(AdapterView<?> parentView) {
//            }
//
//        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Log.v("USER_CLOSE_SESSION", "Sesión cerrada - confirmación");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(AgendarActivity.this, LoginActivity.class);
                    in3.putExtra("target", 1);
                    startActivity(in3);
                    finish();
                } else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE", "mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                }
            }
        };

        try {
            registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {

        }

        if (checkPlayServices()) {
            Log.e("checkPlayServices", "checkPlayServices");
            buildGoogleApiClient();
        }

        createPick();

        updateDisplay();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (provider != null){
//            locationManager.requestLocationUpdates(provider, 400, 1, this);
//        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        Log.v("onLocationChanged", "posicion " + String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()) );

    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                // Intent in3 = new Intent(MapaActivitys.this,HomeActivity.class);
                // startActivity(in3);

                finish();
            }
            return false;
        }
        return true;
    }

    void mostrarMensaje(final String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AgendarActivity.this);
        builder.setTitle(R.string.informacion_importante);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.text_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH", "mensaje: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    @Override
    protected void onDestroy() {
        Log.v("onDestroy", "AgendarActivity");
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.v("onConnected","lat = " + String.valueOf(mLastLocation.getLatitude()));
            Log.v("onConnected","lng = " + String.valueOf(mLastLocation.getLongitude()));
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

            mLatitudeFrom = mLastLocation.getLatitude();
            mLongitudeFrom = mLastLocation.getLatitude();

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("onConnectionFailed", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("onConnectionSuspend", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(data.getStringExtra("FromActivity") != null && data.getStringExtra("FromActivity").equals("MapaActivity")){
                try {
                    if (requestCode == 555) {
                        mLatitudeFrom = Double.valueOf(data.getExtras().getString("latitud"));
                        mLongitudeFrom = Double.valueOf(data.getExtras().getString("longitud"));
                        mAddress.setText(data.getStringExtra("AddressFromMapa"));
                        Log.i("ADDRESS RESULT FROM", "Lat: " + mLatitudeFrom + " Long: " + mLongitudeFrom);
                    } else if (requestCode == 556) {
                        mLatitudeTo = Double.valueOf(data.getExtras().getString("latitud"));
                        mLongitudeTo = Double.valueOf(data.getExtras().getString("longitud"));
                        content_text.setText(data.getStringExtra("AddressFromMapa"));
                        Log.i("ADDRESS RESULT TO", "Lat: " + mLatitudeTo + " Long: " + mLongitudeTo);
                    }
                }catch (Exception ex){
                    Log.i("ADDRESS EXCEPTION", ex.getMessage());
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

//            case R.id.ver_agendamientos:
            case R.id.btnMisAgendamientos:
                Intent i = new Intent(AgendarActivity.this, MisAgendamientosActivity.class);
                startActivity(i);
                break;

//            case R.id.agenservicio:
            case R.id.btnAgendarServicio:
                if (validaDatos()) {

                    if (verificarDatos()) {
                        scheduleService(view);
                    } else {
                        Intent in = new Intent(AgendarActivity.this, RegistroActivity.class);
                        in.putExtra("target", "activity_agendar");
                        startActivity(in);
                    }
                }
                break;

            case R.id.fechayhora:
                mCurrentDate = Calendar.getInstance();
                //myCalendar.add(Calendar.DAY_OF_MONTH, 1);

                Calendar nextDay = Calendar.getInstance();
                nextDay.add(Calendar.DAY_OF_MONTH, 1);

                int intCurrentYear = mCurrentDate.get(Calendar.YEAR);
                int intCurrentMonth = mCurrentDate.get(Calendar.MONTH);
                int intCurrentDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                Log.i("SCHEDULE DATE","Date :"+intCurrentDay);
                datePickerDialog = new MyDatePicker(this, mDateSetListener,
                        intCurrentYear, intCurrentMonth, intCurrentDay,
                        nextDay.get(Calendar.YEAR), nextDay.get(Calendar.MONTH), nextDay.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();

                break;

            case R.id.hora:
                showMyDialog();
                break;

            case R.id.aeropuerto_uno:
                // conte_img.setVisibility(View.GONE);
                // content.setVisibility(View.GONE);
                aeropuerto.setImageResource(R.drawable.aero_over);
                fuera.setImageResource(R.drawable.fuerab_normal);
                horas.setImageResource(R.drawable.horas_normal);
                mensajeria.setImageResource(R.drawable.mensajeria_normal);
                motivo = 1;
                break;

            case R.id.fuera_bogota:
                // conte_img.setVisibility(View.VISIBLE);
                // content.setVisibility(View.VISIBLE);
                //content_text.setHint(R.string.hint_ejemplo_fuera_bogota);
                fuera.setImageResource(R.drawable.fuerab_over);
                aeropuerto.setImageResource(R.drawable.aero_normal);
                horas.setImageResource(R.drawable.horas_normal);
                mensajeria.setImageResource(R.drawable.mensajeria_normal);
                motivo = 2;
                break;

            case R.id.servicio_horas:
                // conte_img.setVisibility(View.GONE);
                // content.setVisibility(View.GONE);
                horas.setImageResource(R.drawable.horas_over);
                aeropuerto.setImageResource(R.drawable.aero_normal);
                fuera.setImageResource(R.drawable.fuerab_normal);
                mensajeria.setImageResource(R.drawable.mensajeria_normal);
                motivo = 4;
                break;

            case R.id.servicio_mensajeria:
                //content_text.setHint(R.string.hint_ejemplo_direccion);
                // conte_img.setVisibility(View.VISIBLE);
                // content.setVisibility(View.VISIBLE);
                mensajeria.setImageResource(R.drawable.mensajeria_over);
                fuera.setImageResource(R.drawable.fuerab_normal);
                aeropuerto.setImageResource(R.drawable.aero_normal);
                horas.setImageResource(R.drawable.horas_normal);
                motivo = 3;
                break;

//            case R.id.clear1:
//                dir_dos.setText("");
//                break;
//
//            case R.id.clear2:
//                dir_uno.setText("");
//                break;
//
//            case R.id.clear3:
//                dir_tres.setText("");
//                break;
//
//            case R.id.clear4:
//                dir_observaciones.setText("");
//                break;
//
//            case R.id.clear5:
//                dir_barrio.setText("");
//                break;

            case R.id.bt_back:
                finish();
                break;
        }
        layoutDestination.setVisibility((motivo == 3 || motivo == 2) ? View.VISIBLE : View.INVISIBLE);
    }

    private void createPick() {
        final Calendar c = Calendar.getInstance();

        c.add(Calendar.DAY_OF_MONTH, 1);

        if (datePickerDialog != null) {
            try {

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;

                if (currentapiVersion >= 11) {
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                } else {
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                }

            } catch (Exception e) {
                mHour = c.get(Calendar.HOUR_OF_DAY);
            }

        } else {
            mHour = c.get(Calendar.HOUR_OF_DAY);
        }

        pick = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);

    }

    private void showMyDialog() {


        pick.show();
    }

    public boolean validaDatos() {

//        String comp1 = dir_uno.getText().toString();
//        String comp2 = dir_dos.getText().toString();
//        String no = dir_tres.getText().toString();
        String address = mAddress.getText().toString();
        String barrio = dir_barrio.getText().toString();
        destino = content_text.getText().toString();

//        comp1 = comp1.replaceAll("[^0-9 ]", "") + " " + comp1.replaceAll("[^A-Z a-z]", "");
//        comp2 = comp2.replaceAll("[^0-9 ]", "") + " " + comp2.replaceAll("[^A-Z a-z]", "");

        if (motivo == 0) {
            Toast.makeText(getApplicationContext(), R.string.aviso_verifica_datos_del_agendamiento, Toast.LENGTH_LONG).show();
            return false;
        }
        //if (indice_direccion.equals(null) || comp1.equals("") || comp2.equals("") || no.equals("") || barrio.equals("")) {
        if (indice_direccion.equals(null) || address.equals("")){// || barrio.equals("")) {

            Toast.makeText(getApplicationContext(), R.string.aviso_verifica_datos_agendamiento, Toast.LENGTH_LONG).show();
            return false;
        }

        if (motivo == 2 || motivo == 3) {
            if (destino == null || destino.equals("")) {
                Toast.makeText(getApplicationContext(), R.string.aviso_verifica_datos_del_agendamiento, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return validaDatosFecha();
        // si tipo agendamiento -> fuera de bogota validar
        // content_text
    }

    private boolean validaDatosFecha(){
        final Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR,mYear);
        c.set(Calendar.MONTH,mMonth);
        c.set(Calendar.DAY_OF_MONTH,mDay);
        c.set(Calendar.HOUR_OF_DAY,mHour);
        c.set(Calendar.MINUTE,mMinute);

        Calendar limitDate = Calendar.getInstance();
        limitDate.add(Calendar.DAY_OF_MONTH,1);


        //return c.after(limitDate);
        if(c.after(limitDate)){
            Toast.makeText(getApplicationContext(), R.string.aviso_no_puedes_agendar_antes_24_horas, Toast.LENGTH_LONG).show();
            return false;
        } else {
            limitDate.add(Calendar.DAY_OF_MONTH,-1);
            if(c.before(limitDate)){
                Toast.makeText(getApplicationContext(), "La hora de agendamiento indicada ya ha pasado", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }

    public boolean verificarDatos() {

        try {

            if (motivo == 2 || motivo == 3) {
                if (content_text.getText().toString().equals("") || content_text.getText() == null) {
                    return false;
                }
            }


            return conf.getLogin();

        } catch (Exception e) {
            return false;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            updateDisplay();
        }
    };

    private void updateDisplay() {
        fecha.setText(new StringBuilder().append(mMonth + 1).append("-")
                .append(mDay).append("-").append(mYear));
        hora.setText(new StringBuilder().append(pad(mHour)).append(":")
                .append(pad(mMinute)));

    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void err_schedule() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(), getString(R.string.error_net), Toast.LENGTH_SHORT).show();
    }

    public void scheduleService(View view) {

        String mes = String.valueOf(mMonth + 1);
        String dia = String.valueOf(mDay);
        String hora = String.valueOf(mHour);
        String minuto = String.valueOf(mMinute);

        if (mes.length() == 1) {
            mes = "0" + mes;
        }

        if (dia.length() == 1) {
            dia = "0" + dia;
        }

        if (hora.length() == 1) {
            hora = "0" + hora;
        }

        if (minuto.length() == 1) {
            minuto = "0" + minuto;
        }

        String fecha_hora = String.valueOf(mYear) + "-" + mes + "-" + dia + " " + hora + ":" + minuto + ":00";

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.v("onConnected","lat2 = " + String.valueOf(mLastLocation.getLatitude()));
            Log.v("onConnected","lng2 = " + String.valueOf(mLastLocation.getLongitude()));
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            mLatitudeFrom = mLastLocation.getLatitude();
            mLongitudeFrom = mLastLocation.getLongitude();
        }

        String lat = String.valueOf(mLatitudeFrom);
        String lng = String.valueOf(mLongitudeFrom);

        MiddleConnect.agend(this, id_user, uuid, fecha_hora, motivo,
//                indice_direccion,
//                dir_uno.getText().toString(),
//                dir_dos.getText().toString(),
//                dir_tres.getText().toString(),
                mAddress.getText().toString(),
                dir_barrio.getText().toString(),
                dir_observaciones.getText().toString(),
                content_text.getText().toString(),
                lat, lng,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        pDialog = new ProgressDialog(AgendarActivity.this);
                        pDialog.setMessage(getString(R.string.title_agendando_servicio));
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        Log.e("ERROR", response + "");
                        try {
                            JSONObject responsejson = new JSONObject(response);

                            if (responsejson != null && responsejson.length() > 0) {
                                if (responsejson.getInt("error") == 0) {

                                    hand.sendEmptyMessage(0);

                                } else {
                                    err_schedule();
                                }
                            } else {
                                err_schedule();
                            }

                        } catch (Exception e) {
                            err_schedule();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        err_schedule();
                    }

                    @Override
                    public void onFinish() {
                        try {
                            pDialog.dismiss();
                        } catch (Exception e) {
                        }

                    }
                });

    }


//    @Override
//    public void onLocationChanged(Location location) {
//        currentlat = location.getLatitude();
//        currentlong = location.getLongitude();
//    }

}
