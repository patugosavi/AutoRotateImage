package com.example.autorotateimage.baseclass;

import android.content.Context;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        injectDependencies();

        // can be used for general purpose in all Fragments of Application
    }



    protected abstract void injectDependencies();

}
