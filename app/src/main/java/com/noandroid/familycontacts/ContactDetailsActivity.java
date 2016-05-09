package com.noandroid.familycontacts;

import android.content.Context;
import android.content.Intent;
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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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


    private static int Width;
    private static int Height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cda);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.materialup_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.materialup_viewpager);
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

        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);


        context = getApplicationContext();

    }


    @Override
    public void onResume() {
        super.onResume();
        updateContactDetails();
    }

    private void updateContactDetails() {
        Bitmap bitmap;
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
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.allen_xie_icon_darker);
        } else {
            //From SD Card Get the icon photo
            bitmap = getDiskBitmap(PATH + _id + ".png");
        }
        mProfileImage.setImageBitmap(bitmap);
        Drawable drawable = new BitmapDrawable(bitmapProcessor.AfterBlurring(context, bitmap, Width, Height));
        mCollapsingToolbarLayout.setBackground(drawable);


        // get telephone
        if (!mTel.isEmpty()) {
            cTel = (mTel.get(0)).getNumber();
            mCity = MainActivity.daoSession.getTelInitialDao().queryBuilder().where(
                    TelInitialDao.Properties.Initial.eq(cTel.substring(0, 7))
            ).build().unique().getCity();
        }
        if (null != mCity) {
            mWeather = mCity.getCityname() + " " + mCity.getWeatherInfo() + " " + mCity.getTemperature();
        } else {
            mWeather = "No location and weather data";
        }
        // Todo:
        textView_desc.setText(cRelationship + " " + cTel + " " + mWeather + " " + cAvatar.toString());
    }

    private Bitmap getDiskBitmap(String pathString) {
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
            switch (i) {
                case 0:
                    return MaterialUpConceptFakePage.newInstance();
                case 1:
                    return MaterialUpConceptFakePage.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details of Contact";
                case 1:
                    return "Recent Record";
            }
            return "";
        }
    }


}
