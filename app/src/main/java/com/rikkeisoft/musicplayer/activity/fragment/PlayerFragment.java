package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.AppbarFragment;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.view.CircularSeekBar;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.service.MusicService;
import com.rikkeisoft.musicplayer.utils.MusicPlayer;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

public class PlayerFragment extends AppbarFragment<PlayerModel> implements View.OnClickListener {

    public PlaylistPlayer playlistPlayer;

    private View btnNext, btnPrevious;
    private TextView tvTime;
    private FloatingActionButton btnPlay;
    private ImageView btnShuffle, btnRepeat;
    private CircularSeekBar seekBar;
    private boolean userIsSeeking;

    public static PlayerFragment newInstance(int modelOwner) {
        PlayerFragment fragment = new PlayerFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        addFragment();

        playlistPlayer = new PlaylistPlayer(MyApplication.playerModel);
        playlistPlayer.setWakeMode(getContext());
        playlistPlayer.observe(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        playlistPlayer.release();
        playlistPlayer = null;
    }

    @Override
    protected PlayerModel onCreateModel() {
//        return getModel(getArguments().getInt("modelOwner"), PlayerModel.class);
        return MyApplication.playerModel;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_player;
    }

    private void addFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        PlaylistFragment fragment = (PlaylistFragment) fragmentManager.findFragmentByTag("PlaylistFragment");
        if(fragment == null) fragment = PlaylistFragment.newInstance(BaseFragment.ACTIVITY_MODEL);

        if(!fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, "PlaylistFragment")
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        model.getDuration().removeObservers(this);
        model.getCurrentPosition().removeObservers(this);
        model.getPlaying().removeObservers(this);
        model.getRepeat().removeObservers(this);
        model.getCurrentIndex().removeObservers(this);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        view.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);

        model.getCurrentIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null && model.getItems().getValue() != null) {
                    setTittle(model.getItems().getValue().get(integer).getName());

                    if(model.isFirstPlay()) playlistPlayer.play(integer);
                }
            }
        });

        model.getDuration().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) seekBar.setMax(integer);
            }
        });

        model.getCurrentPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null && !userIsSeeking) seekBar.setProgress(integer);
            }
        });

        model.getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) btnPlay.setImageDrawable(getContext().getResources()
                        .getDrawable(aBoolean ? R.drawable.ic_pause : R.drawable.ic_play));
            }
        });

        model.getRepeat().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) {
                    int id;
                    switch (integer) {
                        case MusicPlayer.REPEAT_PLAYLIST:
                            id = R.drawable.ic_repeat;
                            break;

                        case MusicPlayer.REPEAT_SONG:
                            id = R.drawable.ic_repeat_once;
                            break;

                        default:
                            id = R.drawable.ic_repeat_off;
                    }

                    btnRepeat.setImageDrawable(getContext().getResources().getDrawable(id));
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

                tvTime.setText(MusicPlayer.formatTime(progress));
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
    protected void findView(View view) {
        super.findView(view);

        tvTime = view.findViewById(R.id.tv_time);
        seekBar = view.findViewById(R.id.seek_bar);
        btnPlay = view.findViewById(R.id.btn_play);
        btnShuffle = view.findViewById(R.id.btn_shuffle);
        btnRepeat = view.findViewById(R.id.btn_repeat);

        view.findViewById(R.id.btn_next).setOnClickListener(this);
        view.findViewById(R.id.btn_previous).setOnClickListener(this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_player, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
