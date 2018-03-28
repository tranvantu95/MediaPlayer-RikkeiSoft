package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;
import com.rikkeisoft.musicplayer.model.SongsModel;

public class SongsFragment extends MyFragment<SongItem> {

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

        Bundle args = getArguments();

        model = getModel(args.getInt("modelOwner"), SongsModel.class);

        recyclerAdapter = new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                addFragment();

            }
        });

        init();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PlayerFragment playerFragment = (PlayerFragment) fragmentManager.findFragmentByTag("PlayerFragment");

        if(playerFragment == null) {
            playerFragment = PlayerFragment.newInstance(BaseFragment.ACTIVITY_MODEL);

            fragmentManager.beginTransaction()
                    .add(android.R.id.content, playerFragment, "PlayerFragment")
                    .addToBackStack(null)
                    .commit();

            getActivityModel(PlayerModel.class).getItems().setValue(model.getItems().getValue());
        }
    }

}
