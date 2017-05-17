package com.imaginamos.usuariofinal.taxisya.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginamos.usuariofinal.taxisya.R;
import com.imaginamos.usuariofinal.taxisya.io.ApiAdapter;
import com.imaginamos.usuariofinal.taxisya.io.ServiceResponse;
import com.imaginamos.usuariofinal.taxisya.models.Direccion;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by leo on 9/8/15.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressHolder> implements ItemTouchHelperAdapter {

    ArrayList<Direccion> addresses;
    Context context;
    private OnItemClickListener onItemClickListener;

    public AddressAdapter(Context context) {
        this.context = context;
        this.addresses = new ArrayList<>();
    }

    @Override
    public AddressHolder onCreateViewHolder(ViewGroup container, int position) {
        View addressView = LayoutInflater.from(context)
                .inflate(R.layout.item_address, container, false);

        return new AddressHolder(addressView);
    }


    @Override
    public void onBindViewHolder(AddressHolder addressHolder, int position) {
        Direccion currentAddress = addresses.get(position);

        String full_dir = null;
        if (currentAddress.getAddress()!= null) {
            full_dir = currentAddress.getAddress() + " " + currentAddress.getBarrio();
        }
        else {
            full_dir = currentAddress.getIndex() + " " + currentAddress.getComp1() + " " + currentAddress.getComp2() + " - " + currentAddress.getNumero() + " " + currentAddress.getBarrio();
        }

        addressHolder.setTitle(currentAddress.getName());
        addressHolder.setName(full_dir);

    }

    @Override
    public void onItemDismiss(int position) {
        Log.v("DEL_ADDRESS", "Adapter onItemDismiss");
        String address_id = addresses.get(position).getId();
        Log.v("DEL_ADDRESS", "Adapter onItemDismiss address_id " + address_id);

        addresses.remove(position);
        notifyItemRemoved(position);
        // elimina reg
        ApiAdapter.getApiService().delAddress(address_id, new Callback<ServiceResponse>() {
            @Override
            public void success(ServiceResponse data, Response response) {
                if (data.getError() == 1) {
                    Log.v("DEL_ADDRESS", "Ok");
                } else
                    Log.v("DEL_ADDRESS", "Ok");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("DEL_ADDRESS", "fail");
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Log.v("ADDRESS1", "Adapter onItemMove");
        Collections.swap(addresses, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(AddressHolder item, int position);
    }

    public void addAll(ArrayList<Direccion> addresses) {
        if (addresses == null)
            throw new NullPointerException("The items cannot be null");

        this.addresses.addAll(addresses);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.addresses.clear();
    }

    public void addItem(Direccion address) {
        addresses.add(address);
        notifyItemInserted(getItemCount() - 1);
    }

    public Direccion getItem(int adapterPosition){
        return addresses.get(adapterPosition);
    }

    public class AddressHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        private ImageView image;
        private TextView title;
        private TextView name;

        public AddressHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.address_icon);
            title = (TextView) itemView.findViewById(R.id.address_title);
            name = (TextView) itemView.findViewById(R.id.address_name);
        }

        public void setTitle(String name) {
            this.title.setText(name);
        }

        public void setName(String description) {
            this.name.setText(description);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        public CharSequence getItemName(){
            return name.getText();
        }


    }


}
