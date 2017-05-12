package com.imaginamos.usuariofinal.taxisya.io;

import com.imaginamos.usuariofinal.taxisya.BuildConfig;

/**
 * Created by leo on 8/31/15.
 */
public class ApiConstants {

    public static String app_code = "TAXISYA";
    //public static String app_secret_key = "oDX4b0Yk4usp0ptEf1XFK00KQSxAgV";
    public static String app_secret_key = "jN2Dk59eDX1h7W7NtiV3YYbzCFSHRY";
    public static boolean api_env = false;


    public static final String BASE_URL = BuildConfig.HOST + "/public";
    public static final String BASE_NODE = BuildConfig.HOST_NODE;

    // USER
    public static final String USER_LOGIN = "/user/login";
    public static final String USER_REGISTER = "/user/register";
    public static final String USER_UPDATE = "/user/update";
    public static final String USER_IS_LOGUED = "/user/islogued";
    public static final String USER_LOGOUT = "/user/logout";
    public static final String USER_FORGOTTEN = "/forgotten";
    public static final String USER_CONFIRM = "/user/pwd/confirm";

    // SERVICE
    public static final String GET_SERVICES = "/service/user";
    public static final String CANCEL_SERVICE = "/v2/user/{user_id}/cancelservice";
    public static final String CANCEL_SERVICE_SYSTEM = "/service/systemcancel";
    public static final String REQUEST_SERVICE = "/v2/user/{user_id}/requestservice";
    public static final String REQUEST_SERVICE_ADDRESS = "/v2/user/{user_id}/requestservice_address";

    public static final String SERVICE_SCORE = "/service/score";
    public static final String SERVICE_STATUS = "/service/status";
    public static final String SERVICE_COMPLAIN = "/complain/create";
    // ADDRESS
    public static final String GET_ADDRESS = "/address/byuser";
    public static final String DEL_ADDRESS = "/address/delete";
    public static final String ADD_ADDRESS = "/address/create";

    // SCHEDULE
    public static final String SCHEDULE_FINISH = "/schedule/finish";
    public static final String SCHEDULE_REQUEST = "/schedule/request";
    public static final String SCHEDULE_USER = "/schedule/user";
    public static final String SCHEDULE_CANCEL = "/schedule/cancel";  // usa node.js

    //
    public static final String APP_VERSION = "/app/versions";


}
