package com.noandroid.familycontacts;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.CallLog;
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
import com.noandroid.familycontacts.model.Record;
import com.noandroid.familycontacts.model.RecordDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.GregorianCalendar;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
/**
 * Created by Hsiao on 2016/4/15.
 */
public class PhoneMonitorSevice extends Service {
    public static final String ACTION_NEW_OUTGOING_CALL = "com.noandroid.familycontacts.ACTION_NEW_OUTGOING_CALL";
    public static final String EXTRA_PHONE_NUMBER = "com.noandroid.familycontacts.EXTRA_PHONE_NUMBER";
    private static final String TAG = "PhoneMonitorService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            Log.d(TAG, intent.getStringExtra(EXTRA_PHONE_NUMBER));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCraete");


        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new CallListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class CallListener extends PhoneStateListener {
        private final String token;
        private BlacklistDao blacklistDao;
        private Object telephonyService;
        private Method endCallMthd;
        private Query<Blacklist> queryBlk;

        private int lastState = TelephonyManager.CALL_STATE_IDLE;

        public CallListener() {
            super();
            token = this.getClass().getName();
            // init db
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(PhoneMonitorSevice.this, "contacts-db", null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            DaoSession daoSession = daoMaster.newSession();
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
                    Intent intent = new Intent(PhoneMonitorSevice.this, CallLogSyncService.class);
                    intent.setAction(CallLogSyncService.ACTION_SYNC);
                    PhoneMonitorSevice.this.startService(intent);
                    Log.d(token, "CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(token, "CALL_STATE_OFFHOOK");
                    break;

            }
        }
    }
}
