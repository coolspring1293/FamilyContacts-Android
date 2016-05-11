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

        return new TelephoneDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TelephoneDetailsViewHolder telephoneDetailsViewHolder,int i) {
        Telephone tel = mTel.get(i);

        telephoneDetailsViewHolder.tv.setText(tel.getNumber());
        telephoneDetailsViewHolder.b1.setText(Telephone.getLocationForTel(tel));
        String weather = Telephone.getWeatherInfoForTel(tel);
        if (weather != null) {
            telephoneDetailsViewHolder.img2.setImageResource(R.drawable.ic_wb_cloudy_24dp);
            telephoneDetailsViewHolder.b2.setText(weather);
        }
    }

    @Override public int getItemCount() {
        return numItems;
    }
}

