package com.example.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PauseService extends Service {
    public PauseService() {

    }

    @Override
    public int onStartCommand(Intent data,int sid,int flag)
    {

        GlobalMedia.mp.pause();

        //return START_STICKY;
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}