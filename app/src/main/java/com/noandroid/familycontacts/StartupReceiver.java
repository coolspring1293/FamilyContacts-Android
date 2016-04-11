package com.noandroid.familycontacts;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Hsiaotsefeng on 2016/4/7.
 */
public class StartupReceiver extends BroadcastReceiver {
    static final int ALARM_REFRESH_STATION = 0;
    static final int ALARM_REFRESH_CITY_LIST = 1;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(context.getString(R.string.app_name), "StartupReceiver received");
        Intent serviceIntent = new Intent(context, WeatherService.class);
        serviceIntent.putExtra(WeatherService.ACTION, WeatherService.REFRESH_STATION_LIST);
//        context.startService(serviceIntent);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_REFRESH_STATION, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                10 * 1000, pendingIntent);

    }
}
