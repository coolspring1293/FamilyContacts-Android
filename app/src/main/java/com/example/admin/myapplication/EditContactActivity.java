package com.example.admin.myapplication;


import android.app.Activity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.myapplication.model.City;
import com.example.admin.myapplication.model.Contact;
import com.example.admin.myapplication.model.ContactDao;
import com.example.admin.myapplication.model.DaoMaster;
import com.example.admin.myapplication.model.DaoSession;
import com.example.admin.myapplication.model.DatabaseHelper;
import com.example.admin.myapplication.model.TelInitialDao;
import com.example.admin.myapplication.model.Telephone;
import com.google.android.gms.common.api.GoogleApiClient;

import junit.framework.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.graphics.BitmapFactory;


import com.example.admin.myapplication.model.City;
import com.example.admin.myapplication.model.Contact;
import com.example.admin.myapplication.model.ContactDao;
import com.example.admin.myapplication.model.DaoMaster;
import com.example.admin.myapplication.model.DaoSession;
import com.example.admin.myapplication.model.DatabaseHelper;
import com.example.admin.myapplication.model.TelInitialDao;
import com.google.android.gms.common.api.GoogleApiClient;
import com.example.admin.myapplication.model.Telephone;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class EditContactActivity extends Activity {


    private AutoCompleteTextView atct_name;
    private AutoCompleteTextView atct_telephone;
    private AutoCompleteTextView atct_avatar;
    private AutoCompleteTextView atct_relationship;


    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ContactDao contactDao;
    private TelInitialDao telInitialDao;


    private Long id = 0L;

    private Button button_ok;

    private String mName;
    private String mRelationship;
    private String mAvatar;
    private String mTel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        // TODO 正式版本不会有这个的，因为主键可以寻到avatar的地址，这个为了测试
        //atct_avatar = (AutoCompleteTextView) findViewById(R.id.edit_avatar);
        atct_name = (AutoCompleteTextView) findViewById(R.id.edit_name);
        atct_telephone = (AutoCompleteTextView) findViewById(R.id.edit_telephone);
        atct_relationship = (AutoCompleteTextView) findViewById(R.id.edit_relationship);


        atct_name.setText("");
        atct_telephone.setText("");
        atct_relationship.setText("");


        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        //updateListContent();

        button_ok = (Button) findViewById(R.id.edit_ok);
        // TODO Leasunhy
        // Long id = 0L; // ContactDao.getNextID();
        button_ok.setOnClickListener(new View.OnClickListener() {		//设置按钮单击事件
            @Override
            public void onClick(View v) {
                id = addContact(
                        atct_name.getText().toString(),
                        atct_relationship.getText().toString(),
                        atct_name.getText().toString()
                );
                // 回到上一个地方
                finish();
            }
        });


    }



    private void updateListContent() {
        Cursor cursor = db.query(contactDao.getTablename(), contactDao.getAllColumns(), null, null, null, null, null);
        String[] from = { ContactDao.Properties.Name.columnName, ContactDao.Properties.Relationship.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                from, to, 0);
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);

    }

    //City myCity = daoSession.getTelInitialDao().queryBuilder().where(
    //TelInitialDao.Properties.Initial.eq("1301996")).build().unique().getCity();
    /***
     *
     * @param contact
     * @return
     */
    // return id
    private void insertContect(Contact contact) {
        contactDao.insert(contact);
        Log.d("Model", "Inserted new Contact, ID: " + contact.getId());
        updateListContent();
    }

    /***
     * 单个添加电话号码
     * @param _id
     * @param tel_number
     * @return
     */
    //add telephone number to this contact
    private boolean addTeltoContact(String _id, String tel_number) {


        return true;
    }



    private Long addContact(String name, String relationship, String haveAva) {
        Contact contact = new Contact(null, name, relationship, haveAva);
        contactDao.insert(contact);

        Log.d("Model", "Inserted new Contact, ID: " + contact.getId());
        // updateListContent();
        return  contact.getId();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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