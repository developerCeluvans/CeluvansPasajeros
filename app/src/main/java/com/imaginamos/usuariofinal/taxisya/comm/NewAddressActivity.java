package com.imaginamos.usuariofinal.taxisya.comm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.imaginamos.usuariofinal.taxisya.io.ApiAdapter;
import com.imaginamos.usuariofinal.taxisya.io.ServiceResponse;
import com.imaginamos.usuariofinal.taxisya.R;
import com.imaginamos.usuariofinal.taxisya.models.Conf;

import java.io.FileInputStream;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewAddressActivity extends Activity implements OnClickListener {
    private EditText edt_new_name;
    private EditText edt_new_direccion;
    private EditText edt_new_comentario;
    private Button btnGuardar;
    private Button btnClose;
    private ImageView image_map;
    private String id_user;
    private String direccion;
    private String barrio;
    private String lat;
    private String lng;
    private Conf conf;
    private boolean keyboardShown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.push_out_to_bottom);

        setContentView(R.layout.activity_new_address);

        conf = new Conf(this);

        image_map = (ImageView) findViewById(R.id.image_map);
        edt_new_name = (EditText) findViewById(R.id.new_nombre);
        edt_new_direccion = (EditText) findViewById(R.id.new_direccion);
        edt_new_comentario = (EditText) findViewById(R.id.new_comentario);

        btnGuardar = (Button) findViewById(R.id.btn_nueva_direccion);
        btnClose = (Button) findViewById(R.id.btn_close);

        btnGuardar.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        Intent intent = getIntent();
        Log.v("SCREENSHOT","test");

        direccion  = intent.getExtras().getString("direccion");
        barrio  = intent.getExtras().getString("barrio");
        lat = intent.getExtras().getString("lat");
        lng = intent.getExtras().getString("lng");

        //Bitmap bitmap = (Bitmap) intent.getParcelableExtra("bitmap");

        Bitmap bitmap = getImageBitmap(getApplicationContext(),"foto","png");
        image_map.setImageBitmap(bitmap);

        edt_new_direccion.setText(direccion);
        edt_new_direccion.requestFocus();

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
                    Log.v("SEGUIMIENTO","oculta teclado");
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
                    Log.v("SEGUIMIENTO","oculta teclado");
                    imm.hideSoftInputFromWindow(edt_new_comentario.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.v("onKeyUp","++++++ keyCode " + String.valueOf(keyCode));
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("onKey", "onKeyDown --- keyCode " + String.valueOf(keyCode));

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.v("onKey", "onKeyDown --- KeyEvent.KEYCODE_BACK");
            if (keyboardShown) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.v("SEGUIMIENTO","oculta teclado");
                imm.hideSoftInputFromWindow(edt_new_name.getWindowToken(), 0);
            }
            else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.push_out_to_bottom,R.anim.pull_up_from_bottom );

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        overridePendingTransition(R.anim.push_out_to_bottom,R.anim.pull_up_from_bottom );
        super.onDestroy();
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
            btnGuardar.setVisibility(View.GONE);
            keyboardShown = true;
        }
        else {
            // show
            btnGuardar.setVisibility(View.VISIBLE);
            keyboardShown = false;
        }
        return isKeyboardShown;
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
        ApiAdapter.getApiService().addAddress(address, barrio, comment, id_user, "1", lat, lng, name, new Callback<ServiceResponse>() {
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

    public Bitmap getImageBitmap(Context context,String name,String extension){
        name=name+"."+extension;
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
    }
}
