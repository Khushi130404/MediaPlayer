package com.example.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlayService extends Service {
    public PlayService()
    {}

    @Override
    public int onStartCommand(Intent i,int sid,int flag)
    {
        GlobalMedia.mp.start();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}