package com.rikkeisoft.musicplayer.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseActivity;
import com.rikkeisoft.musicplayer.custom.adapter.MainPagerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsMainModel;
import com.rikkeisoft.musicplayer.model.ArtistsMainModel;
import com.rikkeisoft.musicplayer.model.base.BaseMainModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsMainModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    AppBarLayout appbar;
    Toolbar toolbar;
    TabLayout tabs;

    ViewPager viewPager;
    MainPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("debug", "onCreate MainActivity");

        init();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 2000);
    }

    void init() {
        findView();
        initAppbar();

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    void findView() {
        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);

        viewPager = findViewById(R.id.view_pager);
    }

    void initAppbar() {
        setSupportActionBar(toolbar);
        tabs.setupWithViewPager(viewPager);
    }

    void loadData() {
        List<SongItem> songItems = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            SongItem songItem = new SongItem();
            songItem.setName("song " + i);
            songItems.add(songItem);
        }

        BaseMainModel<SongItem> songsMainModel = ViewModelProviders.of(this).get(SongsMainModel.class);
        songsMainModel.getItems().setValue(songItems);

        //
        List<AlbumItem> albumItems = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            AlbumItem albumItem = new AlbumItem();
            albumItem.setName("album " + i);
            albumItems.add(albumItem);
        }

        AlbumsMainModel albumsMainModel = ViewModelProviders.of(this).get(AlbumsMainModel.class);
        albumsMainModel.getItems().setValue(albumItems);

        //
        List<ArtistItem> artistItems = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            ArtistItem artistItem = new ArtistItem();
            artistItem.setName("artist " + i);
            artistItems.add(artistItem);
        }

        ArtistsMainModel artistsMainModel = ViewModelProviders.of(this).get(ArtistsMainModel.class);
        artistsMainModel.getItems().setValue(artistItems);
    }
}
