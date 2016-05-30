package com.noandroid.familycontacts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.noandroid.familycontacts.model.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.AlertDialog;


import a_vcard.android.provider.Contacts;



public class MainActivity extends FragmentActivity
        implements GestureDetector.OnGestureListener, FragmentIndicator.OnIndicateListener,
        NavigationView.OnNavigationItemSelectedListener {
    public static Fragment[] mFragments;
    FragmentIndicator mIndicator;
    public static SQLiteDatabase db;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static ContactDao contactDao;
    public static RecordDao recordDao;
    public static TelephoneDao telDao;
    public static CityDao cityDao;

    /*手势识别*/
    public static GestureDetector detector;
    /*滑动距离*/
    final int DISTANT=50;
    public int mark = 0;

    private final int REQUESTCODE = 1;
    //login
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setTintColor(Color.parseColor("#9900FF"));
        tintManager.setNavigationBarTintEnabled(true);


        detector=new GestureDetector(this);

        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        recordDao = daoSession.getRecordDao();
        telDao = daoSession.getTelephoneDao();
        cityDao = daoSession.getCityDao();
        setFragmentIndicator(0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //login
        sharedPreferencesInit();
    }


    private void setFragmentIndicator(int whichIsDefault) {
        mark = whichIsDefault;
        mFragments = new Fragment[3];
        mFragments[0] = getSupportFragmentManager().findFragmentById(R.id.fragment_contacts);
        mFragments[1] = getSupportFragmentManager().findFragmentById(R.id.fragment_records);
        mFragments[2] = getSupportFragmentManager().findFragmentById(R.id.fragment_dialing);

        switchFragment(whichIsDefault, false);

        mIndicator=(FragmentIndicator) findViewById(R.id.indicator);
        mIndicator.setOnIndicateListener(this);
    }

    // FragmentIndicator.OnIndicateListener
    @Override
    public void OnIndicate(View v, int which) {
        switchFragment(which, false);
    }

    private void switchFragment(int which, boolean animate) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (animate) {
            if (which > mark)
                ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            else
                ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
        }
        for (Fragment mFragment : mFragments) ft.hide(mFragment);
        ft.show(mFragments[which]);
        ft.commit();
        if (mFragments[which] instanceof CallLogFragment)
            mFragments[which].onResume();
        FragmentIndicator.setIndicator(which);
        mark = which;
    }



    // GestureDetector.OnGestureListener
    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }
    @Override
    public void onShowPress(MotionEvent arg0) {}
    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }
    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,float arg3) {
        return false;
    }
    @Override
    public void onLongPress(MotionEvent arg0) {}
    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {

        Toast.makeText(getBaseContext(),"----onFling---"+arg1.getX()+">" +arg0.getX() +" + "+DISTANT,Toast.LENGTH_SHORT).show();

        if(arg1.getX() > arg0.getX() + DISTANT) {
            switchFragment((mark - 1) % mFragments.length, true);
        } else if(arg1.getX() < arg0.getX() + DISTANT) {
            switchFragment((mark + 1) % mFragments.length, true);
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(getBaseContext(),"----onFling---",Toast.LENGTH_SHORT).show();
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // NavigationView.OnNavigationItemSelectedListener
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {

            Toast.makeText(this,"nav_login",Toast.LENGTH_SHORT).show();

            showLoginDialog(this);
        } else if (id == R.id.nav_share) {

            try {
                VCardInfo.ContactHandler handler= VCardInfo.ContactHandler.getInstance();
                // 获取要恢复的联系人信息
                List<VCardInfo> infoList = handler.restoreContacts();
                for (VCardInfo vcardInfo : infoList) {
                    // 恢复联系人,并查重
                    List list_contact = contactDao.queryBuilder().where(ContactDao.Properties.Name.eq(vcardInfo.getName())).list();
                    if (list_contact.isEmpty()) {
                        Contact contact = new Contact(null, vcardInfo.getName(), null, false);
                        contactDao.insert(contact);
                    }
                    Contact contact = contactDao.queryBuilder().where(ContactDao.Properties.Name.eq(vcardInfo.getName())).list().get(0);
                    for (VCardInfo.PhoneInfo phoneInfo : vcardInfo.getPhoneList()) {
                        List list_tel = telDao.queryBuilder().where(TelephoneDao.Properties.Number.eq(phoneInfo.number)).list();
                        if (list_tel.isEmpty()) {
                            String number = phoneInfo.number.replaceAll("[ -]", "");
                            Long cityId = null;
                            try {
                                cityId = Telephone.getCityIdForTel(number);
                            } catch (Exception e) {}
                            Telephone telephone = new Telephone(null, number, cityId, null);
                            telephone.setContact(contact);
                            telDao.insert(telephone);
                        }
                    }
                }

                Toast.makeText(this, "Import contacts.vcf Successfully!", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(this, "Import contacts.vcf Failed!\n" +
                        "Please put the contacts.vcf in /com.noandroid.familycontacts/",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
//            Toast.makeText(this,"nav_send",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            new AlertDialog.Builder(this)
                    .setTitle("OK")
                    .setMessage("The backup file will be covered. Continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VCardInfo.ContactHandler handler = VCardInfo.ContactHandler.getInstance();
                            List<Contact> _dbList = contactDao.loadAll();
                            List<VCardInfo> _infoList = new ArrayList<>();
                            for (Contact c : _dbList) {
                                String displayName = c.getName();
                                VCardInfo info = new VCardInfo(displayName);// 初始化联系人信息
                                List<Telephone> _telList = c.getTelephones();
                                if (!_telList.isEmpty()) {
                                    List<VCardInfo.PhoneInfo> phoneNumberList = new ArrayList<>();
                                    for (Telephone tel : _telList) {
                                        // 电话号码
                                        String phoneNumber = tel.getNumber();
                                        // 对应的联系人类型
                                        int type = Contacts.Phones.TYPE_MOBILE;

                                        // 初始化联系人电话信息
                                        VCardInfo.PhoneInfo phoneInfo = new VCardInfo.PhoneInfo();
                                        phoneInfo.type = type;
                                        phoneInfo.number = phoneNumber;

                                        phoneNumberList.add(phoneInfo);
                                    }
                                    // 设置联系人电话信息
                                    info.setPhoneList(phoneNumberList);
                                }
                                _infoList.add(info);
                            }
                            handler.backupContacts(MainActivity.this, _infoList);    // 备份联系人信息
                        }
                    }).setNegativeButton("Cancel", null).create().show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void sharedPreferencesInit () {
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "guanlu");
        editor.putString("password", "guanlu");
        editor.commit();
    }
    public void showLoginDialog(Context context) {

        final LoginDialog loginDialog = new LoginDialog(context,"login");
        loginDialog.init(1);
        loginDialog.show();
        loginDialog.setClickListener(new LoginDialog.ClickListenerInterface(){

            @Override
            public void doLogin() {
                loginDialog.dismiss();
                sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "NULL");
                String password = sharedPreferences.getString("password","NULL");

                if(username.equals(loginDialog.getUserName()) && password.equals(loginDialog.getPassword())) {
                    Toast.makeText(getBaseContext(),"success",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),"Wrong userName or Password",Toast.LENGTH_SHORT).show();
                    loginDialog.init(1);
                }
            }
            @Override
            public void doCancel() {
                loginDialog.dismiss();
            }

            @Override
            public void doRegister() {
                String name = loginDialog.getRigisterUserName();
                String password = loginDialog.getRegisterPassword();
                sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(name==null || password==null) {
                    Toast.makeText(getBaseContext(),"Error! UserName or Password is valid!",Toast.LENGTH_SHORT).show();
                } else {

                    editor.putString("username", name);
                    editor.putString("password", password);
                    editor.commit();
                    Toast.makeText(getBaseContext(), "Rigister Success!\n "+name.toString()+sharedPreferences.getString("name","").toString()
                            +password.toString()+sharedPreferences.getString("password","").toString(), Toast.LENGTH_SHORT).show();
                    loginDialog.init(1);
                }
            }
            @Override
            public void doTurn() {
                loginDialog.init(2);
            }

        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        /*oast.makeText(this,"onActivityResult " + requestCode ,Toast.LENGTH_SHORT).show();

           String tel = data.getStringExtra("tel");//接收返回数据
            getSupportFragmentManager().beginTransaction().hide(mFragments[0])
                        .hide(mFragments[1]).hide(mFragments[2])
                        .show(mFragments[2]).commit();

          TextView txt =(TextView)findViewById(R.id.text_show);
            txt.setText(tel.toString());*/

    }
}
