package com.ccs.app.musicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.ccs.app.musicplayer.R;
import com.ccs.app.musicplayer.activity.base.MyActivity;
import com.ccs.app.musicplayer.custom.adapter.pager.MainPagerAdapter;
import com.ccs.app.musicplayer.model.AlbumsModel;
import com.ccs.app.musicplayer.model.ArtistsModel;
import com.ccs.app.musicplayer.model.PlayerModel;
import com.ccs.app.musicplayer.model.SongsModel;
import com.ccs.app.musicplayer.model.item.SongItem;
import com.ccs.app.musicplayer.utils.AppUtils;
import com.ccs.app.musicplayer.utils.Loader;
import com.ccs.app.musicplayer.player.PlaylistPlayer;

import java.util.List;

public class MainActivity extends MyActivity {

    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return AppUtils.makeMainIntent(intent);
    }

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

    private boolean onDataLoaded;

    @Override
    protected void beforeReloadData() {
        onDataLoaded = false;
    }

    @Override
    protected void onDataLoaded() {
        if(onDataLoaded) return;
        onDataLoaded = true;

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
    protected List<SongItem> getPlaylistDefault() {
        if(Loader.getInstance().isLoaded()) return Loader.getInstance().getSongs();
        return null;
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
