package com.rikkeisoft.musicplayer.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.activity.base.MyFragment;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.custom.adapter.SongsRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.BaseRecyclerAdapter;
import com.rikkeisoft.musicplayer.custom.adapter.base.SwitchRecyclerAdapter;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
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

    }

    @Override
    protected SwitchListModel<SongItem> onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), SongsModel.class);
    }

    @Override
    protected SwitchRecyclerAdapter<SongItem, ?> onCreateRecyclerAdapter() {
        return new SongsRecyclerAdapter(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                addFragment(position);
            }
        });
    }

    private void addFragment(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fragmentManager.findFragmentByTag("PlayerFragment");
        if(fragment == null) fragment = PlayerFragment.newInstance(BaseFragment.ACTIVITY_MODEL);

        if(!fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, fragment, "PlayerFragment")
                    .addToBackStack(null)
                    .commit();
        }

//        PlayerModel playerModel = getActivityModel(PlayerModel.class);
        PlayerModel playerModel = MyApplication.playerModel;
        playerModel.getItems().setValue(model.getItems().getValue());
        playerModel.getPlayingPosition().setValue(position);
    }

}
