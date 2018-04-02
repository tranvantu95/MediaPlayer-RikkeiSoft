package com.rikkeisoft.musicplayer.custom.view;

import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

public class BottomPlayerController implements View.OnClickListener {

    private Callback callback;

    private TextView tvTitle, tvInfo;
    private ImageView ivCover, btnPlay;
    private SeekBar seekBar;
    private boolean userIsSeeking;

    private PlaylistPlayer playlistPlayer;

    public void setPlaylistPlayer(PlaylistPlayer playlistPlayer) {
        this.playlistPlayer = playlistPlayer;
    }

    public BottomPlayerController(ViewGroup parent, Callback _callback) {
        this.callback = _callback;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_bottom_player, parent, true);
        View playerController = view.findViewById(R.id.player_controller);
        if(parent instanceof CoordinatorLayout) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) playerController.getLayoutParams();
            params.setBehavior(new PlayerControllerBehavior(new PlayerControllerBehavior.Callback() {
                @Override
                public int calculate(CoordinatorLayout parent, View child, View dependency) {
                    return callback.calculate(parent, child, dependency);
                }
            }));
        }

        tvTitle = view.findViewById(R.id.tv_title);
        tvInfo = view.findViewById(R.id.tv_info);
        ivCover = view.findViewById(R.id.iv_cover);
        btnPlay = view.findViewById(R.id.btn_play);
        seekBar = view.findViewById(R.id.seek_bar);

        tvTitle.setText("");
        tvInfo.setText("");

        playerController.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int userSelectedPosition;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userIsSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeek();
            }

            private void updateSeek() {
                if(userIsSeeking) {
                    userIsSeeking = false;
                    if(playlistPlayer != null) playlistPlayer.seekTo(userSelectedPosition);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.player_controller:
                callback.onClick(view);
                break;

            case R.id.btn_play:
                if(playlistPlayer != null) playlistPlayer.togglePlay();
                break;

        }
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvInfo() {
        return tvInfo;
    }

    public ImageView getIvCover() {
        return ivCover;
    }

    public ImageView getBtnPlay() {
        return btnPlay;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public boolean isUserIsSeeking() {
        return userIsSeeking;
    }

    public interface Callback {
        void onClick(View view);
        int calculate(CoordinatorLayout parent, View child, View dependency);
    }
}
