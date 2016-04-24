package com.example.admin.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.example.admin.myapplication.model.ContactDao;
import com.example.admin.myapplication.model.DaoMaster;
import com.example.admin.myapplication.model.DaoSession;
import com.example.admin.myapplication.model.DatabaseHelper;



public class MainActivity extends FragmentActivity implements GestureDetector.OnGestureListener {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector=new GestureDetector(this);

        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        setFragmentIndicator(0);
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
