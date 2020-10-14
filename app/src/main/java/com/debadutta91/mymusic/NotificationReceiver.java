package com.debadutta91.mymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;




public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_PREV="actionprevious";
    public static final String ACTION_NEXT="actionnext";
    public static final String ACTION_PLAY="actionplay";

    @Override

    public void onReceive(Context context, Intent intent) {

        Intent intent1=new Intent(context,MusicService.class);
if(intent.getAction()!=null)
{
    switch (intent.getAction())
{
    case ACTION_PLAY:
        Log.e("hello","play");
        Toast.makeText(context,"play",Toast.LENGTH_SHORT).show();
        intent1.putExtra("Myaction",intent.getAction());
        context.startService(intent1);
break;
    case ACTION_NEXT:
        Toast.makeText(context,"next",Toast.LENGTH_SHORT).show();
        intent1.putExtra("Myaction",intent.getAction());
        context.startService(intent1);
        break;
    case ACTION_PREV:
        Toast.makeText(context,"prev",Toast.LENGTH_SHORT).show();
        intent1.putExtra("Myaction",intent.getAction());
        context.startService(intent1);
        break;
}

}
    }
}
