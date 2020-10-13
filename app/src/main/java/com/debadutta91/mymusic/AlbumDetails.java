package com.debadutta91.mymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.debadutta91.mymusic.MainActivity.musicFiles;

public class AlbumDetails extends AppCompatActivity {
RecyclerView recyclerView;
ImageView imageView;
String album;
ArrayList<MusicFiles>  arr=new ArrayList<>();
AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumdetails);
        recyclerView=findViewById(R.id.recycleview_layout);
        imageView=findViewById(R.id.album_photo);
        album=getIntent().getStringExtra("albumname");
        int j=0;
        for(int i=0;i<musicFiles.size();i++)
        {
if(album.equals(musicFiles.get(i).getAlbum()))
{
    arr.add(j,musicFiles.get(i));
    j++;
}
        }
        byte []image=getAlbumArt(arr.get(0).getPath());
        if(image!=null)
        {
            Glide.with(this).load(image).into(imageView);

        }
        else
        {
            Glide.with(this).load(R.drawable.mip).into(imageView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(arr.size()<1))
        {
albumDetailsAdapter=new AlbumDetailsAdapter(this,arr);
recyclerView.setAdapter(albumDetailsAdapter);
recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        }
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte []art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;

    }
}