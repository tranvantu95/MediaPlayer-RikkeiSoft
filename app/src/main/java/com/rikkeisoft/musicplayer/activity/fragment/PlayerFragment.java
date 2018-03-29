package com.rikkeisoft.musicplayer.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.AppbarFragment;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.model.PlayerModel;

public class PlayerFragment extends AppbarFragment<PlayerModel> implements View.OnClickListener {

    private View btnPlay, btnNext, btnPrevious, btnShuffle, btnRepeat;
    private TextView tvTime;

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
    }

    @Override
    protected PlayerModel onCreateModel() {
        return getModel(getArguments().getInt("modelOwner"), PlayerModel.class);
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
    protected void initView(View view) {
        super.initView(view);
        view.setOnClickListener(this);
    }

    @Override
    protected void findView(View view) {
        super.findView(view);

        tvTime = view.findViewById(R.id.tv_time);

        view.findViewById(R.id.btn_play).setOnClickListener(this);
        view.findViewById(R.id.btn_next).setOnClickListener(this);
        view.findViewById(R.id.btn_previous).setOnClickListener(this);
        view.findViewById(R.id.btn_shuffle).setOnClickListener(this);
        view.findViewById(R.id.btn_repeat).setOnClickListener(this);
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
                break;

            case R.id.btn_next:
                break;

            case R.id.btn_previous:
                break;

            case R.id.btn_shuffle:
                break;

            case R.id.btn_repeat:
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
