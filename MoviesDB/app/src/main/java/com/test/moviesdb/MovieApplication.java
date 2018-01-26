package com.test.moviesdb;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Farhan on 1/22/2018.
 */

/*
 * Application Class to be used as Singleton in current application
 * it can be used if there is a need to get application object and anything
 * associated with it
 */

public class MovieApplication extends Application{
    private static final String TAG = MovieApplication.class.getSimpleName();

    private static MovieApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MovieApplication getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    /*
     * Function to check if the mobile on which app is running has internet connection
     */
    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
