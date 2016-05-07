package com.noandroid.familycontacts;


import android.app.Activity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.View;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListView;

import com.noandroid.familycontacts.model.City;
import com.noandroid.familycontacts.model.Contact;
import com.noandroid.familycontacts.model.ContactDao;
import com.noandroid.familycontacts.model.DaoMaster;
import com.noandroid.familycontacts.model.DaoSession;
import com.noandroid.familycontacts.model.DatabaseHelper;
import com.noandroid.familycontacts.model.TelInitial;
import com.noandroid.familycontacts.model.TelInitialDao;
import com.noandroid.familycontacts.model.Telephone;
import com.noandroid.familycontacts.model.TelephoneDao;


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
    private TelephoneDao telDao;


    private Long id = 0L;

    private Button button_ok;

    private String mName;
    private String mRelationship;
    private String mAvatar;
    private String mTel;

    public boolean haveId = false;

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
        telDao = daoSession.getTelephoneDao();
        telInitialDao = daoSession.getTelInitialDao();
        //updateListContent();

        button_ok = (Button) findViewById(R.id.edit_ok);

        // Long id = 0L; // ContactDao.getNextID();
        button_ok.setOnClickListener(new View.OnClickListener() {		//修改
            @Override
            public void onClick(View v) {
                if (haveId == false) {
                    Contact contact = new Contact(
                            null,
                            atct_name.getText().toString(),
                            atct_relationship.getText().toString(),
                            false
                    );
                    // TODO
                    // addTeltoContact(id, atct_telephone.getText().toString());
                    String telStr = atct_telephone.getText().toString();
                    contactDao.insert(contact);
                    Telephone tel = new Telephone(null, telStr,
                            telInitialDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(
                                    telStr.substring(0, 7))).build().unique().getTelinitCityId(),
                            contact.getId());
                    telDao.insert(tel);
                }
                else {
                    Contact contact = new Contact(
                            id,
                            atct_name.getText().toString(),
                            atct_relationship.getText().toString(),
                            false
                    );
                    updateContact(contact);
                    // TODO
                    // addTeltoContact(id, atct_telephone.getText().toString());
                }
                //updateContact(contact);
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
    private boolean addTeltoContact(Long _id, String tel_number) {
        City city = daoSession.getTelInitialDao().queryBuilder().where(
                TelInitialDao.Properties.Initial.eq(tel_number.substring(0, 6))).build().unique().getCity();
        Telephone t = new Telephone(null, tel_number, city.getId(), _id);


        return true;
    }


    private void updateContact(Contact c) {
        contactDao.insert(c);
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