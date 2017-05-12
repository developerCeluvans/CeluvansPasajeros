package com.imaginamos.usuariofinal.taxisya.io;

import retrofit.RestAdapter;

/**
 * Created by leo on 8/31/15.
 */
public class ApiAdapter {
    private static ApiService API_SERVICE;

    public static ApiService getApiService () {

        if(API_SERVICE == null){
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(ApiConstants.BASE_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            API_SERVICE = adapter.create(ApiService.class);
        }
        return API_SERVICE;

    }


    //public static Observable<HypedArtistResponse> getHypedArtist(){
    //    return getApiService().getHypedArtists(obtainApiKey());
    //}

}
