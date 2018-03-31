package com.rikkeisoft.musicplayer.activity;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.view.CircularSeekBar;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.utils.Format;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

public class PlayerActivity extends AppbarActivity implements View.OnClickListener {

    private View btnNext, btnPrevious;
    private TextView tvTime;
    private FloatingActionButton btnPlay;
    private ImageView btnShuffle, btnRepeat;
    private CircularSeekBar seekBar;
    private boolean userIsSeeking;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init();

        addFragment();
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

        playerModel.getCurrentIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null && playerModel.getItems().getValue() != null) {
                    setTittle(playerModel.getItems().getValue().get(integer).getName());
                }
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
                    playlistPlayer.seekTo(userSelectedPosition);
                }
            }
        });

    }

    @Override
    protected void findView() {
        super.findView();

        tvTime = findViewById(R.id.tv_time);
        seekBar = findViewById(R.id.seek_bar);
        btnPlay = findViewById(R.id.btn_play);
        btnShuffle = findViewById(R.id.btn_shuffle);
        btnRepeat = findViewById(R.id.btn_repeat);

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
                playlistPlayer.togglePlay();
                break;

            case R.id.btn_next:
                playlistPlayer.next();
                break;

            case R.id.btn_previous:
                playlistPlayer.previous();
                break;

            case R.id.btn_shuffle:
                playlistPlayer.toggleShuffle();
                break;

            case R.id.btn_repeat:
                playlistPlayer.toggleRepeat();
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


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

