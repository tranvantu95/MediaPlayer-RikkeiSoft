package com.rikkeisoft.musicplayer.activity.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.AppbarFragment;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.model.PlayerModel;
import com.rikkeisoft.musicplayer.model.PlaylistModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.item.SongItem;

import java.util.List;

public class PlayerFragment extends AppbarFragment<PlayerModel> implements View.OnClickListener {

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

        model.getItems().observe(this, new Observer<List<SongItem>>() {
            @Override
            public void onChanged(@Nullable List<SongItem> songItems) {
                getFragmentModel(PlaylistModel.class).getItems().setValue(songItems);
            }
        });

        addFragment();
    }

    @Override
    protected PlayerModel onCreateModel() {
        return model = getModel(getArguments().getInt("modelOwner"), PlayerModel.class);
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_player;
    }

    private void addFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        PlaylistFragment fragment = (PlaylistFragment) fragmentManager.findFragmentByTag("PlaylistFragment");
        if(fragment == null) fragment = PlaylistFragment.newInstance(BaseFragment.PARENT_FRAGMENT_MODEL);

        if(!fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, "PlaylistFragment")
                    .commit();
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        view.setOnClickListener(this);
    }

    @Override
    protected void findView(View view) {
        super.findView(view);
    }

    @Override
    protected void setupActionBar() {
        super.setupActionBar();
        showHomeButton();
    }

    @Override
    public void onClick(View view) {

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
