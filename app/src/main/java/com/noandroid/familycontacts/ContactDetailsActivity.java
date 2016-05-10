package com.noandroid.familycontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noandroid.familycontacts.model.City;
import com.noandroid.familycontacts.model.Contact;
import com.noandroid.familycontacts.model.ContactDao;
import com.noandroid.familycontacts.model.TelInitialDao;
import com.noandroid.familycontacts.model.Telephone;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liuw53 on 5/4/16.
 */


public class ContactDetailsActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;

    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private android.support.design.widget.CollapsingToolbarLayout mCollapsingToolbarLayout;
    private int mMaxScrollSize;

    final String PATH = Environment.getExternalStorageDirectory() + "/com.noandroid.familycontacts/icon/";

    /* Contact Basic Info Start */
    String contactName = "No Name";
    String contactId = null;
    TextView textView_desc;
    TextView textView_name;
    Contact mContact;

    private String cName = null;
    private String cRelationship = null;
    private Boolean cAvatar = false;
    private String cTel = null;
    private City mCity = null;
    private String mWeather = null;
    private List<Telephone> mTel;

    /* Contact Basic Info End */


    private static Context context;

    public BitmapProcessor bitmapProcessor = new BitmapProcessor();


    // fragment
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();
    private View view1, view2;
    private List<View> mViewList = new ArrayList<>();

    private static int Width;
    private static int Height;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cda);

        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);
        mProfileImage = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.details_img_avatar);
        mCollapsingToolbarLayout = (android.support.design.widget.CollapsingToolbarLayout) findViewById(R.id.collapsing);

        textView_desc = (TextView) findViewById(R.id.text_for_desc);
        textView_name = (TextView) findViewById(R.id.text_for_name);


        WindowManager wm = this.getWindowManager();
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();


        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#9900FF"));
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);

        /* Get ID from Contact Fragment */
        Bundle bundle = this.getIntent().getExtras();
        if (!bundle.isEmpty()) {
            contactName = bundle.getString("contactName");
            contactId = bundle.getString("contactId");
        }

        /* TEST */
        textView_name.setText(contactName);
        textView_desc.setText(contactId);


        Toolbar toolbar = (Toolbar) findViewById(R.id.materialup_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ImageButton button_add_contact = (ImageButton) findViewById(R.id.contact_edit);

        button_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ContactDetailsActivity.this, EditContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("contactId", contactId);
                intent.putExtras(bundle);
                //startActivityForResult(intent, REQUESTCODE);
                startActivity(intent);
            }
        });


        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();



        context = getApplicationContext();

        //Tab
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mTabLayout = (TabLayout)findViewById(R.id.tabs);

        mInflater = LayoutInflater.from(this);
        view1 = mInflater.inflate(R.layout.tab1,null);
        view2 = mInflater.inflate(R.layout.tab2,null);

        mViewList.add(view1);
        mViewList.add(view2);

        mTitleList.add("Telephone Details");
        mTitleList.add("Recent Records");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);


    }


    @Override
    public void onResume() {
        super.onResume();
        updateContactDetails();
    }

    private void updateContactDetails() {
        String _id = contactId;
        mContact = MainActivity.daoSession.getContactDao().queryBuilder().where(
                ContactDao.Properties.Id.eq(_id)).build().unique();
        if (null != mContact) {
            cName = mContact.getName();
            cAvatar = mContact.getAvatar();
            cRelationship = mContact.getRelationship();
            mTel = mContact.getTelephones();
        }
        // set avatar
        if (!cAvatar) {
            mProfileImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar));
            mCollapsingToolbarLayout.setBackgroundResource(R.drawable.default_bg);
        } else {
            //From SD Card Get the icon photo
            Bitmap tBitmap = getDiskBitmap(PATH + _id + ".png");
            mProfileImage.setImageBitmap(tBitmap);
            Drawable drawable = new BitmapDrawable(bitmapProcessor.AfterBlurring(context, tBitmap, Width, Height));
            mCollapsingToolbarLayout.setBackground(drawable);
        }


        // get telephone
        if (!mTel.isEmpty()) {
            cTel = (mTel.get(0)).getNumber();
            mCity = MainActivity.daoSession.getTelInitialDao().queryBuilder().where(
                    TelInitialDao.Properties.Initial.eq(cTel.substring(0, 7))
            ).build().unique().getCity();
        }

        // Todo:
        textView_desc.setText(cRelationship + " " + cTel + " " + mWeather + " " + cAvatar.toString());
        String tmp_s = "";
        for (Telephone t : mTel) {
            tmp_s += (t.getNumber() + " " + getWeatherDesc(t.getCity()) + "\n");
        }
        textView_desc.setText(tmp_s);
    }


    private String getWeatherDesc(City c) {
        String m = "";
        if (null != c) {
            if (c.getWeatherInfo() == null) {
                String cCityCode = c.getWeatherCode();
                final WeatherStatusReceiver mWthReceiver = new WeatherStatusReceiver();
                IntentFilter filter = new IntentFilter(WeatherStatusReceiver.NEW_WEATHER);
                LocalBroadcastManager.getInstance(this).registerReceiver(mWthReceiver, filter);
                Intent weaIntent = new Intent(this, WeatherService.class);
                weaIntent.putExtra(WeatherService.ACTION, WeatherService.REFRESH_REAL_WEATHER);
                weaIntent.putExtra(WeatherService.CITYCODE, cCityCode);
                startService(weaIntent);
            } else {
                m = String.format("%s %s %s℃", c.getCityname(),
                        c.getWeatherInfo(), c.getTemperature());

            }
        } else {
            m = "No location and weather data";
        }
        return m;
    }


    public static Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, ContactDetailsActivity.class));
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            mProfileImage.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }


    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)  {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
        @Override
        public CharSequence getPageTitle(int postion) {
            return mTitleList.get(postion);
        }
    }

    public class WeatherStatusReceiver extends BroadcastReceiver {
        public WeatherStatusReceiver() {}
        public static final String NEW_WEATHER = "com.noandroid.familycontacts.NEW_WEATHER";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("WeatherStatusReceiver", "Received");
            if (NEW_WEATHER.equals(intent.getAction())) {
//                String weaCode = intent.getStringExtra(WeatherService.EXTRA_WEATHER_CITYCODE);
//                String temp = intent.getStringExtra(WeatherService.EXTRA_WEATHER_TEMPERATURE);
//                String info = intent.getStringExtra(WeatherService.EXTRA_WEATHER_INFO);
//                if (weaCode != null && weaCode.equals(mCity.getWeatherCode())
//                        && temp != null && info != null) {
                MainActivity.daoSession.getCityDao().refresh(mCity);
                updateContactDetails();
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
//                }
            }
        }
    }


    class TabsAdapter extends FragmentPagerAdapter {
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0: return TelephoneTabFragment.newInstance();
                case 1: return TelephoneTabFragment.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Tab 1";
                case 1: return "Tab 2";
            }
            return "";
        }
    }

}
