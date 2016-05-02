package com.noandroid.familycontacts;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;

import com.noandroid.familycontacts.model.DaoMaster;
import com.noandroid.familycontacts.model.DaoSession;
import com.noandroid.familycontacts.model.Record;
import com.noandroid.familycontacts.model.RecordDao;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by Hsiaotsefeng on 2016/5/2.
 */
public class CallLogSyncService extends IntentService {
    private RecordDao recordDao;
    private ContentResolver resolver;
    private static final String proj[] = new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};
//    private static final String ACTION_SYNC_PREV = "com.noandroid.familycontacts.CallLogSyncService.ACTION_SYNC_PREV";
//    private static final String ACTION_SYNC_NEXT = "com.noandroid.familycontacts.CallLogSyncService.ACTION_SYNC_PREV";
    public static final String ACTION_SYNC = "com.noandroid.familycontacts.CallLogSyncService.ACTION_SYNC_PREV";
//    private static final String EXTRA_TIMESTAMP = "EXTRA_TIMESTMAP";
    private Query lastRecQury;

    public CallLogSyncService() {
        super("CallLogSyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "contacts-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        recordDao = daoSession.getRecordDao();
        resolver = getContentResolver();
        lastRecQury = recordDao.queryBuilder().orderDesc(RecordDao.Properties.Time).limit(1).build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor cursor;
        List lastRecs = lastRecQury.forCurrentThread().list();
        Long lastDate;
        if (lastRecs.size() == 0) {
            lastDate = 0L;
        } else {
            Record lastRec = (Record) lastRecs.get(0);
            lastDate = lastRec.getTime().getTime();
        }
        cursor = resolver.query(CallLog.Calls.CONTENT_URI, proj, "date>?", new String[]{String.valueOf(lastDate)}, "date ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                long dateInt = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
                Date date = new Date(dateInt);
                recordDao.insert(new Record(null, date, type, number, (int)duration));
            }
        }
    }
}
