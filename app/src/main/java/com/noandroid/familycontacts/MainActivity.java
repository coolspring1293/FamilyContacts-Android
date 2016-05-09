package com.noandroid.familycontacts;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.noandroid.familycontacts.model.ContactDao;
import com.noandroid.familycontacts.model.DaoMaster;
import com.noandroid.familycontacts.model.DaoSession;
import com.noandroid.familycontacts.model.DatabaseHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class MainActivity extends FragmentActivity implements GestureDetector.OnGestureListener
        ,NavigationView.OnNavigationItemSelectedListener{
    public static Fragment[] mFragments;
    FragmentIndicator mIndicator;
    public static SQLiteDatabase db;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static ContactDao contactDao;

    /*手势识别*/
    public static GestureDetector detector;
    /*滑动距离*/
    final int DISTANT=50;
    public int mark = 0;

    private final int REQUESTCODE=1;
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
        setFragmentIndicator(0);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void setFragmentIndicator(int whichIsDefault) {
        mark = whichIsDefault;
        mFragments = new Fragment[3];
        mFragments[0] = getSupportFragmentManager().findFragmentById(R.id.fragment_contacts);
        mFragments[1] = getSupportFragmentManager().findFragmentById(R.id.fragment_records);
        mFragments[2] = getSupportFragmentManager().findFragmentById(R.id.fragment_dialing);

        getSupportFragmentManager().beginTransaction().hide(mFragments[0])
                .hide(mFragments[1]).hide(mFragments[2])
                .show(mFragments[whichIsDefault]).commit();

        mIndicator=(FragmentIndicator) findViewById(R.id.indicator);
        FragmentIndicator.setIndicator(whichIsDefault);
        mIndicator.setOnIndicateListener(new FragmentIndicator.OnIndicateListener() {

            @Override
            public void OnIndicate(View v, int which) {
                // TODO Auto-generated method stub
                getSupportFragmentManager().beginTransaction()
                        .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).
                        show(mFragments[which]).commit();
                mark = which;
            }

        });
    }


    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {

        Toast.makeText(getBaseContext(),"----onFling---"+arg1.getX()+">" +arg0.getX() +" + "+DISTANT,Toast.LENGTH_SHORT).show();

        if(arg1.getX()>arg0.getX()+DISTANT) {
            FragmentIndicator.setIndicator((mark-1)%3);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out)
                    .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).show(mFragments[(mark - 1)%3]).commit();
            mark = (mark-1)%3;
        }
        if(arg1.getX()<arg0.getX()+DISTANT) {
            FragmentIndicator.setIndicator((mark+1)%3);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
                    .hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).show(mFragments[(mark + 1)%3]).commit();
            mark = (mark+1)%3;
        }
        return false;
    }
    @Override
    public void onShowPress(MotionEvent arg0) {}
    @Override
    public void onLongPress(MotionEvent arg0) {}
    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }
    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(getBaseContext(),"----onFling---",Toast.LENGTH_SHORT);
        return detector.onTouchEvent(event);
    }
    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,float arg3) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera
            Toast.makeText(this,"nav_camera",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {

            Toast.makeText(this,"nav_camera",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(this,"nav_slideshow",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this,"nav_manage",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this,"nav_share",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this,"nav_send",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,"onActivityResult " + requestCode ,Toast.LENGTH_SHORT).show();
//
//            String tel = data.getStringExtra("tel");//接收返回数据
//            getSupportFragmentManager().beginTransaction().hide(mFragments[0])
//                        .hide(mFragments[1]).hide(mFragments[2])
//                        .show(mFragments[2]).commit();
//            TextView txt =(TextView)findViewById(R.id.text_show);
//            txt.setText(tel.toString());

    }
}
