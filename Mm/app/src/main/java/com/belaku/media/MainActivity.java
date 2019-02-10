package com.belaku.media;

import android.Manifest;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import rm.com.audiowave.AudioWaveView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    public static ArrayList<AudioSong> mAudioSongs = new ArrayList<>(), favoriteSongs = new ArrayList<>(), plSongs = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ArrayList<String> songNames = new ArrayList<>(), favoriteSongNames = new ArrayList<>();
    private MyAdapter artistAdapter, albumAdapter, songsAdapter, favoriteSongsAdapter,plNamesAdapter;
    private boolean permission;
    private Cursor musicCursor;
    private Cursor musicCursorAlbumArt;
    private Map<String, String> mapAlbumArtIDs = new HashMap();
    private ViewPager vpPager;
    private static Context mContext;
    private static ArrayList<String> mSongs, mArtists, mAlbums;
    private ImageButton ImgBtnBack;
    public static ArrayList<Playlist> playlists;
    private ArrayList<String> plNames = new ArrayList<>();
    private ArrayList<String> playlistNames = new ArrayList<>();
    private ArrayList<String> plSongNames = new ArrayList<>();
    private ImageButton BtnSearch;
    private EditText editTextSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        ImgBtnBack = findViewById(R.id.imgbtn_back);
        BtnSearch = findViewById(R.id.btn_search);
        editTextSearch = findViewById(R.id.edtx_search);

        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextSearch.getText().toString().trim().length() > 0)
                startActivity(new Intent(MainActivity.this, WebActivity.class).putExtra("Srch", editTextSearch.getText().toString()));
            }
        });


        /*playlistNames.add("LOVE");
        playlistNames.add("HAPPINESS");*/


        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                final ArrayList<String> songsOfArtist = new ArrayList<>(), songsOfAlbum = new ArrayList<>();
                final ArrayList<Integer> songPosInAlbum = new ArrayList<>(), songPosInArtist = new ArrayList<>();
                if (position == 0) {
                    ImgBtnBack.setVisibility(View.INVISIBLE);
                    if (mArtists != null) {
                        if (mArtists.size() > 0) {
                            artistAdapter = new MyAdapter(mContext, mArtists, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    songsOfArtist.clear();
                                    songPosInArtist.clear();
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Artists->Songs \n \n \n", Snackbar.LENGTH_LONG).show();
                                    ImgBtnBack.setVisibility(View.VISIBLE);
                                    ImgBtnBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ImgBtnBack.setVisibility(View.INVISIBLE);
                                            mRecyclerView.setAdapter(artistAdapter);
                                        }
                                    });
                                    for (int i = 0; i < mAudioSongs.size(); i++) {
                                        if (mAudioSongs.get(i).getArtist().toString().equals(mArtists.get(position))) {
                                            if (!songsOfArtist.contains(mAudioSongs.get(i).getTitle())) {
                                                songsOfArtist.add(mAudioSongs.get(i).getTitle());
                                                songPosInArtist.add(i);
                                            }
                                        }
                                        MyAdapter artistAdapter = new MyAdapter(mContext, songsOfArtist, new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int position) {
                                                Gson gson = new Gson();
                                                String jsonString = gson.toJson(mAudioSongs);

                                                Intent pIntent = new Intent(MainActivity.this, PlayerActivity.class).putExtra("position", songPosInArtist.get(position))
                                                        .putExtra("Pageposition", position)
                                                        .putExtra("KEY", jsonString);
                                                startActivity(pIntent);
                                            }
                                        });
                                        mRecyclerView.setAdapter(artistAdapter);
                                    }
                                }
                            });
                            mRecyclerView.setAdapter(artistAdapter);
                        }
                    }
                } else if (position == 1) {
                    ImgBtnBack.setVisibility(View.INVISIBLE);
                    if (mSongs != null) {
                        if (mSongs.size() > 0) {
                            songsAdapter = new MyAdapter(mContext, mSongs, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(mAudioSongs);

                                    Intent pIntent = new Intent(MainActivity.this, PlayerActivity.class).putExtra("position", position)
                                            .putExtra("KEY", jsonString);
                                    startActivity(pIntent);
                                }
                            });
                            mRecyclerView.setAdapter(songsAdapter);
                        }
                    }
                } else if (position == 2) {
                    ImgBtnBack.setVisibility(View.INVISIBLE);
                    if (mAlbums != null) {
                        if (mAlbums.size() > 0) {
                            albumAdapter = new MyAdapter(mContext, mAlbums, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    songsOfAlbum.clear();
                                    songPosInAlbum.clear();
                                    Snackbar.make(getWindow().getDecorView().getRootView(), "Albums->Songs \n \n \n", Snackbar.LENGTH_LONG).show();
                                    ImgBtnBack.setVisibility(View.VISIBLE);
                                    ImgBtnBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ImgBtnBack.setVisibility(View.INVISIBLE);
                                            mRecyclerView.setAdapter(albumAdapter);
                                        }
                                    });

                                    for (int i = 0; i < mAudioSongs.size(); i++) {
                                        if (mAudioSongs.get(i).getAlbum().toString().equals(mAlbums.get(position))) {
                                            if (!songsOfAlbum.contains(mAudioSongs.get(i).getTitle())) {
                                                songsOfAlbum.add(mAudioSongs.get(i).getTitle());
                                                songPosInAlbum.add(i);
                                            }
                                        }
                                        MyAdapter albumAdapter = new MyAdapter(mContext, songsOfAlbum, new CustomItemClickListener() {
                                            @Override
                                            public void onItemClick(View v, int position) {
                                                Gson gson = new Gson();
                                                String jsonString = gson.toJson(mAudioSongs);

                                                Intent pIntent = new Intent(MainActivity.this, PlayerActivity.class).putExtra("position", songPosInAlbum.get(position))
                                                        .putExtra("KEY", jsonString);
                                                startActivity(pIntent);
                                            }
                                        });
                                        mRecyclerView.setAdapter(albumAdapter);
                                    }
                                }
                            });
                            mRecyclerView.setAdapter(albumAdapter);
                        }
                    }
                } else if (position == 3) {
                    favoriteSongs.clear();
                    favoriteSongNames.clear();
                    ImgBtnBack.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < mAudioSongs.size(); i++) {
                        if (mAudioSongs.get(i).isFavorite)
                            favoriteSongs.add(mAudioSongs.get(i));
                    }
                    if (favoriteSongs.size() > 0) {
                        for (int i = 0; i < favoriteSongs.size(); i++) {
                            favoriteSongNames.add(favoriteSongs.get(i).getTitle());
                        }
                        if (favoriteSongNames.size() > 0) {
                            favoriteSongsAdapter = new MyAdapter(mContext, favoriteSongNames, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(favoriteSongs);

                                    Intent pIntent = new Intent(MainActivity.this, PlayerActivity.class).putExtra("position", position)
                                            .putExtra("KEY", jsonString);
                                    startActivity(pIntent);
                                }
                            });
                            mRecyclerView.setAdapter(favoriteSongsAdapter);
                        }
                    } else mRecyclerView.setAdapter(null);

                } else if (position == 4) {
                loadPlList();
                } else if (position == 5) {
                    final ArrayList<String> top10 = new ArrayList<>();
                    top10.add("Top 10 International");
                    top10.add("Bollywood");
                    top10.add("Kannada");
                    mRecyclerView.setAdapter(new MyAdapter(getApplicationContext(), top10, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            makeToast("yet2impl - " + top10.get(position));
                        }
                    }));
                }
            }


            private void loadPlList() {
                SharedPreferences sharedPreferences = getSharedPreferences("PList", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferences.getString("PlayList", null);
                Type type = new TypeToken<ArrayList<Playlist>>() {
                }.getType();
                if (json != null) {
                //    final ArrayList<Playlist> savedPlaylist = gson.fromJson(json, type);
                    setPlAdapter(gson, json, type);
                }
            }

            private void setPlAdapter(Gson gson, String json, Type type) {
                /*SharedPreferences sharedPreferences = getSharedPreferences("PList", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferences.getString("PlayList", null);
                Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
                if (json != null) {*/
                    final ArrayList<Playlist> savedPlaylist = gson.fromJson(json, type);
                    for (int i = 0 ; i < savedPlaylist.size(); i++) {
                        if (!playlistNames.contains(savedPlaylist.get(i).getPlName()))
                            playlistNames.add(savedPlaylist.get(i).getPlName());
                    }
                    mRecyclerView.setAdapter(new MyAdapter(getApplicationContext(), playlistNames, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                            for (int i = 0 ; i < savedPlaylist.size(); i++) {
                                if (savedPlaylist.get(i).getPlName().equals(playlistNames.get(position).toString())) {
                                    plSongs.clear();
                                    plSongs.addAll(savedPlaylist.get(i).getPlSongs());
                                }
                            }
                            if (plSongs.size() > 0) {
                                makeToast("retreving songs from Playlist : " + playlistNames.get(position));
                                for (int i = 0 ; i < plSongs.size() ; i++) {
                                    if (!plSongNames.contains(plSongs.get(i).getTitle()))
                                    plSongNames.add(plSongs.get(i).getTitle());
                                }
                                mRecyclerView.setAdapter(new MyAdapter(getApplicationContext(), plSongNames, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        Gson gson = new Gson();
                                        String jsonString = gson.toJson(plSongs);

                                        Intent pIntent = new Intent(MainActivity.this, PlayerActivity.class).putExtra("position", position)
                                                .putExtra("KEY", jsonString);
                                        startActivity(pIntent);
                                    }
                                }));
                                plSongs.clear();
                            } else makeToast("no Songs in playlist");
                        }
                    }));
            //    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mContext = getApplicationContext();
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (MusicService.getInstance() != null) {
            if (MusicService.getInstance().isPlaying())
                makeToast("YesYAY");
            else makeToast("NotPlaying");
        }

        MediaCheckPermission();

        if (permission) {
            ReadAudio();

            //      mRecyclerView.setAdapter(adapter);
        }

    }

    public ArrayList<String> getArrayListOfPlNames(String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> value = preferences.getStringSet(key, null);
        if (value != null)
        return new ArrayList<>(value);
        else return null;
    }


    private void MediaCheckPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            makeToast("Storage permission granted already");
            permission = true;
            MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            vpPager.setAdapter(adapterViewPager);
            vpPager.setCurrentItem(1);
        } else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    {
                        ReadAudio();
                        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
                        vpPager.setAdapter(adapterViewPager);
                        vpPager.setCurrentItem(1);
                    }


                    for (int i = 0; i < mAudioSongs.size(); i++) {

                        mAudioSongs.get(i).getAlbum();
                    }
                    if (ReadAudio().size() > 0) {
                        makeToast("Number of Audio Songs in Device - " + mAudioSongs.size());
                    } else makeToast("No Audio Songs in Device");
                } else
                    makeToast("Permission denied ! Booo, app won't work without access to Media files in storage.");
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (true) {
        } else {
            super.onBackPressed();
        }
    }

    private void makeToast(String s) {
        //    Snackbar.make(getWindow().getDecorView().getRootView(), s , Snackbar.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<AudioSong> ReadAudio() {
        Uri musicUrl;
        ContentResolver mContentResolver = getContentResolver();

        musicUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Perform a query on the content resolver
        musicCursor = mContentResolver.query(musicUrl, null, MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        if (musicCursor == null) {
            // Query failed...
            makeToast("Failed to retrieve music: cursor is null :-(");
            return mAudioSongs;
        }
        if (!musicCursor.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            makeToast("Failed to move cursor to first row (no query results).");
            return mAudioSongs;
        }

        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

        do {
            try {
                AudioSong audioSong = new AudioSong(
                        musicCursor.getLong(idColumn),
                        null,
                        musicCursor.getString(pathColumn),
                        musicCursor.getString(artistColumn),
                        musicCursor.getString(titleColumn),
                        musicCursor.getString(albumColumn),
                        musicCursor.getLong(durationColumn),
                        false);
                if (!mAudioSongs.contains(audioSong))
                    mAudioSongs.add(audioSong);
            } catch (IllegalStateException ex) {
                //    makeToast("ILLEXP" + ex);
            }
        } while (musicCursor.moveToNext());

        ContentResolver musicResolve = getContentResolver();
        Uri smusicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;


        musicCursorAlbumArt = musicResolve.query(smusicUri, null         //should use where clause(_ID==albumid)
                , null, null, null);

        if (musicCursorAlbumArt == null) {
            // Query failed...
            makeToast("Failed to retrieve music: cursor is null :-(");
            return mAudioSongs;
        }
        if (!musicCursorAlbumArt.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            makeToast("Failed to move cursor to first row (no query results).");
            return mAudioSongs;
        }

        int albumArtColumn = musicCursorAlbumArt.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART);
        int albumIdColumn = musicCursorAlbumArt.getColumnIndex(MediaStore.Audio.Media.ALBUM);


        do {
            try {
                musicCursorAlbumArt.getString(albumArtColumn);
                mapAlbumArtIDs.put(musicCursorAlbumArt.getString(albumIdColumn), musicCursorAlbumArt.getString(albumArtColumn));
            } catch (IllegalStateException ex) {
                //   makeToast("ILLEXP???" + ex);
            }
        } while (musicCursorAlbumArt.moveToNext());

        ArrayList keyAlbumIdList = new ArrayList();
        keyAlbumIdList.addAll(mapAlbumArtIDs.keySet());

        for (int i = 0; i < mAudioSongs.size(); i++) {
            for (int u = 0; u < mapAlbumArtIDs.keySet().size(); u++) {
                if (mAudioSongs.get(i).getAlbum().equals(keyAlbumIdList.get(u))) {
                    if (mapAlbumArtIDs.get(mAudioSongs.get(i).getAlbum()) != null) {
                        mAudioSongs.get(i).setAlbumArt(mapAlbumArtIDs.get(mAudioSongs.get(i).getAlbum()));
                        Log.d("HERE??? - ", mapAlbumArtIDs.get(mAudioSongs.get(i).getAlbum()));
                    }
                }
            }
        }


        for (int i = 0; i < mAudioSongs.size(); i++) {
            songNames.add(mAudioSongs.get(i).getTitle());
        }
        return mAudioSongs;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.ƒ

        return super.onOptionsItemSelected(item);
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 6;
        private MyAdapter adapter;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            mSongs = new ArrayList<>();
            mArtists = new ArrayList<>();
            mAlbums = new ArrayList<>();

            for (int i = 0; i < mAudioSongs.size(); i++) {
                mSongs.add(mAudioSongs.get(i).getTitle());
                mArtists.add(mAudioSongs.get(i).getArtist());
                mAlbums.add(mAudioSongs.get(i).getAlbum());

                if (vpPager.getCurrentItem() == 1) {
                    if (mSongs.size() > 0) {
                        adapter = new MyAdapter(mContext, mSongs, new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(mAudioSongs);

                                Intent pIntent = new Intent(MainActivity.this, PlayerActivity.class).putExtra("position", position)
                                        .putExtra("KEY", jsonString);
                                startActivity(pIntent);
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }

            Set<String> setAr = new TreeSet<>(mArtists);
            mArtists = new ArrayList<String>(setAr);

            Set<String> setAl = new TreeSet<>(mAlbums);
            mAlbums = new ArrayList<String>(setAl);

            switch (position) {

                case 0: // Fragment # 0 - This will show FirstFragment
                    return MainFragment.newInstance(mContext, 0, "Artists", mAudioSongs);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return MainFragment.newInstance(mContext, 1, "Songs", mAudioSongs);
                case 2: // Fragment # 1 - This will show SecondFragment
                    return MainFragment.newInstance(mContext, 2, "Albums", mAudioSongs);
                case 3: // Fragment # 1 - This will show SecondFragment
                    return MainFragment.newInstance(mContext, 3, "Favorites", mAudioSongs);
                case 4: // Fragment # 1 - This will show SecondFragment
                    return MainFragment.newInstance(mContext, 4, "Playlists", mAudioSongs);
                case 5: // Fragment # 1 - This will show SecondFragment
                    return MainFragment.newInstance(mContext, 4, "Top 10", mAudioSongs);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Artists";
            else if (position == 1)
                return "Songs";
            else if (position == 2)
                return "Albums";
            else if (position == 3)
                return "Favorites";
            else if (position == 4)
                return "Playlists";
            else if (position == 5)
                return "Top 10";
            else
                return null;
        }

    }
}
