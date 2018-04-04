package com.rikkeisoft.musicplayer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.MyActivity;
import com.rikkeisoft.musicplayer.custom.adapter.pager.MainPagerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.ArtistsModel;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

public class MainActivity extends MyActivity {

    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    protected void beforeReloadData() {}

    @Override
    protected void onDataLoaded() {
        getModel(SongsModel.class).getItems().setValue(Loader.getInstance().getSongs());
        getModel(AlbumsModel.class).getItems().setValue(Loader.getInstance().getAlbums());
        getModel(ArtistsModel.class).getItems().setValue(Loader.getInstance().getArtists());
    }

    @Override
    protected void onChangeTypeView(int typeView) {
        super.onChangeTypeView(typeView);

        getModel(SongsModel.class).getTypeView().setValue(typeView);
        getModel(AlbumsModel.class).getTypeView().setValue(typeView);
        getModel(ArtistsModel.class).getTypeView().setValue(typeView);
    }

    @Override
    protected void onPlayerModelCreated(@NonNull PlayerModel playerModel) {
        super.onPlayerModelCreated(playerModel);

        getModel(SongsModel.class).getPayerModel().setValue(playerModel);
        getModel(AlbumsModel.class).getPayerModel().setValue(playerModel);
        getModel(ArtistsModel.class).getPayerModel().setValue(playerModel);
    }

    @Override
    protected void onPlaylistPlayerCreated(@Nullable PlaylistPlayer playlistPlayer) {
        super.onPlaylistPlayerCreated(playlistPlayer);

        getModel(SongsModel.class).getPlaylistPlayer().setValue(playlistPlayer);
    }

    @Override
    protected int calculateForBottomPlayerController(CoordinatorLayout parent, View child, View dependency) {
        return -dependency.getTop() * child.getHeight() / dependency.findViewById(R.id.toolbar).getHeight();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
