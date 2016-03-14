package com.omdbexample.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
public class InternetConexion {
    private static InternetConexion instance;
    private ConnectivityManager connectivityManager;
    private NetworkInfo ni;

    public InternetConexion(Context c){
        connectivityManager = (ConnectivityManager)  c.getSystemService(c.CONNECTIVITY_SERVICE);
    }

    public static InternetConexion getInstance(Context c) {
        if (instance == null)
            instance = new InternetConexion(c);
        return instance;
    }

    public boolean hasInternetConexion(){
        ni = connectivityManager.getActiveNetworkInfo();
        if(ni != null && ni.isConnectedOrConnecting() && ni.isAvailable())
            return true;
        return false;
    }



}
