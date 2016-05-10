package com.noandroid.familycontacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuw53 on 5/10/16.
 */





public class TelephoneTabFragment extends Fragment {
    private RecyclerView mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RecyclerView) inflater.inflate(R.layout.fragment_page, container, false);
        return mRootView;
    }
    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
    }
    private void initRecyclerView() {
        mRootView.setAdapter(new TabAdapter(20));
    }

    public static Fragment newInstance() {
        return new TelephoneTabFragment();
    }

}
