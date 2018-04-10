package com.rikkeisoft.musicplayer.activity.base;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.rikkeisoft.musicplayer.R;

public class AppbarActivity extends BaseActivity {

    protected AppBarLayout appbar;
    protected Toolbar toolbar;
    protected TabLayout tabs;
    protected ImageView appbarImage;
    protected CollapsingToolbarLayout collapsingToolbar;

    protected void init() {
        findView();
        setupActionBar();
    }

    protected void findView() {
        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        appbarImage = findViewById(R.id.app_bar_image);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
    }

    // ActionBar
    protected void setupActionBar() {
        toolbar.setContentInsetStartWithNavigation(0);
        setSupportActionBar(toolbar);
    }

    protected void showHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setTitle(int resStringId) {
        setTitle(getString(resStringId));
    }

    public void setTitle(String string) {
        if(collapsingToolbar != null && collapsingToolbar.isTitleEnabled()) {
            collapsingToolbar.setTitle(string);
            return;
        }

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(string);
            return;
        }

        if(toolbar != null) {
            toolbar.setTitle(string);
            return;
        }

        super.setTitle(string);
    }

    public String getTitle2() {
        if(collapsingToolbar != null && collapsingToolbar.isTitleEnabled()
                && collapsingToolbar.getTitle() != null)
            return collapsingToolbar.getTitle().toString();

        if(getSupportActionBar() != null && getSupportActionBar().getTitle() != null)
            return getSupportActionBar().getTitle().toString();

        if(toolbar != null && toolbar.getTitle() != null) return toolbar.getTitle().toString();

        return getTitle() != null ? getTitle().toString() : "";
    }

    // Collapsing
    public void setCollapsingTitleEnable(boolean enable) {
        collapsingToolbar.setTitleEnabled(enable);
    }

    // Tabs
    protected void setTitleTap(int index, int resStringId) {
        setTitleTap(index, getString(resStringId));
    }

    protected void setTitleTap(int index, String string) {
        TabLayout.Tab tab = tabs.getTabAt(index);
        if(tab != null) tab.setText(string);
    }

    // Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
