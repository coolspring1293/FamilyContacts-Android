package com.noandroid.familycontacts.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by leasunhy on 4/13/16.
 */
public class DatabaseHelper {
    public static DaoMaster.DevOpenHelper getDB(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean firstTime = prefs.getBoolean("FIRST_TIME", true);
        if (firstTime) {
            // copy default database
            if (copyDatabase(context))
                prefs.edit().putBoolean("FIRST_TIME", false);
        }
        return new DaoMaster.DevOpenHelper(context, "contacts-db", null);
    }

    private static boolean copyDatabase(Context context) {
        try {
            InputStream in = context.getAssets().open("contacts-db");
            Log.d("Model", "opened in");
            // first create the database file
            SQLiteDatabase db = context.openOrCreateDatabase("contacts-db", 0, null);
            db.close();
            // then do the copy
            OutputStream out = new FileOutputStream(context.getDatabasePath("contacts-db"));
            Log.d("Model", "opened out");
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            Log.e("Model", e.getMessage());
        }
        return false;
    }
}
