package com.imaginamos.usuariofinal.taxisya.io;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leo on 9/1/15.
 */
public class RequestServiceResponse {
    @SerializedName("service_id")
    private String service_id;
    @SerializedName("success")
    private boolean success;
    @SerializedName("error")
    private int error;

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
