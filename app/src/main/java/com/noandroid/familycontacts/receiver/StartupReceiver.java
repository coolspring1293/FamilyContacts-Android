package com.noandroid.familycontacts.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.noandroid.familycontacts.service.PhoneMonitorSevice;
import com.noandroid.familycontacts.R;
import com.noandroid.familycontacts.service.WeatherService;

/**
 * Created by Hsiaotsefeng on 2016/4/7.
 */
public class StartupReceiver extends BroadcastReceiver {
    static final int ALARM_REFRESH_CITY_LIST = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(context.getString(R.string.app_name), "StartupReceiver received");
        Intent weatherIntent = new Intent(context, WeatherService.class);
        weatherIntent.putExtra(WeatherService.ACTION, WeatherService.REFRESH_REAL_WEATHER_LIST);

        context.startService(weatherIntent);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_REFRESH_CITY_LIST, weatherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                60 * 60 * 1000, pendingIntent);

        Intent phoneMonInt = new Intent(context, PhoneMonitorSevice.class);
        context.startService(phoneMonInt);



    }
}
