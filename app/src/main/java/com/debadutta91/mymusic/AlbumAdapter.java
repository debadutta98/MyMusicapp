package com.debadutta91.mymusic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyVIewHolder> {
    private Context context;
    private ArrayList<MusicFiles> albumfiles;
View view;
    public AlbumAdapter(Context context, ArrayList<MusicFiles> albumfiles) {
        this.context = context;
        this.albumfiles = albumfiles;
    }

    @NonNull
    @Override
    public MyVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(context).inflate(R.layout.album_iteam,parent,false);
        return new MyVIewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVIewHolder holder, final int position) {
        holder.textView.setText(albumfiles.get(position).getAlbum());
        byte[] image=getAlbumArt(albumfiles.get(position).getPath());
        if(image!=null)
        {
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.imageView);
        }
        else
        {
            Glide.with(context)
                    .load(R.drawable.mip).into(holder.imageView);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AlbumDetails.class);
                intent.putExtra("albumname",albumfiles.get(position).getAlbum());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumfiles.size();
    }

    public class MyVIewHolder extends RecyclerView.ViewHolder
    {
ImageView imageView;
TextView textView;
        public MyVIewHolder(@NonNull View itemView) {
            super(itemView);
           imageView= itemView.findViewById(R.id.album_image);
            textView=itemView.findViewById(R.id.android_name);

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
