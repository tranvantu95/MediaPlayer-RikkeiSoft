package com.rikkeisoft.musicplayer.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseActivity;
import com.rikkeisoft.musicplayer.custom.adapter.pager.MainPagerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.ArtistsModel;
import com.rikkeisoft.musicplayer.model.base.BaseListModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsModel;

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

        loadData();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadData();
//
//                new Handler().postDelayed(this, 3000);
//            }
//        }, 3000);
    }

    void init() {
        findView();

        setSupportActionBar(toolbar);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        setTitleTap(0, R.string.songs);
        setTitleTap(1, R.string.albums);
        setTitleTap(2, R.string.artists);
    }

    void findView() {
        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);

        viewPager = findViewById(R.id.view_pager);
    }

    void setTitleTap(int index, int resStringId) {
        TabLayout.Tab tab = tabs.getTabAt(index);
        if(tab != null) tab.setText(getString(resStringId));
    }

    void loadData() {
        List<SongItem> songItems = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            SongItem songItem = new SongItem();
            songItem.setName("song " + i);
            songItems.add(songItem);
        }

        BaseListModel<SongItem> songsModel = ViewModelProviders.of(this).get(SongsModel.class);
//        if(songsModel.getItems().getValue() != null) songItems.addAll(songsModel.getItems().getValue());
        songsModel.getItems().setValue(songItems);

        //
        List<AlbumItem> albumItems = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            AlbumItem albumItem = new AlbumItem();
            albumItem.setName("album " + i);
            albumItems.add(albumItem);
        }

        AlbumsModel albumsModel = ViewModelProviders.of(this).get(AlbumsModel.class);
        albumsModel.getItems().setValue(albumItems);

        //
        List<ArtistItem> artistItems = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            ArtistItem artistItem = new ArtistItem();
            artistItem.setName("artist " + i);
            artistItems.add(artistItem);
        }

        ArtistsModel artistsModel = ViewModelProviders.of(this).get(ArtistsModel.class);
        artistsModel.getItems().setValue(artistItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
