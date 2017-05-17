package com.imaginamos.usuariofinal.taxisya.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginamos.taxisya.activities.MapaActivitys;
import com.imaginamos.usuariofinal.taxisya.R;
import com.imaginamos.usuariofinal.taxisya.adapter.AddressAdapter;
import com.imaginamos.usuariofinal.taxisya.adapter.RecyclerItemClickListener;
import com.imaginamos.usuariofinal.taxisya.adapter.SimpleItemTouchHelperCallback;
import com.imaginamos.usuariofinal.taxisya.io.AddressServiceResponse;
import com.imaginamos.usuariofinal.taxisya.io.ApiAdapter;
import com.imaginamos.usuariofinal.taxisya.io.ServiceResponse;
import com.imaginamos.usuariofinal.taxisya.models.Conf;
import com.imaginamos.usuariofinal.taxisya.models.Direccion;
import com.imaginamos.usuariofinal.taxisya.utils.Actions;
import com.imaginamos.usuariofinal.taxisya.utils.Utils;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyAddressesActivity extends Activity implements OnClickListener, Callback<AddressServiceResponse>, AddressAdapter.OnItemClickListener {

    private RecyclerView addressList;
    private AddressAdapter addressAdapter;
    private String id_user, uuid;
    private Conf conf;

    private BroadcastReceiver mReceiver;

    private ImageView mBack;
    private Button mAddAddress;
    private LinearLayout mNewAddress;
    private EditText edt_new_name;
    private EditText edt_new_direccion;
    private EditText edt_new_comentario;
    private Button btnGuardar;
    private boolean keyboardShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        conf = new Conf(this);
        id_user = conf.getIdUser();
        uuid = Utils.uuid(this);

        mNewAddress = (LinearLayout) findViewById(R.id.add_new);

        edt_new_name = (EditText) findViewById(R.id.new_nombre);
        edt_new_direccion = (EditText) findViewById(R.id.new_direccion);
        edt_new_comentario = (EditText) findViewById(R.id.new_comentario);

        btnGuardar = (Button) findViewById(R.id.btn_nueva_direccion);


        mBack = (ImageView) findViewById(R.id.btn_volver);
        mAddAddress = (Button) findViewById(R.id.btn_add);

        mBack.setOnClickListener(this);
        mAddAddress.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);

        edt_new_name.setImeActionLabel(getString(R.string.text_agregar), EditorInfo.IME_ACTION_DONE);
        edt_new_direccion.setImeActionLabel(getString(R.string.text_agregar), EditorInfo.IME_ACTION_DONE);
        edt_new_comentario.setImeActionLabel(getString(R.string.text_agregar), EditorInfo.IME_ACTION_DONE);


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






        addressList = (RecyclerView) findViewById(R.id.addresses_list);
        addressAdapter = new AddressAdapter(this);
        addressList.setLayoutManager(new GridLayoutManager(this, 1));
        addressList.setAdapter(addressAdapter);

        addressList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Log.v("ADDRESS1", "item selccionado");
                        Direccion dir = addressAdapter.getItem(position);
                        Log.v("ADDRESS1", "   id = " + String.valueOf(dir.getId()));


                        Intent returnIntent = new Intent(MyAddressesActivity.this, MapaActivitys.class);

                        returnIntent.putExtra("index", dir.getIndex());
                        returnIntent.putExtra("comp1", dir.getComp1());
                        returnIntent.putExtra("comp2", dir.getComp2());
                        returnIntent.putExtra("no", dir.getNumero());
                        returnIntent.putExtra("barrio", dir.getBarrio());
                        returnIntent.putExtra("obs", dir.getObservaciones());
                        returnIntent.putExtra("address", dir.getAddress());
                        returnIntent.putExtra("lat", dir.getLat());
                        returnIntent.putExtra("lng", dir.getLng());

                        setResult(RESULT_OK, returnIntent);

                        finish();
                    }
                })
        );

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(addressAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(addressList);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Actions.ACTION_USER_CLOSE_SESSION);
        intentFilter.addAction(Actions.ACTION_MESSAGE_MASSIVE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.ACTION_USER_CLOSE_SESSION)) {
                    Log.v("USER_CLOSE_SESSION","Sesión cerrada - confirmación");
                    Conf conf = new Conf(getApplicationContext());
                    conf.setLogin(false);

                    Intent in3 = new Intent(MyAddressesActivity.this,LoginActivity.class);
                    in3.putExtra("target", 1);
                    startActivity(in3);
                    finish();
                }
                else if (intent.getAction().equals(Actions.ACTION_MESSAGE_MASSIVE)) {

                    Log.v("MESSAGE_MASSIVE","mensaje global recibido");
                    String message = intent.getExtras().getString("message");
                    mostrarMensaje(message);

                }
            }
        };

        try {
            registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {

        }

    }


     private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
    /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d("TECLADO", "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        if (isKeyboardShown) {
            // hide
//            btnGuardar.setVisibility(View.GONE);
//            mScrollView.scrollTo(108, mScrollView.getBottom());
            keyboardShown = true;
        }
        else {
            // show
//            btnGuardar.setVisibility(View.VISIBLE);
//            mScrollView.scrollTo(20, mScrollView.getBottom());
            keyboardShown = false;
        }
        return isKeyboardShown;
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestAddresses();
    }

    @Override
    public void onItemClick(AddressAdapter.AddressHolder item, int position) {
        Toast.makeText(this,
                "Remove " + position + " : " + item.getItemName(),
                Toast.LENGTH_SHORT).show();
        //addressAdapter.onItemDismiss(position);
    }


    @Override
    protected void onDestroy() {
        Log.v("onDestroy", "MyAddressActivity");
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_volver:
                Log.v("SEGUIMIENTO","button btn_volver");
                finish();
                break;

            case R.id.btn_add:
                Log.v("SEGUIMIENTO","button add_dir");

                //mNewAddress.setVisibility(View.VISIBLE);
                //edt_new_name.setText("");
                //edt_new_comentario.setText("");
                //edt_new_direccion.setText("");
                Intent in2 = new Intent(MyAddressesActivity.this, MapNewAddressActivity.class);

                startActivityForResult(in2, 1);

                break;

            case R.id.btn_nueva_direccion:
                mNewAddress.setVisibility(View.GONE);

                saveNewAddress();

                break;

        }

    }

    void mostrarMensaje(final String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.informacion_importante);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.text_aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.e("PUSH","mensaje: " + message);
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    public void err_address() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.error_load_address), Toast.LENGTH_SHORT)
                .show();
    }


    public void saveNewAddress() {
        String address = edt_new_direccion.getText().toString();
        String comment = edt_new_comentario.getText().toString();
        String name = edt_new_name.getText().toString();
        id_user = conf.getIdUser();

        ApiAdapter.getApiService().addAddress(address, "", comment, id_user, "1", null, null, name, new Callback<ServiceResponse>() {
            @Override
            public void success(ServiceResponse data, Response response) {
                if (data.getError() == 1) {
                    Log.v("ADD_ADDRESS1", "ApiService 1");
                    //dirs.addAll(data.getAddress());
                    //adaptador.notifyDataSetChanged();

                    requestAddresses();


                } else {
                    Log.v("ADD_ADDRESS1", "ApiService 0");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("ADD_ADDRESS1", "onFailure " + String.valueOf(new Date()));
                //err_address();
            }
        });

    }


    private void requestAddresses() {
        ApiAdapter.getApiService().getAddress(id_user, uuid, this);
    }

    @Override
    public void success(AddressServiceResponse addressResponse, Response response) {
        Log.v("ADDRESS1", "ApiService " + addressResponse.toString());

        if (addressResponse.getError() == 0) {
            Log.v("ADDRESS1", "ApiService");
            //dirs.addAll(data.getAddress());
            Log.v("ADDRESS1"," " + String.valueOf(addressResponse.getAddress().size()));
            addressAdapter.removeAll();
            addressAdapter.addAll(addressResponse.getAddress());

            addressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Log.v("ADDRESS1", "onFailure " + String.valueOf(new Date()));
        if (error.getKind() == RetrofitError.Kind.NETWORK) {
            Toast.makeText(this,"Error de red", Toast.LENGTH_LONG).show();
        }
    }


}
