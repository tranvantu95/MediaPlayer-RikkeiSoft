package com.rikkeisoft.musicplayer.activity;

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

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.MyActivity;
import com.rikkeisoft.musicplayer.custom.adapter.pager.ArtistPagerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

public class ArtistActivity extends MyActivity {

    public static final String ID = "id";

    private ArtistItem artist;

    private ViewPager viewPager;
    private FragmentPagerAdapter pagerAdapter;

    public static Intent createIntent(Context context, int id) {
        Intent intent = new Intent(context, ArtistActivity.class);
        intent.putExtra(ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        init();
    }

    @Override
    protected void init() {
        super.init();

        pagerAdapter = new ArtistPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        setTitleTap(0, R.string.songs);
        setTitleTap(1, R.string.albums);
    }

    @Override
    protected void findView() {
        super.findView();

        viewPager = findViewById(R.id.view_pager);
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        showHomeButton();
    }

    @Override
    protected void beforeReloadData() {
        artist = null;
    }

    @Override
    protected void onDataLoaded() {
        int id = getIntent().getIntExtra(ID, -1);
        if(id == -1) {
            finish();
            return;
        }

        if(artist == null) {
            artist = Loader.getInstance().findArtist(id);

            if(artist == null) {
                finish();
                return;
            }

            setTitle(artist.getName());
            setCollapsingTitle(artist.getName());

            if(artist.getBitmap() != null) appbarImage.setImageBitmap(artist.getBitmap());

            //
            getModel(SongsModel.class).getItems().setValue(artist.getSongs());
            getModel(AlbumsModel.class).getItems().setValue(artist.getAlbums());
        }
    }

    @Override
    protected void onChangeTypeView(int typeView) {
        super.onChangeTypeView(typeView);

        getModel(SongsModel.class).getTypeView().setValue(typeView);
        getModel(AlbumsModel.class).getTypeView().setValue(typeView);
    }

    @Override
    protected void onPlayerModelCreated(@NonNull PlayerModel playerModel) {
        super.onPlayerModelCreated(playerModel);

        getModel(SongsModel.class).getPayerModel().setValue(playerModel);
        getModel(AlbumsModel.class).getPayerModel().setValue(playerModel);
    }

    @Override
    protected void onPlaylistPlayerCreated(@Nullable PlaylistPlayer playlistPlayer) {
        super.onPlaylistPlayerCreated(playlistPlayer);

        getModel(SongsModel.class).getPlaylistPlayer().setValue(playlistPlayer);
    }

    @Override
    protected int calculateForBottomPlayerController(CoordinatorLayout parent, View child, View dependency) {
        return -dependency.getTop() * child.getHeight()
                / (dependency.getHeight() - dependency.findViewById(R.id.toolbar).getHeight()
                                            - dependency.findViewById(R.id.tabs).getHeight());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
