package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rikkeisoft.musicplayer.R;
import com.rikkeisoft.musicplayer.app.MyApplication;
import com.rikkeisoft.musicplayer.model.AlbumsModel;
import com.rikkeisoft.musicplayer.model.ArtistsModel;
import com.rikkeisoft.musicplayer.model.SongsModel;
import com.rikkeisoft.musicplayer.model.base.ListGridModel;
import com.rikkeisoft.musicplayer.utils.General;

public class ListGridActivity extends AppbarActivity {

    private Menu typeViewMenu;

    private int typeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();

        setTypeView(General.typeView, true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getSharedPreferences(MyApplication.DATA, MODE_PRIVATE).edit();
        editor.putInt("typeView", typeView);
        editor.apply();
    }

    private void setTypeView(int typeView, boolean updateMenu) {
        if(this.typeView == typeView) return;
        Log.d("debug", "setTypeView " + getClass().getSimpleName());
        this.typeView = General.typeView = typeView;

        //
        if(updateMenu && typeViewMenu != null) {
            clearChecked(typeViewMenu);
            setChecked(typeView);
        }

        //
        SongsModel songsModel = ViewModelProviders.of(this).get(SongsModel.class);
        songsModel.getTypeView().setValue(typeView);

        //
        AlbumsModel albumsModel = ViewModelProviders.of(this).get(AlbumsModel.class);
        albumsModel.getTypeView().setValue(typeView);

        //
        ArtistsModel artistsModel = ViewModelProviders.of(this).get(ArtistsModel.class);
        artistsModel.getTypeView().setValue(typeView);
    }

    private void clearChecked(Menu menu) {
        for(int i = menu.size() - 1; i >= 0; i--) {
            setChecked(menu.getItem(i), false);
        }
    }

    private void setChecked(MenuItem item, boolean isChecked) {
        SpannableString spannable = new SpannableString(item.getTitle());
        spannable.setSpan(new ForegroundColorSpan(isChecked ? Color.BLUE : Color.BLACK),
                0, spannable.length(), 0);
        item.setTitle(spannable);
        item.setChecked(isChecked);
    }

    private void setChecked(int typeView) {
        int id = typeView == ListGridModel.LIST ? R.id.action_list : R.id.action_grid;
        setChecked(typeViewMenu.findItem(id), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        typeViewMenu = menu.findItem(R.id.action_change_type_view).getSubMenu();

        if(typeView != 0) setChecked(typeView);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_list:
            case R.id.action_grid: {
                if(item.isChecked()) return true;
                clearChecked(typeViewMenu);
                setChecked(item, true);

                int typeView = item.getItemId() == R.id.action_list
                        ? ListGridModel.LIST : ListGridModel.GRID;

                setTypeView(typeView, false);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
