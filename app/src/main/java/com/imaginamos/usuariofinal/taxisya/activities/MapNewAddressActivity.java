package com.imaginamos.usuariofinal.taxisya.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.imaginamos.usuariofinal.taxisya.R;
import com.imaginamos.usuariofinal.taxisya.io.ApiAdapter;
import com.imaginamos.usuariofinal.taxisya.io.ServiceResponse;
import com.imaginamos.usuariofinal.taxisya.models.Conf;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapNewAddressActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener , OnMapReadyCallback {
    private GoogleMap mMap;
    private boolean firstTime = true;
    private float lat;
    private float lng;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private EditText edt_new_name;
    private EditText edt_new_direccion;
    private EditText edt_new_comentario;
    private Button btnGuardar;
    private ImageView mBack;

    private String id_user;
    private String direccion;
    private String barrio;
    private String strLat;
    private String strLng;
    private Conf conf;

    private boolean keyboardShown = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_new_address);
        setUpMapIfNeeded();

        conf = new Conf(this);

        edt_new_name = (EditText) findViewById(R.id.new_nombre);
        edt_new_direccion = (EditText) findViewById(R.id.new_direccion);
        edt_new_comentario = (EditText) findViewById(R.id.new_comentario);

        btnGuardar = (Button) findViewById(R.id.btn_nueva_direccion);


        mBack = (ImageView) findViewById(R.id.btn_volver);

        mBack.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);


        edt_new_name.setImeActionLabel("Agregar", EditorInfo.IME_ACTION_DONE);
        edt_new_direccion.setImeActionLabel("Agregar", EditorInfo.IME_ACTION_DONE);
        edt_new_comentario.setImeActionLabel("Agregar", EditorInfo.IME_ACTION_DONE);


        edt_new_direccion.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isKeyboardShown(edt_new_direccion.getRootView());
            }
        });

        edt_new_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isKeyboardShown(edt_new_name.getRootView());
            }
        });
        edt_new_comentario.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isKeyboardShown(edt_new_comentario.getRootView());
            }
        });



        edt_new_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int actionId,
                                          KeyEvent arg2) {
                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || (arg2.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    Log.v("SEGUIMIENTO", "oculta teclado");
                    imm.hideSoftInputFromWindow(edt_new_name.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        edt_new_direccion.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int actionId,
                                          KeyEvent arg2) {
                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || (arg2.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    Log.v("SEGUIMIENTO", "oculta teclado");
                    imm.hideSoftInputFromWindow(edt_new_direccion.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        edt_new_comentario.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int actionId,
                                          KeyEvent arg2) {
                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || (arg2.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    Log.v("SEGUIMIENTO", "oculta teclado");
                    imm.hideSoftInputFromWindow(edt_new_comentario.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

    }

    private boolean isKeyboardShown(View rootView) {
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d("TECLADO", "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        if (isKeyboardShown) {
            // hide
            keyboardShown = true;
        }
        else {
            // show
            keyboardShown = false;
        }
        return isKeyboardShown;
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (locationRequest == null) {
            locationRequest = createLocationRequest();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_nueva_direccion:
                saveNewAddress();

                break;

            case R.id.btn_close:
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.v("SEGUIMIENTO","oculta teclado");
                imm.hideSoftInputFromWindow(edt_new_name.getWindowToken(), 0);
                finish();
                break;

            default:
                break;
        }

    }

    void saveNewAddress() {
        //url_cancel_current =  Url;

        //public static void addAddress(Context context, String index_id, String comp1, String comp2, String no, String barrio, String obs, String user_id,
        //String user_pref_order, AsyncHttpResponseHandler responseHandler)
        String address = edt_new_direccion.getText().toString();
        String comment = edt_new_comentario.getText().toString();
        String name = edt_new_name.getText().toString();

        id_user = conf.getIdUser();

        Log.v("ADD_ADDRESS1", "address=" + address + " comment=" + comment + " barrio=" + barrio + " id_user=" + id_user + " name=" + name);
        ApiAdapter.getApiService().addAddress(address, barrio, comment, id_user, "1", strLat, strLng, name, new Callback<ServiceResponse>() {
            @Override
            public void success(ServiceResponse data, Response response) {
                if (data.getError() == 1) {
                    Log.v("ADD_ADDRESS1", "ApiService 1");
                    //dirs.addAll(data.getAddress());
                    //adaptador.notifyDataSetChanged();
                }
                else {
                    Log.v("ADD_ADDRESS1", "ApiService 0");
                }
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.v("SEGUIMIENTO","oculta teclado");
                imm.hideSoftInputFromWindow(edt_new_name.getWindowToken(), 0);

                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ADD_ADDRESS1", "onFailure " + String.valueOf(new Date()));
                //err_address();
            }
        });

    }


    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }

        buildGoogleApiClient();


//        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
//        if (mMap == null) {
//            return;
//        }
        // Initialize map options. For example:
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            /*
             * (non-Javadoc)
             * @see com.google.android.gms.maps.GoogleMap.OnCameraChangeListener#onCameraChange(com.google.android.gms.maps.model.CameraPosition)
             */
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                Location l = new Location("");
                l.setLatitude(cameraPosition.target.latitude);
                l.setLongitude(cameraPosition.target.longitude);
                searchByLocation(l);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(firstTime)
        {
            lat = (float) location.getLatitude();
            lng = (float) location.getLongitude();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
            firstTime = false;
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (!client.isConnected()) {
            client.connect();
        }
    }

    public LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                client, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        if (client.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    client, this);
        }
    }


    //Location Related Methods
    private void searchByLocation(final Location location) {
        AsyncTask<Void, Void, List<Address>> task = new AsyncTask<Void, Void,
                List<android.location.Address>>() {

            @Override
            protected List<android.location.Address> doInBackground(Void... params) {
                Geocoder geocoder = new Geocoder(MapNewAddressActivity.this);
                List<android.location.Address> list = null;
                try {
                    double latitude = location.getLatitude(), longitude = location.getLongitude();
                    strLat = String.valueOf(latitude);
                    strLng = String.valueOf(longitude);

                    list = geocoder.getFromLocation(latitude, longitude, 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return list;
            }

            protected void onPostExecute(List<android.location.Address> result) {
                if (result != null) {
                    if (result.size() > 0) {
                        android.location.Address address = result.get(0);
                        android.location.Address addressBarrio = result.get(1);

                        String userAddress = address.getAddressLine(0);
                        String userBarrio = null;

                        if (addressBarrio != null && addressBarrio.getAddressLine(0) != null && !addressBarrio.getAddressLine(0).equals("")) {
                            userBarrio = addressBarrio.getAddressLine(0).split(",")[0];
                        }

                        int index = userAddress.indexOf("-");
                        if (index != -1) {
                            userAddress = userAddress.substring(0, index + 1);
                        }


                        Log.d("Address1", userAddress);
                        Log.d("Address1", userBarrio);
                        Log.d("Address1","lat " + strLat);
                        Log.d("Address1","lng " + strLng);

                        edt_new_direccion.setText(userAddress);
                        barrio = userBarrio;


//                        etAddress.setText(userAddress);
//                        etAddress.setSelection(userAddress.length());
//                        etAddress.setEnabled(true);
//                        btnOk.setEnabled(true);

                        if (client.isConnected()) {
                            stopLocationUpdates();
                            client.disconnect();
                        }
                    }
                }

            }
        };

        task.execute();
    }
}
