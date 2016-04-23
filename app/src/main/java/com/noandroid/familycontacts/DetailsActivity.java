package com.noandroid.familycontacts;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.noandroid.familycontacts.model.City;
import com.noandroid.familycontacts.model.Contact;
import com.noandroid.familycontacts.model.ContactDao;
import com.noandroid.familycontacts.model.DaoMaster;
import com.noandroid.familycontacts.model.DaoSession;
import com.noandroid.familycontacts.model.DatabaseHelper;
import com.noandroid.familycontacts.model.TelInitialDao;
import android.support.v7.widget.AppCompatImageButton;

/**
 * Created by liuw53 on 4/14/16.
 */
public class DetailsActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ContactDao contactDao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setTitle("Allen Xie");
        //toolbar.setSubtitle("18819461662");
        //toolbar.setLogo(R.drawable.allen_xie_icon);

        setSupportActionBar(toolbar);



        // (TODO):Liu Wang
        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        updateListContent();

        Contact c = getContactbyId("1");

        toolbar.setTitle(c.getName());

        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                City myCity = daoSession.getTelInitialDao().queryBuilder().where(
                        TelInitialDao.Properties.Initial.eq("1301996")).build().unique().getCity();
                String displayStr = String.format("1301996 is in %s, the weather code of which is %s.",
                        myCity.getCityname(), myCity.getWeatherCode());
                Snackbar.make(view, displayStr, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        AppCompatImageButton btn = (AppCompatImageButton) findViewById(R.id.button_add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
                Snackbar.make(view, "Added contact", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void updateListContent() {
        Cursor cursor = db.query(contactDao.getTablename(), contactDao.getAllColumns(), null, null, null, null, null);
        String[] from = {
                ContactDao.Properties.Name.columnName,
                ContactDao.Properties.Id.columnName
        };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                from, to, 0);
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);

    }

    Contact getContactbyId(String contact_id) {
        String selection = "_id=?" ;
        int offset = 0;
        String[] selectionArgs = new String[]{ "1" };
        Long tmp_id = Long.valueOf(1);
        Cursor cursor = db.query(contactDao.getTablename(), contactDao.getAllColumns(), selection,
                selectionArgs, null, null, null);

        // TODO(Liu Wang) Pseudo Method
        Contact contact =  new Contact(tmp_id, "Liu Siyuan", "Father", "1.png", "liu siyuan");

        // TODO the real
        // Contact contact =  contactDao.readEntity(cursor, 0);
        /* {
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // name
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // relationship
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // avatar
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // pinyin
        );*/
        return contact;

    }

    private void addContact() {
        Contact contact = new Contact(null, "Wang Liu", "Grandpa", "");
        contactDao.insert(contact);
        Log.d("Model", "Inserted new Contact, ID: " + contact.getId());
        updateListContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}