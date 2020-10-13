package com.debadutta91.mymusic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.debadutta91.mymusic.AlbumDetailsAdapter.albumfiles;
import static com.debadutta91.mymusic.MainActivity.musicFiles;
import static com.debadutta91.mymusic.MainActivity.repeat;
import static com.debadutta91.mymusic.MainActivity.shuffle;


public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,ActionPlayer ,ServiceConnection {
TextView artist_name,song_name,duration_total,duration_played;
SeekBar seekBar;
int postion=-1;
MusicService musicService;
ImageView cover_art,next_btn,prev_btn,back_btn,shuffle_btn,repeate_btn;
FloatingActionButton playbutton;
static Uri uri;
private Handler handler=new Handler();
static MediaPlayer mediaPlayer;
static ArrayList<MusicFiles> song=new ArrayList<>();
private Thread playthread,nextthread,prevthread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initview();
        getIntentMethod();
       song_name.setText(song.get(postion).getTitle());
       artist_name.setText(song.get(postion).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                {
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null)
                {
                    int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formatted(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle)
                {
                    shuffle=false;
                    shuffle_btn.setImageResource(R.drawable.ic_baseline_shuffle_off);
                }
                else
                {
                    shuffle=true;
                    shuffle_btn.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
            }
        });
        repeate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeat)
                {
                    repeat=false;
                    shuffle_btn.setImageResource(R.drawable.ic_baseline_shuffle_off);

                }
                else
                {
                    repeat=true;
                    repeate_btn.setImageResource(R.drawable.ic_baseline_repeat);
                }
            }
        });
    }

    private String formatted(int mCurrentPosition) {
        String totalout="";
        String totalnew="";
        String second=String.valueOf(mCurrentPosition%60);
        String values=String.valueOf(mCurrentPosition/60);
        totalout=values+":"+second;
        totalnew=values+":"+"0"+second;
        if(second.length()==1)
        {
            return totalnew;
        }
        else
        {
            return totalout;
        }
    }

    private void getIntentMethod() {
        postion=getIntent().getIntExtra("position",-1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null && sender.equals("albumdetails"))
        {
            song=albumfiles;
        }
        else
        {
            song=MusicAdapter.musicFiles;
        }
        if(song!=null)
        {
           playbutton.setImageResource(R.drawable.ic_baseline_pause);
            uri=Uri.parse(song.get(postion).getPath());

        }
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);
    }

    private void initview() {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        duration_played=findViewById(R.id.duration_played);
        duration_total=findViewById(R.id.duration_total);
        seekBar=findViewById(R.id.seek_bar);
        cover_art=findViewById(R.id.cover_art);
        next_btn=findViewById(R.id.next);
        prev_btn=findViewById(R.id.prev);
        back_btn=findViewById(R.id.back_btn);
        shuffle_btn=findViewById(R.id.shuffle);
        repeate_btn=findViewById(R.id.repeat);
        playbutton=findViewById(R.id.play_pause);
    }
    private void metaData(Uri uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());
        int durationtotal=Integer.parseInt(song.get(postion).getDuration())/1000;
        duration_total.setText(formatted(durationtotal));
        byte []art=mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap;

        if(art!=null)
        {
            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null)
                    {
ImageView imageView=findViewById(R.id.imageviewgradient);
                        RelativeLayout mcontainer=findViewById(R.id.mcontainer);
                        imageView.setBackgroundResource(R.drawable.gradiant_bg);
                        mcontainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        imageView.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawables=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        ImageView imageView=findViewById(R.id.imageviewgradient);
                        RelativeLayout mcontainer=findViewById(R.id.mcontainer);
                        imageView.setBackgroundResource(R.drawable.gradiant_bg);
                        mcontainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        imageView.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawables=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }

            });
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.mip).into(cover_art);
            ImageView imageView=findViewById(R.id.imageviewgradient);
            RelativeLayout mcontainer=findViewById(R.id.mcontainer);
            imageView.setBackgroundResource(R.drawable.gradiant_bg);
            mcontainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }

    @Override
    protected void onResume() {
        Intent intent=new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadButton();
        nextThreadButton();
        prevThreadButton();
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);

    }

    private void prevThreadButton() {
        prevthread= new Thread() {
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevbuttonClicked();
                    }
                });
            }
        };
        prevthread.start();
    }

    public void prevbuttonClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat)
            {
                postion=getRamdom(song.size()-1);
            }
            else if(!shuffle && !repeat) {
                postion=((postion-1)<0?(song.size()-1):(postion-1));
            }
            uri=Uri.parse(song.get(postion).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(song.get(postion).getTitle());
            artist_name.setText(song.get(postion).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playbutton.setBackgroundColor(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }
        else
        {

            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat)
            {
                postion=getRamdom(song.size()-1);
            }
            else if(!shuffle && !repeat) {
                postion=((postion-1)<0?(song.size()-1):(postion-1));
            }
            uri=Uri.parse(song.get(postion).getPath());
             mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(song.get(postion).getTitle());
            artist_name.setText(song.get(postion).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playbutton.setBackgroundColor(R.drawable.ic_baseline_pause);
        }
    }

    private void nextThreadButton() {
        nextthread= new Thread() {
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextbuttonClicked();
                    }
                });
            }
        };
        nextthread.start();
    }

    public void nextbuttonClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat)
            {
                postion=getRamdom(song.size()-1);
            }
            else if(!shuffle && !repeat) {
                postion=((postion+1)%song.size());
            }
            uri=Uri.parse(song.get(postion).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(song.get(postion).getTitle());
            artist_name.setText(song.get(postion).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playbutton.setBackgroundColor(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }
        else
        {

            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffle && !repeat)
            {
                postion=getRamdom(song.size()-1);
            }
            else if(!shuffle && !repeat) {
                postion=((postion+1)%song.size());
            }
            uri=Uri.parse(song.get(postion).getPath());
             mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(song.get(postion).getTitle());
            artist_name.setText(song.get(postion).getArtist());
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playbutton.setBackgroundColor(R.drawable.ic_baseline_pause);
        }
    }

    private int getRamdom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void playThreadButton() {
        playthread= new Thread() {
            @Override
            public void run() {
                super.run();
                playbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playbuttonClicked();
                    }
                });
            }
        };
        playthread.start();
    }

   public void playbuttonClicked() {
        if(mediaPlayer.isPlaying())
        {
            playbutton.setImageResource(R.drawable.ic_baseline_play_arrow);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
            playbutton.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }
    public  void ImageAnimation(final Context context, final ImageView imageView, final Bitmap bitmap)
    {
        Animation animation_out= AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        final Animation animation_in= AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
        animation_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
Glide.with(context).load(bitmap).into(imageView);
animation_in.setAnimationListener(new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
});
imageView.startAnimation(animation_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation_out);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextbuttonClicked();
        if(mediaPlayer!=null)
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);

        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder= (MusicService.MyBinder)service;
        musicService=myBinder.getService();
        Toast.makeText(this,"connected"+musicService,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
musicService=null;
    }
}