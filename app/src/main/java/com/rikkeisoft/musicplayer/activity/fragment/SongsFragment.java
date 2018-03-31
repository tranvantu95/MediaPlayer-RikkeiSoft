package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.rikkeisoft.musicplayer.activity.PlayerActivity;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.MyRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsModel;

public class SongsFragment extends MyFragment<SongItem, SongsModel, SongsRecyclerAdapter> {

    public static SongsFragment newInstance(int modelOwner) {
        SongsFragment fragment = new SongsFragment();

        Bundle args = new Bundle();
        args.putInt("modelOwner", modelOwner);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.getPlayerModel().getCurrentSongId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer != null) {
                    recyclerAdapter.setCurrentId(integer);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        MyApplication.getPlayerModel().getPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean != null) {
                    recyclerAdapter.setPlaying(aBoolean);
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected SongsModel onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), SongsModel.class);
    }

    @Override
    protected SongsRecyclerAdapter onCreateRecyclerAdapter() {
        return new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                MyApplication.getPlayerModel().getItems().setValue(model.getItems().getValue());
                MyApplication.getPlaylistPlayer().setPlaylist(model.getItems().getValue(), position, true);

                getActivity().startActivity(new Intent(getContext(), PlayerActivity.class));

//                addFragment();
            }
        });
    }

    private void addFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fragmentManager.findFragmentByTag("PlayerFragment");
        if(fragment == null) fragment = PlayerFragment.newInstance(BaseFragment.ACTIVITY_MODEL);

        if(!fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, fragment, "PlayerFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

}
