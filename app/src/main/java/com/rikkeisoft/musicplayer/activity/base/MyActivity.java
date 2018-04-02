package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.PlayerActivity;
import com.rikkeisoft.musicplayer.custom.view.BottomPlayerController;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.service.PlayerService;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.List;

public abstract class MyActivity extends SwitchListActivity {

    protected BottomPlayerController bottomPlayerController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();

        bottomPlayerController = new BottomPlayerController((ViewGroup) findViewById(R.id.root),
                new BottomPlayerController.Callback() {
                    @Override
                    public void onClick(View view) {
                        if(playlistPlayer != null && !playlistPlayer.getPlaylist().isEmpty())
                            startActivity(PlayerActivity.createIntent(getApplicationContext()));
                    }

                    @Override
                    public int calculate(CoordinatorLayout parent, View child, View dependency) {
                        return calculateForBottomPlayerController(parent, child, dependency);
                    }
                });
    }

    protected abstract int calculateForBottomPlayerController(CoordinatorLayout parent, View child, View dependency);

    @Override
    protected void onPlayerConnected(PlayerService playerService, PlaylistPlayer playlistPlayer, PlayerModel _playerModel) {
        super.onPlayerConnected(playerService, playlistPlayer, _playerModel);

        bottomPlayerController.setPlaylistPlayer(playlistPlayer);

        playerModel.getCurrentSong().observe(this, new Observer<SongItem>() {
            @Override
            public void onChanged(@Nullable SongItem songItem) {
                if(songItem != null) {
                    bottomPlayerController.getTvTitle().setText(songItem.getName());
                    bottomPlayerController.getTvInfo().setText(songItem.getArtistName());

                    if(songItem.getBitmap() != null)
                        bottomPlayerController.getIvCover().setImageBitmap(songItem.getBitmap());
                    else bottomPlayerController.getIvCover().setImageDrawable(
                            getResources().getDrawable(R.drawable.im_song));
                }
            }
        });

        playerModel.getDuration().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) bottomPlayerController.getSeekBar().setMax(integer);
            }
        });

        playerModel.getCurrentPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null && !bottomPlayerController.isUserIsSeeking())
                    bottomPlayerController.getSeekBar().setProgress(integer);
            }
        });

        playerModel.getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null)
                    bottomPlayerController.getBtnPlay().setImageDrawable(getResources()
                        .getDrawable(aBoolean ? R.drawable.ic_pause : R.drawable.ic_play));
            }
        });

    }

    @Override
    protected void onPermissionGranted() {
        super.onPermissionGranted();

        loadData();
    }

    protected abstract void loadData();

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
