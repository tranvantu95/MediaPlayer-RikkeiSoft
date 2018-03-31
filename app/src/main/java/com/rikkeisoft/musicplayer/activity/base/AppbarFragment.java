package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.rikkeisoft.musicplayer.R;

public abstract class AppbarFragment<Model extends ViewModel> extends BaseFragment<Model> {

    protected AppBarLayout appbar;
    protected Toolbar toolbar;
    protected TabLayout tabs;
    protected ImageView appbarImage;
    protected CollapsingToolbarLayout collapsingToolbar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    protected void initView(View view) {
        findView(view);
        setupActionBar();
    }

    protected void findView(View view) {
        appbar = view.findViewById(R.id.appbar);
        toolbar = view.findViewById(R.id.toolbar);
        tabs = view.findViewById(R.id.tabs);
        appbarImage = view.findViewById(R.id.app_bar_image);
    }

    // ActionBar
    protected void setupActionBar() {
        toolbar.setContentInsetStartWithNavigation(0);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    protected void showHomeButton() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setTittle(int resStringId) {
        setTittle(getString(resStringId));
    }

    public void setTittle(String string) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(string);
        }
    }

    // Collapsing
    public void setCollapsingTitle(int resStringId) {
        setCollapsingTitle(getString(resStringId));
    }

    public void setCollapsingTitle(String string) {
        collapsingToolbar.setTitle(string);
    }

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

}
