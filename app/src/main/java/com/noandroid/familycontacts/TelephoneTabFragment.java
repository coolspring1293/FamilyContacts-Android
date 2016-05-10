package com.noandroid.familycontacts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noandroid.familycontacts.model.Telephone;

import java.util.List;

/**
 * Created by liuw53 on 5/10/16.
 */

public class TelephoneTabFragment extends Fragment {
    private RecyclerView mRootView;
    private List<Telephone> mTel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RecyclerView) inflater.inflate(R.layout.fragment_tel_details, container, false);


        return mRootView;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
    }

    public void setTelList(List<Telephone> l) {
            this.mTel = l;
    }

    private void initRecyclerView() {
        mRootView.setAdapter(new TelephoneTabAdapter(this.mTel));
    }

    public static Fragment newInstance(List<Telephone> lt) {
        TelephoneTabFragment ttf =  new TelephoneTabFragment();
        ttf.setTelList(lt);
        return ttf;
    }

}
