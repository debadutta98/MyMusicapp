package com.debadutta91.mymusic;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context context;
     static ArrayList<MusicFiles> musicFiles;
MusicAdapter(Context mcontext,ArrayList<MusicFiles> mfiles)
{
    this.context=mcontext;
    this.musicFiles=mfiles;
}
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(context).inflate(R.layout.music_iteam,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
holder.filename.setText(musicFiles.get(position).getTitle());
byte[] image=getAlbumArt(musicFiles.get(position).getPath());
if(image!=null)
{
    Glide.with(context).asBitmap()
    .load(image)
            .into(holder.album_art);
}
else
{
    Glide.with(context)
            .load(R.drawable.mip).into(holder.album_art);

}
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(context,PlayerActivity.class);
        intent.putExtra("position",position);
        context.startActivity(intent);
    }
});
holder.more_menu.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(final View v) {
        PopupMenu popupMenu=new PopupMenu(context,v);
        popupMenu.getMenuInflater().inflate(R.menu.pop_up,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.delete_song:
                        delete_file(position,v);
                        break;
                }
                return true;
            }
        });
    }
});
    }
private void delete_file(int p,View v)
{
    Uri uri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(musicFiles.get(p).getId()));

    File file=new File(musicFiles.get(p).getPath());
    boolean filedelete=file.delete();
    if(filedelete) {
        context.getContentResolver().delete(uri,null,null);
        musicFiles.remove(p);
        notifyItemRemoved(p);
        notifyItemRangeChanged(p, musicFiles.size());
        Snackbar.make(v, "File Deleted", Snackbar.LENGTH_LONG)
                .show();
    }
    else
    {
        Snackbar.make(v, "Can't be delete", Snackbar.LENGTH_LONG)
                .show();
    }
}


    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder
    {
TextView filename;
ImageView album_art,more_menu;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            filename=itemView.findViewById(R.id.file_name);
            album_art=itemView.findViewById(R.id.music_image);
            more_menu=itemView.findViewById(R.id.menu_more);
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
public  void updateALL(ArrayList<MusicFiles> musicFilesArrayList)
{
    musicFiles=new ArrayList<>();
    musicFiles.addAll(musicFilesArrayList);
    notifyDataSetChanged();
}

}
