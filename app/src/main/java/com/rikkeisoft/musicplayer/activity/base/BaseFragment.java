package com.rikkeisoft.musicplayer.activity.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

public class BaseFragment<Model extends ViewModel> extends Fragment {

    public Model model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate " + getClass().getSimpleName());

    }

    // Model Owner
    public static final int ACTIVITY_MODEL = 1;
    public static final int PARENT_FRAGMENT_MODEL = 2;

    // Model
    protected <Model extends ViewModel> Model getFragmentModel(Class<Model> modelClass) {
        return ViewModelProviders.of(this).get(modelClass);
    }

    protected <Model extends ViewModel> Model getActivityModel(Class<Model> modelClass) {
        return ViewModelProviders.of(getActivity()).get(modelClass);
    }

    protected <Model extends ViewModel> Model getParentFragmentModel(Class<Model> modelClass) {
        return ViewModelProviders.of(getParentFragment()).get(modelClass);
    }

    protected <Model extends ViewModel> Model getModel(int owner, Class<Model> modelClass) {
        switch (owner) {
            case ACTIVITY_MODEL:
                return getActivityModel(modelClass);

            case PARENT_FRAGMENT_MODEL:
                return getParentFragmentModel(modelClass);

            default:
                return getFragmentModel(modelClass);
        }
    }
}
