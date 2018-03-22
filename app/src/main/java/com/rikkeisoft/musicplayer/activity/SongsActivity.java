package com.rikkeisoft.musicplayer.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.fragment.SongsFragment;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class SongsActivity extends AppCompatActivity {

    public static final String ID = "id";
    public static final String FLAG = "flag";
    public static final int SONGS_OF_ALBUM = 1;
    public static final int SONGS_OF_ARTIST = 2;

    public static Intent createIntent(Activity activity, String id, int flag) {
        Intent intent = new Intent(activity, SongsActivity.class);
        intent.putExtra(ID, id);
        intent.putExtra(FLAG, flag);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        int flag = intent.getIntExtra(FLAG, SONGS_OF_ALBUM);
        if(flag == SONGS_OF_ALBUM) initAlbum();
        else initArtist();

        FragmentManager fragmentManager = getSupportFragmentManager();
        SongsFragment songsFragment = (SongsFragment) fragmentManager.findFragmentByTag("SongsFragment");

        if(songsFragment == null) {
            songsFragment = SongsFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, songsFragment, "SongsFragment")
                    .commit();
        }
    }

    private void initAlbum() {
        AlbumItem album = Loader.findAlbum(this, getIntent().getStringExtra(ID));
        setSongs(album.getSongs(this));
    }

    private void initArtist() {
        ArtistItem artist = Loader.findArtist(this, getIntent().getStringExtra(ID));
        setSongs(artist.getSongs(this));
    }

    private void setSongs(List<SongItem> songs) {
        SongsModel songsModel = ViewModelProviders.of(this).get(SongsModel.class);
        songsModel.getItems().setValue(songs);
    }
}
