package com.noandroid.familycontacts;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import com.noandroid.familycontacts.model.Blacklist;
import com.noandroid.familycontacts.model.BlacklistDao;
import com.noandroid.familycontacts.model.DaoMaster;
import com.noandroid.familycontacts.model.DaoSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

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
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new CallListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class CallListener extends PhoneStateListener {
        private final String token;
        private SQLiteDatabase db;
        private DaoMaster daoMaster;
        private DaoSession daoSession;
        private BlacklistDao blacklistDao;
        private Object telephonyService;
        private Method endCallMthd;
        private Query<Blacklist> queryBlk;

        public CallListener() {
            super();
            token = this.getClass().getName();
            // init db
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(PhoneMonitorSevice.this, "contacts-db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
            blacklistDao = daoSession.getBlacklistDao();
            queryBlk = blacklistDao.queryBuilder().where(BlacklistDao.Properties.PhoneNumber.eq(null)).build();
            // insert test data
            daoSession.insertOrReplace(new Blacklist(null, "110"));
            try {
                TelephonyManager tm = (TelephonyManager) PhoneMonitorSevice.this.getSystemService(Context.TELEPHONY_SERVICE);
                Class c = Class.forName(tm.getClass().getName());
                endCallMthd = c.getDeclaredMethod("getITelephony");
                endCallMthd.setAccessible(true);
                // Get the internal ITelephony object
                telephonyService = endCallMthd.invoke(tm);
                c = Class.forName(telephonyService.getClass().getName()); // Get its class
                endCallMthd = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
                endCallMthd.setAccessible(true); // Make it accessible
                endCallMthd.invoke(telephonyService); // invoke endCall()

            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException e
                    )

            {
                telephonyService = null;
                endCallMthd = null;
                e.printStackTrace();
            }
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(token, "CALL_STATE_RINGING");
                    queryBlk.setParameter(0, incomingNumber);
                    if (queryBlk.list().size() > 0 && endCallMthd != null) {
                        try {
                            endCallMthd.invoke(telephonyService);
                            Toast.makeText(PhoneMonitorSevice.this, String.format("Phone Call %s ended.", incomingNumber), Toast.LENGTH_LONG).show();
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(token, "CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(token, "CALL_STATE_OFFHOOK");
                    break;

            }
        }
    }
}
