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
    public static final String ACTION_PREV="actionprevious";
    public static final String ACTION_NEXT="actionnext";
    public static final String ACTION_PLAY="actionplay";

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
        String actionname=intent.getStringExtra("Myaction");
        if(actionname!=null) {
            switch (actionname) {
                case ACTION_PLAY:
if(actionPlayer!=null)
    actionPlayer.playbuttonClicked();
                    break;
                case ACTION_NEXT:
                    if(actionPlayer!=null)
                        actionPlayer.nextbuttonClicked();
                    break;
                case ACTION_PREV:
                    if(actionPlayer!=null)
                        actionPlayer.prevbuttonClicked();
                    break;
            }
        }
        return START_STICKY;
    }
    public void setCallback(ActionPlayer actionPlayer1)
    {
this.actionPlayer=actionPlayer1;
    }

}
