package com.rikkeisoft.musicplayer.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.AppbarActivity;
import com.rikkeisoft.musicplayer.custom.adapter.pager.MainPagerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.ArtistsModel;
import com.rikkeisoft.musicplayer.model.base.BaseListModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.utils.Loader;

import java.util.List;

public class MainActivity extends AppbarActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;

    private ViewPager viewPager;
    private MainPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("debug", "onCreate MainActivity");

        init();

    }

    @Override
    protected void init() {
        super.init();

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        setTitleTap(0, R.string.songs);
        setTitleTap(1, R.string.albums);
        setTitleTap(2, R.string.artists);
    }

    @Override
    protected void findView() {
        super.findView();

        viewPager = findViewById(R.id.view_pager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    loadData();
                }
                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Log.d("debug", "permission denied");
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d("debug", "should Show Request Permission Rationale");

                requestPermissions();

            }
            else {
                // No explanation needed; request the permission
                requestPermissions();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            // Permission has already been granted

            loadData();
        }
    }

    void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    void loadData() {
        Log.d("debug", "loadData");

        //
        List<SongItem> songs = Loader.loadSongs(this);

        BaseListModel<SongItem> songsModel = ViewModelProviders.of(this).get(SongsModel.class);
        songsModel.getItems().setValue(songs);

        //
        List<AlbumItem> albums = Loader.loadAlbums(this);

        AlbumsModel albumsModel = ViewModelProviders.of(this).get(AlbumsModel.class);
        albumsModel.getItems().setValue(albums);

        //
        List<ArtistItem> artists = Loader.loadArtists(this);

        ArtistsModel artistsModel = ViewModelProviders.of(this).get(ArtistsModel.class);
        artistsModel.getItems().setValue(artists);
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
