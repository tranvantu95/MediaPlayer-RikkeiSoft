package com.ccs.app.musicplayer.custom.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ccs.app.musicplayer.R;
import com.ccs.app.musicplayer.activity.base.BaseFragment;
import com.ccs.app.musicplayer.activity.fragment.AlbumsFragment;
import com.ccs.app.musicplayer.activity.fragment.ArtistsFragment;
import com.ccs.app.musicplayer.activity.fragment.SongsFragment;
import com.ccs.app.musicplayer.utils.AppUtils;

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
