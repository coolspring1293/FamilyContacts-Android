package com.noandroid.familycontacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanlu on 16/5/9.
 */
public class SMSProcessor {

    private List<String> weatherRain = new ArrayList<>();

    public SMSProcessor() {
        init();
    }

    public void init() {
        weatherRain.add("阵雨");
        weatherRain.add("雷阵雨");
        weatherRain.add("大雨");
        weatherRain.add("小到中雨");
        weatherRain.add("中到大雨");
        weatherRain.add("中雨");
        weatherRain.add("小雨");
        weatherRain.add("大到暴雨");
        weatherRain.add("暴雨");
    }

    public String messageTemplate(String relationship, String weather, String temprature) {
        String result = "";
        result += "我看你那里今天天气是";
        result += weather;
        result += "，";
        result += temprature;

        if (weatherRain.contains(weather)) {
            result += "，出门别忘了带伞。";
        }

        if (weather == "晴" && Integer.getInteger(temprature.substring(0, 1)) > 25) {
            result += "，温度有点高， 注意避暑和防晒。";
        }

        if (Integer.getInteger(temprature.substring(0, 2)) < 15) {
            result += "，气温有些低，注意身体，小心感冒。";
        }

        return result;

    }

    public void sendSMS(Context context, String phoneNumber, String relationship, String weather, String temprature) {
        String message = messageTemplate(relationship, weather, temprature);

        if (phoneNumber != null) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "no telephone! ", Toast.LENGTH_SHORT).show();
        }
    }


}
