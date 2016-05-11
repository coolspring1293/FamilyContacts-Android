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
        telephoneDetailsViewHolder.tv.setText(mTel.get(i).getNumber());
        telephoneDetailsViewHolder.b1.setText(mTel.get(i).getCityStr());


        if (mTel.get(i).getWeatherInfo() == null) {

        }
        else {
            telephoneDetailsViewHolder.img2.setImageResource(R.drawable.ic_wb_cloudy_24dp);
            String tmpW = mTel.get(i).getWeatherInfo().toString();
            if (mTel.get(i).getCity() != null) {
                tmpW += ("  " + mTel.get(i).getCity().getTemperature().toString() + "°C");
            }
            telephoneDetailsViewHolder.b2.setText(tmpW);
        }

    }

    @Override public int getItemCount() {
        return numItems;
    }
}

