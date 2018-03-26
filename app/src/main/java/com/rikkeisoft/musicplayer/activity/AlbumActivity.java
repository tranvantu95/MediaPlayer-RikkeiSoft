package com.rikkeisoft.musicplayer.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.AppbarActivity;
import com.rikkeisoft.musicplayer.activity.fragment.SongsFragment;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class AlbumActivity extends AppbarActivity {

    public static final String ID = "id";

    private AlbumItem album;

    public static Intent createIntent(Context context, String id) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra(ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        init();
    }

    @Override
    protected void init() {
        super.init();

        addFragment();
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        showHomeButton();
    }

    @Override
    protected void onPermissionGranted() {
        super.onPermissionGranted();

        findAlbum();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SongsFragment songsFragment = (SongsFragment) fragmentManager.findFragmentByTag("SongsFragment");
        if(songsFragment == null) songsFragment = SongsFragment.newInstance();

        if(!songsFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, songsFragment, "SongsFragment")
                    .commit();
        }
    }

    private void findAlbum() {
        if(album == null) album = Loader.getInstance().findAlbum(getIntent().getStringExtra(ID));
        if(album == null) {
            finish();
            return;
        }

        if(album.getBmAlbumArt() != null) appbarImage.setImageBitmap(album.getBmAlbumArt());
        setTitle(album.getName());

        setSongs(album.getSongs());
    }

    private void setSongs(List<SongItem> songs) {
        SongsModel songsModel = ViewModelProviders.of(this).get(SongsModel.class);
        songsModel.getItems().setValue(songs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
