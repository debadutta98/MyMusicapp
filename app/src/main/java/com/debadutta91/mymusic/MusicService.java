package com.debadutta91.mymusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class MusicService extends Service {
    IBinder myBinder =new MyBinder();
    MediaPlayer mediaPlayer;
    ActionPlayer actionPlayer;
    ArrayList<MusicFiles> musicFilesArrayList=new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }
public class MyBinder extends Binder
{

    MusicService getService()
    {
return MusicService.this;
    }
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
