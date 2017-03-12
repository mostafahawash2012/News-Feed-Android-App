package com.example.mostafa.newsfeed.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
//A component that allows the sync adapter framework to run the code in your sync adapter class.
public class NewsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NewsSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.e("NewsSyncService", "onCreate - NewsSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new NewsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}