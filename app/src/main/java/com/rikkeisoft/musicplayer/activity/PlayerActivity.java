package com.rikkeisoft.musicplayer.activity;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.AppbarActivity;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.activity.fragment.PlaylistFragment;
import com.rikkeisoft.musicplayer.activity.fragment.SortPlaylistFragment;
import com.rikkeisoft.musicplayer.custom.view.CircularSeekBar;
import com.rikkeisoft.musicplayer.custom.view.PlayerBehavior;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.PlaylistModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.Format;
import com.rikkeisoft.musicplayer.player.PlaylistPlayer;

import java.util.List;

public class PlayerActivity extends AppbarActivity implements View.OnClickListener {

    private View btnNext, btnPrevious;
    private TextView tvTime, tvTitle;
    private FloatingActionButton btnPlay;
    private ImageView btnShuffle, btnRepeat;
    private CircularSeekBar seekBar;
    private boolean userIsSeeking;

    private View fragmentContainer;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, PlayerActivity.class);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        addFragment();

        init();
    }

    @Override
    protected void onPlayerModelCreated(@NonNull PlayerModel _playerModel) {
        super.onPlayerModelCreated(_playerModel);

        getModel(PlaylistModel.class).getPayerModel().setValue(playerModel);

        playerModel.getCurrentSong().observe(this, new Observer<SongItem>() {
            @Override
            public void onChanged(@Nullable SongItem songItem) {
                if(songItem != null) setTitle(songItem.getName());
                else setTitle(null);
            }
        });

        playerModel.getPlaylistName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String string) {
                if(string != null) tvTitle.setText(string);
            }
        });

        playerModel.getDuration().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) seekBar.setMax(integer);
            }
        });

        playerModel.getCurrentPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null && !userIsSeeking) seekBar.setProgress(integer);
            }
        });

        playerModel.getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) btnPlay.setImageDrawable(getResources()
                        .getDrawable(aBoolean ? R.drawable.ic_pause : R.drawable.ic_play));
            }
        });

        playerModel.getShuffle().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) btnShuffle.setImageDrawable(getResources()
                        .getDrawable(aBoolean ? R.drawable.ic_shuffle : R.drawable.ic_shuffle_disabled));
            }
        });

        playerModel.getRepeat().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) {
                    int id;
                    switch (integer) {
                        case PlaylistPlayer.REPEAT_PLAYLIST:
                            id = R.drawable.ic_repeat;
                            break;

                        case PlaylistPlayer.REPEAT_SONG:
                            id = R.drawable.ic_repeat_once;
                            break;

                        default:
                            id = R.drawable.ic_repeat_off;
                    }

                    btnRepeat.setImageDrawable(getResources().getDrawable(id));
                }
            }
        });

        playerModel.getItems().observe(this, new Observer<List<SongItem>>() {
            @Override
            public void onChanged(@Nullable List<SongItem> songItems) {
                getModel(PlaylistModel.class).getItems().setValue(songItems);
                if(songItems == null || songItems.isEmpty()) finish();
            }
        });
    }

    @Override
    protected void onPlaylistPlayerCreated(@Nullable PlaylistPlayer playlistPlayer) {
        super.onPlaylistPlayerCreated(playlistPlayer);

        getModel(PlaylistModel.class).getPlaylistPlayer().setValue(playlistPlayer);
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlaylistFragment fragment = (PlaylistFragment) fragmentManager.findFragmentByTag("PlaylistFragment");
        if(fragment == null) fragment = PlaylistFragment.newInstance(BaseFragment.ACTIVITY_MODEL);

        if(!fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, "PlaylistFragment")
                    .commit();
        }
    }

    @Override
    protected void init() {
        super.init();

        btnPlay.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            private int userSelectedPosition;

            @Override
            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    userSelectedPosition = progress;
                }

                tvTime.setText(Format.formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                userIsSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                updateSeek();
            }

            @Override
            public void onOutsideTrackingTouch(CircularSeekBar seekBar) {
                updateSeek();
            }

            private void updateSeek() {
                if(userIsSeeking) {
                    userIsSeeking = false;
                    if(playlistPlayer != null) playlistPlayer.seekTo(userSelectedPosition);
                }
            }
        });

        CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) fragmentContainer.getLayoutParams()).getBehavior();

        if(behavior != null)
            ((PlayerBehavior) behavior).setCallback(new PlayerBehavior.Callback() {
                @Override
                public void onTopChange(int top) {
//                    Log.d("debug", "onTopChange " + top);
                    fragmentContainer.setPadding(0, 0, 0, top);
                }
            });
    }

    @Override
    protected void findView() {
        super.findView();

        tvTitle = findViewById(R.id.tv_title);
        tvTime = findViewById(R.id.tv_time);
        seekBar = findViewById(R.id.seek_bar);
        btnPlay = findViewById(R.id.btn_play);
        btnShuffle = findViewById(R.id.btn_shuffle);
        btnRepeat = findViewById(R.id.btn_repeat);

        fragmentContainer = findViewById(R.id.fragment_container);

        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_previous).setOnClickListener(this);
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        showHomeButton();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_play:
                if(playlistPlayer != null) playlistPlayer.togglePlay();
                break;

            case R.id.btn_next:
                if(playlistPlayer != null) playlistPlayer.next();
                break;

            case R.id.btn_previous:
                if(playlistPlayer != null) playlistPlayer.previous();
                break;

            case R.id.btn_shuffle:
                if(playlistPlayer != null) playlistPlayer.toggleShuffle();
                break;

            case R.id.btn_repeat:
                if(playlistPlayer != null) playlistPlayer.toggleRepeat();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_sort_playlist:
                FragmentManager fragmentManager = getSupportFragmentManager();
                SortPlaylistFragment fragment = (SortPlaylistFragment) fragmentManager.findFragmentByTag("SortPlaylistFragment");
                if(fragment == null) fragment = SortPlaylistFragment.newInstance(BaseFragment.ACTIVITY_MODEL);

                if(!fragment.isAdded()) {
                    fragmentManager.beginTransaction()
                            .add(android.R.id.content, fragment, "SortPlaylistFragment")
                            .addToBackStack(null)
                            .commit();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

