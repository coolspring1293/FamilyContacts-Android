package com.noandroid.familycontacts;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Hsiaotsefeng on 2016/4/7.
 */
public class WeatherService extends IntentService {
    public static final String ACTION = "ACTION";
    public static final int REFRESH_STATION_LIST = 0;
    public static final int REFRESH_WEATHER_PREDICT = 1;
    final String appname = "FamilyContacts< Weather Service";
    public WeatherService() {
        super("WeatherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(appname, "Weather onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(appname, "Weather onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(appname, "Weather onDestory");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(appname, "Weather Service Intent");
        int action = workIntent.getIntExtra(ACTION, -1);
        switch (action) {
            case REFRESH_STATION_LIST:
                Log.d(appname, "REFRESH_STATION_LIST");
                break;
            case REFRESH_WEATHER_PREDICT:
                Log.d(appname, "RREFRESH_WEATHER_PREDICT");
                break;
        }
    }
}
