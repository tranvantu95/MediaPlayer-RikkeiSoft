package com.rikkeisoft.musicplayer.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.rikkeisoft.musicplayer.R;

public class AppbarActivity extends BaseActivity {

    protected AppBarLayout appbar;
    protected Toolbar toolbar;
    protected TabLayout tabs;
    protected ImageView ivAppbar;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        init();
//    }

    protected void init() {
        findView();
        setupActionBar();
    }

    protected void findView() {
        appbar = findViewById(R.id.appbar);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        ivAppbar = findViewById(R.id.app_bar_image);
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

    public void setTittle(int resStringId) {
        setTittle(getString(resStringId));
    }

    public void setTittle(String string){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(string);
        }
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
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
