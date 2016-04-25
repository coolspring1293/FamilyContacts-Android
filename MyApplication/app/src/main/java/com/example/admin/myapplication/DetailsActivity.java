package com.example.admin.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.admin.myapplication.model.City;
import com.example.admin.myapplication.model.Contact;
import com.example.admin.myapplication.model.ContactDao;
import com.example.admin.myapplication.model.DaoMaster;
import com.example.admin.myapplication.model.DaoSession;
import com.example.admin.myapplication.model.DatabaseHelper;
import com.example.admin.myapplication.model.TelInitialDao;

/**
 * Created by liuw53 on 4/14/16.
 */
public class DetailsActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ContactDao contactDao;
    private String number = "18824110669";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = this.getIntent().getExtras();
        String contactName = bundle.getString("contactName");
        String contactId = bundle.getString("contactId");
        toolbar.setTitle(contactName);
        toolbar.setSubtitle(contactId);
        //toolbar.setLogo(R.drawable.allen_xie_icon);

        setSupportActionBar(toolbar);

        //TODO find list<tel> according to contacId ,  and display


        // (TODO):Liu Wang
        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        updateListContent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("tel", number);
                setResult(1,intent);
                finish();//结束当前的activity的生命周期
               /* City myCity = daoSession.getTelInitialDao().queryBuilder().where(
                        TelInitialDao.Properties.Initial.eq("1301996")).build().unique().getCity();
                String displayStr = String.format("1301996 is in %s, the weather code of which is %s.",
                        myCity.getCityname(), myCity.getWeatherCode());
                Snackbar.make(view, displayStr, Snackbar.LENGTH_LONG).setAction("Action", null).show();*/
            }
        });

        AppCompatImageButton btn = (AppCompatImageButton) findViewById(R.id.button_add);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*addContact();
                Snackbar.make(view, "Added contact", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                dialog();
            }
        });
    }

    protected void dialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("get pic from ");
        builder.setTitle("avatar");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* GetPicFromGallery();*/
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Camera",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* GetPicFromCamera();*/
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void updateListContent() {
        Cursor cursor = db.query(contactDao.getTablename(), contactDao.getAllColumns(), null, null, null, null, null);
        String[] from = { ContactDao.Properties.Name.columnName, ContactDao.Properties.Relationship.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                from, to, 0);
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);

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