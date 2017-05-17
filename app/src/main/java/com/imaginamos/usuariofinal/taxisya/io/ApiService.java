package com.imaginamos.usuariofinal.taxisya.io;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by leo on 8/31/15.
 */
public interface ApiService {

    // login
//    params.put("type", HomeActivity.TYPE_USER);
    @FormUrlEncoded
    @POST(ApiConstants.USER_LOGIN)
    void login(@Field("type") String type, @Field("login") String login,
               @Field("pwd") String pwd, @Field("uuid") String uuid,
               Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.USER_REGISTER)
    void register(@Field("type") String type, @Field("name") String name,
                  @Field("lastname") String lastname, @Field("email") String email,
                  @Field("login") String login, @Field("pwd") String pwd,
                  @Field("token") String token, @Field("cellphone") String cellphone,
                  @Field("uuid") String uuid, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.USER_UPDATE)
    void update(@Field("type") String type, @Field("name") String name,
                @Field("lastname") String lastname, @Field("email") String email,
                @Field("login") String login, @Field("pwd") String pwd,
                @Field("token") String token, @Field("cellphone") String cellphone,
                @Field("uuid") String uuid, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.USER_IS_LOGUED)
    void isLogued(@Field("login") String login, @Field("uuid") String uuid, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.USER_LOGOUT)
    void logout(@Field("login") String login, @Field("pwd") String pwd, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.GET_SERVICES)
    void getServicesMonth(@Field("user_id") String user_id, @Field("month") String month,
                          @Field("uuid") String uuid, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.GET_SERVICES)
    void getServicesDayOfMonth(@Field("user_id") String user_id, @Field("month") String month,
                               @Field("day") String day, @Field("uuid") String uuid, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.GET_ADDRESS)
    void getAddress(@Field("user_id") String user_id, @Field("uuid") String uuid, Callback<AddressServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.DEL_ADDRESS)
    void delAddress(@Field("address_id") String address_id, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.ADD_ADDRESS)
    void addAddress(
            @Field("address") String address,
            @Field("barrio") String barrio,
            @Field("obs") String obs,
            @Field("user_id") String user,
            @Field("user_pref_order") String order,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("nombre") String nombre, Callback<ServiceResponse> callback);

    // solicitar servicio
    @FormUrlEncoded
    @POST(ApiConstants.REQUEST_SERVICE)
    void requestService(
            @Path("user_id") String user_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("index_id") String index_id,
            @Field("comp1") String comp1,
            @Field("comp2") String comp2,
            @Field("no") String no,
            @Field("barrio") String barrio,
            @Field("obs") String obs,
            @Field("uuid") String uuid, Callback<RequestServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.REQUEST_SERVICE_ADDRESS)
    void requestServiceAddress(
            @Path("user_id") String user_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("barrio") String barrio,
            @Field("address") String address,
            @Field("uuid") String uuid, Callback<RequestServiceResponse> callback);


    // cancelar servicio
    @POST(ApiConstants.CANCEL_SERVICE)
    void cancelService(@Path("user_id") String user_id, Callback<RequestServiceResponse> callback);

    // cancelar servicio
    @POST(ApiConstants.CANCEL_SERVICE_SYSTEM)
    void cancelServiceBySystem(@Path("user_id") String user_id, @Field("by_system") String value, Callback<RequestServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.SERVICE_SCORE)
    void scoreService(@Field("user_id") String user_id,
                      @Field("service_id") String service_id,
                      @Field("qualification") String qualification,
                      Callback<ServiceResponse> callback);

    // agendamiento
    @FormUrlEncoded
    @POST(ApiConstants.SCHEDULE_FINISH)
    void finishSchedule(@Field("user_id") String user_id,
                        @Field("schedule_id") String schedule_id,
                        @Field("qualification") String qualification,
                        @Field("uuid") String uuid,
                        Callback<ScheduleResponse> callback);


    @FormUrlEncoded
    @POST(ApiConstants.SCHEDULE_USER)
    void getSchedules(@Field("user_id") String user_id, @Field("uuid") String uuid, Callback<ScheduleResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.SCHEDULE_REQUEST)
    void requestSchedule(@Field("user_id") String user_id,
                         @Field("service_date_time") String service_date_time,
                         @Field("schedule_type") String schedule_type,
                         @Field("address_index") String address_index,
                         @Field("comp1") String comp1,
                         @Field("comp2") String comp2,
                         @Field("no") String no,
                         @Field("barrio") String barrio,
                         @Field("obs") String obs,
                         @Field("description") String description,
                         @Field("uuid") String uuid, Callback<ScheduleResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.USER_CONFIRM)
    void sendMailResetConfirm(@Field("email") String email,
                              @Field("token") String token,
                              @Field("password") String password,
                              Callback<ScheduleResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.USER_FORGOTTEN)
    void sendMailReset(@Field("email") String email, Callback<UserResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.SERVICE_COMPLAIN)
    void complain(@Field("service_id") String service_id,
                  @Field("descript") String descript,
                  @Field("uuid") String uuid, Callback<ServiceResponse> callback);

    @FormUrlEncoded
    @POST(ApiConstants.APP_VERSION)
    void appVersion(@Field("descript") String descript, Callback<AppResponse> callback);

}
