package com.noandroid.familycontacts;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Hsiao on 2016/4/15.
 */
public class PhoneMonitorSevice extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("PhoneMonitorService", "onCraete");
        try
        {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            tm.listen(new CallListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
        catch (Exception e)
        {
            Log.d("BLOCK CALL", e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class CallListener extends PhoneStateListener {
        private final String token;
        private int lastState = Telephony
        public CallListener() {
            super();
            token = this.getClass().getName();
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d("PhoneMonitorService", state + incomingNumber);
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(token, "CALL_STATE_RINGING");
                        if (incomingNumber.equals("110")) {
                            Toast.makeText(PhoneMonitorSevice.this, "110 end", Toast.LENGTH_LONG).show();
                            TelephonyManager tm = (TelephonyManager) PhoneMonitorSevice.this
                                    .getSystemService(Context.TELEPHONY_SERVICE);
                            Class c = Class.forName(tm.getClass().getName());
                            Method endCallMthd = c.getDeclaredMethod("getITelephony");
                            endCallMthd.setAccessible(true);
                            Object telephonyService = endCallMthd.invoke(tm); // Get the internal ITelephony object
                            c = Class.forName(telephonyService.getClass().getName()); // Get its class
                            endCallMthd = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
                            endCallMthd.setAccessible(true); // Make it accessible
                            endCallMthd.invoke(telephonyService); // invoke endCall()
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(token, "CALL_STATE_IDLE");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(token, "CALL_STATE_OFFHOOK");
                        break;

                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException|InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
