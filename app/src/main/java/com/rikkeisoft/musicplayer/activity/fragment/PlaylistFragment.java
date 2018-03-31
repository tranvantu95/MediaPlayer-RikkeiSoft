package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseListFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.utils.PlaylistPlayer;

import java.util.List;

public class PlaylistFragment extends BaseListFragment<SongItem, PlayerModel,
        RecyclerView, LinearLayoutManager, SongsRecyclerAdapter> {

    private PlaylistPlayer playlistPlayer;

    private PlaylistPlayer.PlayCallback pauseOnIsPlaying = new PlaylistPlayer.PlayCallback() {
        @Override
        public void onIsPlaying(PlaylistPlayer playlistPlayer) {
            playlistPlayer.pause();
        }
    };

    public static PlaylistFragment newInstance(int modelOwner) {
        PlaylistFragment fragment = new PlaylistFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerAdapter.setTypeView(SwitchRecyclerAdapter.LIST_VIEW);

//        model.getCurrentIndex().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(@Nullable Integer integer) {
//                if(integer != null) gotoPos(integer);
//            }
//        });

        model.getCurrentSongId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) {
                    recyclerAdapter.setCurrentId(integer);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        model.getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) {
                    recyclerAdapter.setPlaying(aBoolean);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void gotoPos(Integer position) {
        Log.d("debug", "gotoPos " + position);
        Log.d("debug", "getItemCount " + recyclerAdapter.getItemCount());
        if(position != null && position > 0 && position < recyclerAdapter.getItemCount())
            if(recyclerView != null) recyclerView.smoothScrollToPosition(position);
    }

    @Override
    protected void updateRecyclerAdapter(List<SongItem> songItems) {
        super.updateRecyclerAdapter(songItems);

    }

    @Override
    protected void updateRecyclerView() {
        super.updateRecyclerView();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gotoPos(model.getCurrentIndex().getValue());
//            }
//        }, 100);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected PlayerModel onCreateModel() {
//        return getModel(getArguments().getInt("modelOwner"), PlayerModel.class);
        return MyApplication.getPlayerModel();
    }

    @Override
    protected SongsRecyclerAdapter onCreateRecyclerAdapter() {
        return new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                MyApplication.getPlaylistPlayer().play(position, pauseOnIsPlaying);
            }
        });
    }

    @Override
    protected LinearLayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected int onCreateDivider() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.divider_list);
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_list;
    }
}
