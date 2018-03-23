package com.rikkeisoft.musicplayer.custom.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.fragment.AlbumsFragment;
import com.rikkeisoft.musicplayer.activity.fragment.ArtistsFragment;
import com.rikkeisoft.musicplayer.activity.fragment.SongsFragment;
import com.rikkeisoft.musicplayer.utils.AppUtils;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private Fragment songsFragment;
    private Fragment albumsFragment;
    private Fragment artistsFragment;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        songsFragment = AppUtils.getPagerFragment(fm, R.id.view_pager, 0);
        albumsFragment = AppUtils.getPagerFragment(fm, R.id.view_pager, 1);
        artistsFragment = AppUtils.getPagerFragment(fm, R.id.view_pager, 2);

        if(songsFragment == null) songsFragment = SongsFragment.newInstance();
        if(albumsFragment == null) albumsFragment = AlbumsFragment.newInstance();
        if(artistsFragment == null) artistsFragment = ArtistsFragment.newInstance();
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
