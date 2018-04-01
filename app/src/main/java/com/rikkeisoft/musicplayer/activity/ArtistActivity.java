package com.rikkeisoft.musicplayer.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.MyActivity;
import com.rikkeisoft.musicplayer.custom.adapter.pager.ArtistPagerAdapter;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.ArtistsModel;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.AlbumItem;
import com.rikkeisoft.musicplayer.model.item.ArtistItem;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.utils.Loader;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.List;

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
    protected void onReceiverMediaChange() {
        super.onReceiverMediaChange();

        artist = null;
        loadData();
    }

    @Override
    protected void loadData() {
        int id = getIntent().getIntExtra(ID, -1);
        if(id == -1) {
            finish();
            return;
        }

        if(artist == null) artist = Loader.getInstance().findArtist(id);
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

    @Override
    protected void onChangeTypeView(int typeView) {
        super.onChangeTypeView(typeView);

        getModel(SongsModel.class).getTypeView().setValue(typeView);
        getModel(AlbumsModel.class).getTypeView().setValue(typeView);
    }

    @Override
    protected void onPlayerConnected(PlayerService playerService, PlaylistPlayer playlistPlayer, PlayerModel playerModel) {
        super.onPlayerConnected(playerService, playlistPlayer, playerModel);

        getModel(SongsModel.class).getPlaylistPlayer().setValue(playlistPlayer);
        getModel(SongsModel.class).getPayerModel().setValue(playerModel);
        getModel(AlbumsModel.class).getPayerModel().setValue(playerModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
