package com.imaginamos.usuariofinal.taxisya.io;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leo on 9/8/15.
 */
public class UserResponse {
    @SerializedName("Response")
    private String response;
    @SerializedName("error")
    private int error;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
