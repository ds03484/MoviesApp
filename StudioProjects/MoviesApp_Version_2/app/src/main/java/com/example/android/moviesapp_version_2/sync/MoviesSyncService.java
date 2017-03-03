package com.example.android.moviesapp_version_2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by ds034_000 on 1/30/2017.
 */

public class MoviesSyncService extends Service {
    private static final Object movieSyncAdapterLock = new Object();
    private static MoviesSyncAdapter movieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MoviesSyncService", "onCreate - MoviesSyncService");
        synchronized (movieSyncAdapterLock) {
            if (movieSyncAdapter == null) {
                movieSyncAdapter = new MoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return movieSyncAdapter.getSyncAdapterBinder();
    }
}
