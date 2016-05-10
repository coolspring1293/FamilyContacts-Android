package com.noandroid.familycontacts;

/**
 * Created by liuw53 on 5/10/16.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.noandroid.familycontacts.model.Contact;
import com.noandroid.familycontacts.model.Telephone;

import org.w3c.dom.Text;

public class TelephoneDetailsViewHolder extends RecyclerView.ViewHolder {
    public TextView tv;
    public Button b1, b2;

    private Context context;
    public TelephoneDetailsViewHolder(View itemView, Telephone telephone) {
        super(itemView);
        tv = (TextView) itemView.findViewById(R.id.card_phone);
        b1 = (Button)   itemView.findViewById(R.id.card_button_1);
        b2 = (Button)   itemView.findViewById(R.id.card_button_2);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactDetailsActivity.context, "The location: " + b1.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactDetailsActivity.context, "The weather: " + b2.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }





}