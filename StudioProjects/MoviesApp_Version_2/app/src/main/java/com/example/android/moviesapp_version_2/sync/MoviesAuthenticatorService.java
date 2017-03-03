package com.example.android.moviesapp_version_2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ds034_000 on 1/23/2017.
 */

public class MoviesAuthenticatorService extends Service {
    //variable to call MoviesAuthenticator
    private MoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MoviesAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
