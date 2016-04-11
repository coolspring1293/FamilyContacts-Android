package com.noandroid.familycontacts;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Hsiaotsefeng on 2016/4/7.
 */
public class WeatherService extends IntentService {
    public static final String ACTION = "ACTION";
    public static final int REFRESH_STATION_LIST = 0;
    public static final int REFRESH_WEATHER_PREDICT = 1;
    private static final String stationUrl;
    private static final String realtimeWeatherUrl;
    private static final String predictWeatherUrl;
    final String appname = "Weather Service";
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
        if

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
    private static String getContent(String URLStr, int trylimits) {
        int tryCount = 0;
        String result = null;
        HttpURLConnection connection = null;
        URL url;
        InputStreamReader unicodeIn = null;
        while (tryCount < trylimits) {
            try {
                url = new URL(URLStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                unicodeIn = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(unicodeIn);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                result = stringBuilder.toString();
            } catch (IOException e) {
                tryCount++;
                continue;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (unicodeIn != null) {
                    try {
                        unicodeIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return null;
    }

}
