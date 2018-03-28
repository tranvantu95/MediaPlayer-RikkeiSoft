package com.rikkeisoft.musicplayer.custom.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.activity.base.BaseFragment;
import com.rikkeisoft.musicplayer.activity.fragment.AlbumsFragment;
import com.rikkeisoft.musicplayer.activity.fragment.ArtistsFragment;
import com.rikkeisoft.musicplayer.activity.fragment.SongsFragment;
import com.rikkeisoft.musicplayer.utils.AppUtils;

public class ArtistPagerAdapter extends FragmentPagerAdapter {

    private Fragment songsFragment;
    private Fragment albumsFragment;

    public ArtistPagerAdapter(FragmentManager fm) {
        super(fm);

        songsFragment = AppUtils.getPagerFragment(fm, R.id.view_pager, 0);
        albumsFragment = AppUtils.getPagerFragment(fm, R.id.view_pager, 1);

        if(songsFragment == null) songsFragment = SongsFragment.newInstance(BaseFragment.ACTIVITY_MODEL);
        if(albumsFragment == null) albumsFragment = AlbumsFragment.newInstance(BaseFragment.ACTIVITY_MODEL);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return songsFragment;

            case 1:
                return albumsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
