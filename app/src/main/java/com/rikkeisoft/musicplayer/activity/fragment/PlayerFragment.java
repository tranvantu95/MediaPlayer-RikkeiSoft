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

public class PlayerFragment extends AppbarFragment<PlayerModel> implements View.OnClickListener {

    private MusicService musicService;
    private MusicPlayer musicPlayer;

    private View btnNext, btnPrevious;
    private TextView tvTime;
    private FloatingActionButton btnPlay;
    private ImageView btnShuffle, btnRepeat;
    private CircularSeekBar seekBar;

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

        musicService = MyApplication.musicService;
        musicPlayer = musicService.getMusicPlayer();

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
        model.getCurrentTime().removeObservers(this);
        model.getPaused().removeObservers(this);
        model.getRepeat().removeObservers(this);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        view.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);

        model.getDuration().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer == null) return;
                seekBar.setMax(integer);
            }
        });

        model.getCurrentTime().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer == null) return;
                seekBar.setProgress(integer);
            }
        });

        model.getPaused().observe(this, new Observer<Boolean>() {
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
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    musicPlayer.seekTo(progress);
                }

                tvTime.setText(MusicPlayer.formatTime(progress));

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

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
                musicPlayer.togglePause();
                break;

            case R.id.btn_next:
                musicPlayer.next();
                break;

            case R.id.btn_previous:
                musicPlayer.previous();
                break;

            case R.id.btn_shuffle:
                musicPlayer.toggleShuffle();
                break;

            case R.id.btn_repeat:
                musicPlayer.toggleRepeat();
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
