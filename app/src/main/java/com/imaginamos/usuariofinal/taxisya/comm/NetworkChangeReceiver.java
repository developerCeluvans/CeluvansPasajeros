package com.imaginamos.usuariofinal.taxisya.comm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Observable;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private NetworkReceiverListener listener;

    public NetworkChangeReceiver(){

    }

    public NetworkChangeReceiver(NetworkReceiverListener listener){

        this.listener = listener;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("NetworkChangeReceiver", "Connection status changed");
        getObservable().connectionChanged();
        if(listener != null) {

            listener.onNetworkConnectivityChange(Connectivity.isConnected(context));

        }
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 www.taxisya.co");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


        return false;
    }

    public interface NetworkReceiverListener{

        void onNetworkConnectivityChange(boolean connected);
    }


    public static class NetworkObservable extends Observable{
        private static NetworkObservable instance = null;

        private NetworkObservable() {
            // Exist to defeat instantiation.
        }

        public void connectionChanged(){
            //setChanged();
            //notifyObservers();
        }

        public static NetworkObservable getInstance(){
             if(instance == null){
                 instance = new NetworkObservable();
             }
             return instance;
        }
    }


    public static NetworkObservable getObservable() {
        return NetworkObservable.getInstance();
    }
}
