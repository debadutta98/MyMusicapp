package com.debadutta91.mymusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, ServiceConnection {
static ArrayList<MusicFiles> albums=new ArrayList<>();
    private static final int REQUEST_CODE = 1;
    static boolean shuffle=false,repeat=false;
    MusicService musicService;
static ArrayList<MusicFiles> musicFiles;
private String SORT_ORDER_PREF="SortOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewPager();
        perMission();

    }

    private void perMission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }
        else
        {
            musicFiles=getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {

                musicFiles=getAllAudio(this);
                initViewPager();
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        ViewPager viewPager=findViewById(R.id.view_pager);
        TabLayout tabLayout=findViewById(R.id.tab_layut);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SongFragment(),"Song");
        viewPagerAdapter.addFragment(new AlbumFragment(),"Album");
       viewPager.setAdapter(viewPagerAdapter);
       tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments;
        ArrayList<String> title;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.title=new ArrayList<>();
        }
public void addFragment(Fragment fragment,String titles)
{
    fragments.add(fragment);
    title.add(titles);

}

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return title.get(position);
        }
    }
public  ArrayList<MusicFiles> getAllAudio(Context context)
{
    SharedPreferences preferences=getSharedPreferences(SORT_ORDER_PREF,MODE_PRIVATE);
    String sortorder=preferences.getString("sorting","sortbyname");
    String order=null;
    ArrayList<String> duplicate =new ArrayList<>();
    albums.clear();
    ArrayList<MusicFiles> tempAudioList =new ArrayList<>();
    Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    switch (sortorder)
    {
        case "sortbyname":
            order=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
            break;
        case "sortbydate":
            order=MediaStore.MediaColumns.DATE_ADDED+" ASC";
            break;
        case "sortbysize":
            order=MediaStore.MediaColumns.SIZE+" DESC";
            break;

    }
    String[] projection={MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID};
    Cursor cursor=context.getContentResolver().query(uri,projection,null,null,order);
    if(cursor!=null)
    {
        while(cursor.moveToNext())
        {
            String album=cursor.getString(0);
            String title=cursor.getString(1);
            String duration=cursor.getString(2);
            String path=cursor.getString(3);
            String artist=cursor.getString(4);
            String id=cursor.getString(5);
            MusicFiles musicFiles=new MusicFiles(path,title,artist,album,duration,id);

            tempAudioList.add(musicFiles);
            if(!duplicate.contains(album))
            {
albums.add(musicFiles);

duplicate.add(album);
            }
        }
        cursor.close();
    }
    return tempAudioList;
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
MenuItem menuItem=menu.findItem(R.id.search_option);
SearchView searchView=(SearchView)menuItem.getActionView();
searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String ui=newText.toLowerCase();
        ArrayList<MusicFiles> mfiles=new ArrayList<>();
for(MusicFiles song:musicFiles)
{
    if(song.getTitle().toLowerCase().contains(ui))
    {
        mfiles.add(song);
    }
}
SongFragment.musicAdapter.updateALL(mfiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor  editor= getSharedPreferences(SORT_ORDER_PREF,MODE_PRIVATE).edit();
        switch(item.getItemId())
        {
            case R.id.sort_by_name:
                editor.putString("sorting","sortbyname");
                editor.apply();
                break;
            case R.id.sort_by_date:
            editor.putString("sorting","sortbydate");
            editor.apply();
            break;
            case R.id.sort_by_size:
                editor.putString("sorting","sortbysize");
                editor.apply();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}