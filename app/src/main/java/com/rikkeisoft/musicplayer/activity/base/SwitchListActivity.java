package com.rikkeisoft.musicplayer.activity.base;

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
import com.rikkeisoft.musicplayer.model.base.SwitchListModel;
import com.rikkeisoft.musicplayer.utils.General;

public class SwitchListActivity extends AppbarActivity {

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

    protected void onChangeTypeView(int typeView) {
        Log.d("debug", "onChangeTypeView " + getClass().getSimpleName());

    }

    private void setTypeView(int typeView, boolean updateMenu) {
        if(this.typeView == typeView) return;
        General.typeView = this.typeView = typeView;

        //
        if(updateMenu && typeViewMenu != null) {
            clearChecked(typeViewMenu);
            setChecked(typeView);
        }

        //
        onChangeTypeView(typeView);
    }

    private void setChecked(MenuItem item, boolean isChecked) {
        if(item == null) return;

        SpannableString spannable = new SpannableString(item.getTitle());
        spannable.setSpan(new ForegroundColorSpan(isChecked ? Color.BLUE : Color.BLACK),
                0, spannable.length(), 0);
        item.setTitle(spannable);
        item.setChecked(isChecked);
    }

    private void clearChecked(Menu menu) {
        for(int i = menu.size() - 1; i >= 0; i--) {
            setChecked(menu.getItem(i), false);
        }
    }

    private void setChecked(int typeView) {
//        int id = getTypeViewMenuItemId(typeView); // id - typeView synchronized
        setChecked(typeViewMenu.findItem(typeView), true);
    }

    private int getTypeViewMenuItemId(int typeView) {
        switch (typeView) {
            case SwitchListModel.LIST:
                return R.id.type_list;

            case SwitchListModel.GRID:
                return R.id.type_grid;

            default:
                return R.id.type_list;
        }
    }

    private int getTypeView(int menuItemId) {
        switch (menuItemId) {
            case R.id.type_list:
                return SwitchListModel.LIST;

            case R.id.type_grid:
                return SwitchListModel.GRID;

            default:
                return SwitchListModel.LIST;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem typeViewMenuItem = menu.findItem(R.id.type_view);
        if(typeViewMenuItem != null) {
            typeViewMenu = typeViewMenuItem.getSubMenu();
            setChecked(typeView);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.type_list:
            case R.id.type_grid: {
                if(item.isChecked()) return true;
                clearChecked(typeViewMenu);
                setChecked(item, true);

//                int typeView = getTypeView(id); // id - typeView synchronized
                setTypeView(id, false);

                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
