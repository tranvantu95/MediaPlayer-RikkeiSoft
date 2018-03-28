package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;

public class BaseFragment<Model extends ViewModel> extends Fragment {

    protected Model model;

    // Model Owner
    public static final int ACTIVITY_MODEL = 1;
    public static final int FRAGMENT_MODEL = 2;

    // Model
    protected  <Model extends ViewModel> Model getThisModel(Class<Model> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected  <Model extends ViewModel> Model getActivityModel(Class<Model> modelClass) {
        return ViewModelProviders.of(getActivity()).get(modelClass);
    }

    protected  <Model extends ViewModel> Model getFragmentModel(Class<Model> modelClass) {
        return ViewModelProviders.of(getParentFragment()).get(modelClass);
    }

    protected <Model extends ViewModel> Model getModel(int owner, Class<Model> modelClass) {
        switch (owner) {
            case ACTIVITY_MODEL:
                return getActivityModel(modelClass);

            case FRAGMENT_MODEL:
                return getFragmentModel(modelClass);

            default:
                return getThisModel(modelClass);
        }
    }
}
