package com.noandroid.familycontacts;

/**
 * Created by liuw53 on 5/10/16.
 */


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TabAdapter extends RecyclerView.Adapter<FakePageVH> {

    private final int numItems;

    public TabAdapter(int numItems) {
        this.numItems = numItems;
    }

    @Override public FakePageVH onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_card, viewGroup, false);

        return new FakePageVH(itemView);
    }

    @Override
    public void onBindViewHolder(FakePageVH fakePageVH, int i) {
        // do nothing
    }

    @Override public int getItemCount() {
        return numItems;
    }
}

