package com.imaginamos.usuariofinal.taxisya.io;

import com.google.gson.annotations.SerializedName;
import com.imaginamos.usuariofinal.taxisya.models.Direccion;

import java.util.ArrayList;

/**
 * Created by leo on 9/1/15.
 */
public class AddressServiceResponse {
    @SerializedName("error")
    private int error;
    @SerializedName("address")
    private ArrayList<Direccion> address;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public ArrayList<Direccion> getAddress() {
        return address;
    }

    public void setAddress(ArrayList<Direccion> address) {
        this.address = address;
    }
}
