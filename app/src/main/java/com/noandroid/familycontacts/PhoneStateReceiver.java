package com.noandroid.familycontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneStateReceiver extends BroadcastReceiver {
    public PhoneStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serv_int = new Intent(context, PhoneStateReceiver.class);
        context.startService(serv_int);

    }
}
