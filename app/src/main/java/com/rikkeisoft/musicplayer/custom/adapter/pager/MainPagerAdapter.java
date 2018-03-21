package com.rikkeisoft.musicplayer.custom.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rikkeisoft.musicplayer.activity.fragment.AlbumsFragment;
import com.rikkeisoft.musicplayer.activity.fragment.ArtistsFragment;
import com.rikkeisoft.musicplayer.activity.fragment.SongsFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private SongsFragment songsFragment;
    private AlbumsFragment albumsFragment;
    private ArtistsFragment artistsFragment;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        songsFragment = SongsFragment.newInstance();
        albumsFragment = AlbumsFragment.newInstance();
        artistsFragment = ArtistsFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return songsFragment;

            case 1:
                return albumsFragment;

            case 2:
                return artistsFragment;

            default:
                return songsFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
