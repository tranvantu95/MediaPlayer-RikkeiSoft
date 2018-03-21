package com.rikkeisoft.musicplayer.custom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rikkeisoft.musicplayer.activity.base.BaseMainFragment;
import com.rikkeisoft.musicplayer.activity.fragment.AlbumsMainFragment;
import com.rikkeisoft.musicplayer.activity.fragment.ArtistsMainFragment;
import com.rikkeisoft.musicplayer.activity.fragment.SongsMainFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    SongsMainFragment songsMainFragment;
    AlbumsMainFragment albumsMainFragment;
    ArtistsMainFragment artistsMainFragment;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        songsMainFragment = SongsMainFragment.newInstance();
        albumsMainFragment = AlbumsMainFragment.newInstance();
        artistsMainFragment = ArtistsMainFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return songsMainFragment;

            case 1:
                return albumsMainFragment;

            case 2:
                return artistsMainFragment;

            default:
                return songsMainFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
