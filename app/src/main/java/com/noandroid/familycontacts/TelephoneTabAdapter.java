package com.noandroid.familycontacts;

/**
 * Created by liuw53 on 5/10/16.
 */


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.noandroid.familycontacts.model.Telephone;
import java.util.List;


public class TelephoneTabAdapter extends RecyclerView.Adapter<TelephoneDetailsViewHolder> {

    private int numItems = 0;

    List<Telephone> mTel;


    public TelephoneTabAdapter(List<Telephone> tel) {
        if (tel != null) {
            this.numItems = tel.size();
        }
        mTel = tel;
    }

    @Override
    public TelephoneDetailsViewHolder onCreateViewHolder(ViewGroup viewGroup,int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_card, viewGroup, false);

        return new TelephoneDetailsViewHolder(itemView, mTel.get(i));
    }

    @Override
    public void onBindViewHolder(TelephoneDetailsViewHolder telephoneDetailsViewHolder,int i) {
        telephoneDetailsViewHolder.tv.setText(mTel.get(i).getNumber());


        telephoneDetailsViewHolder.b1.setText(mTel.get(i).getCityStr());
        telephoneDetailsViewHolder.b2.setText(mTel.get(i).getWeatherInfo() + "  " + mTel.get(i).getCity().getTemperature() + "°C");
    }

    @Override public int getItemCount() {
        return numItems;
    }
}

