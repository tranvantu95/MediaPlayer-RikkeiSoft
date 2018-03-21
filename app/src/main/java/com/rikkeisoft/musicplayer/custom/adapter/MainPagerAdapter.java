package com.rikkeisoft.musicplayer.custom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rikkeisoft.musicplayer.activity.fragment.SongsMainFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    SongsMainFragment songsMainFragment;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        songsMainFragment = SongsMainFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        return songsMainFragment;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
