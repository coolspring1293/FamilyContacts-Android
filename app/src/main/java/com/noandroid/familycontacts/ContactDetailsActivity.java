package com.noandroid.familycontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.noandroid.familycontacts.model.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noandroid.familycontacts.model.Blacklist;
import com.noandroid.familycontacts.model.BlacklistDao;
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
    public String telephoneNum = "18814091386";
    public String contactId = null;
    public TextView textView_desc;
    public TextView textView_name;


    public Contact mContact;
    private String cName = null;
    private String cRelationship = null;
    private Boolean cAvatar = false;
    private List<Telephone> mTel;

    /* Contact Basic Info End */

    private com.getbase.floatingactionbutton.FloatingActionButton call_button;
    private com.getbase.floatingactionbutton.FloatingActionButton sms_button;
    private com.getbase.floatingactionbutton.FloatingActionButton block_button;

    private SMSProcessor smsProcessor  = new SMSProcessor();



    public static Context context;

    public BitmapProcessor bitmapProcessor = new BitmapProcessor();



    private static int Width;
    private static int Height;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cda);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager  = (ViewPager) findViewById(R.id.viewpager);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);
        mProfileImage = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.details_img_avatar);
        mCollapsingToolbarLayout = (android.support.design.widget.CollapsingToolbarLayout) findViewById(R.id.collapsing);

        textView_desc = (TextView) findViewById(R.id.text_for_desc);
        textView_name = (TextView) findViewById(R.id.text_for_name);


        WindowManager wm = this.getWindowManager();
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();


        ImageButton button_add_contact = (ImageButton) findViewById(R.id.contact_edit);


        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#9900FF"));
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);



        /* Get ID from Contact Fragment */
        Bundle bundle = this.getIntent().getExtras();
        if (!bundle.isEmpty()) {
            // From record and no id but Single telephone Exit
            if (null == bundle.getString("contactId")) {
                telephoneNum = bundle.getString("telephoneNum");
                textView_name.setText(telephoneNum);
                textView_desc.setText("Unknown");

                button_add_contact.setImageResource(R.drawable.ic_add_circle_24dp);

                button_add_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ContactDetailsActivity.this, EditContactActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("contactId", contactId);
                        bundle.putString("tmp_tel", telephoneNum);
                        intent.putExtras(bundle);
                        //startActivityForResult(intent, REQUESTCODE);
                        startActivityForResult(intent, EditContactActivity.ADD_CONTACT);
                    }
                });

            }
            else {
                contactId = bundle.getString("contactId");

                button_add_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ContactDetailsActivity.this, EditContactActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("contactId", contactId);
                        intent.putExtras(bundle);
                        //startActivityForResult(intent, REQUESTCODE);
                        startActivityForResult(intent, EditContactActivity.EDIT_CONTACT);
                    }
                });
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.materialup_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        context = getApplicationContext();


        // Weather
        final WeatherStatusReceiver mWthReceiver = new WeatherStatusReceiver();
        IntentFilter filter = new IntentFilter(WeatherStatusReceiver.NEW_WEATHER);
        LocalBroadcastManager.getInstance(this).registerReceiver(mWthReceiver, filter);
        call_button =(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.menu_call);
        sms_button =(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.menu_sms);
        block_button =(com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.menu_block);

        block_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContact != null) {
                    for (Telephone tel : mContact.getTelephones()) {
                        DatabaseHelper.getDaoMaster(getApplicationContext()).newSession().getBlacklistDao()
                                .insert(new Blacklist(null, tel.getNumber()));
                    }
                } else {
                    DatabaseHelper.getDaoMaster(getApplicationContext()).newSession().getBlacklistDao()
                            .insert(new Blacklist(null, telephoneNum));
                }
                Snackbar.make(v, "Added to blacklist.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowTelDialog();

            }
        });



        sms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowSMSDialog();

            }
        });

    }
    public void ShowTelDialog() {
        int id = 0;
        List<String> tels = new ArrayList<>();
        for(Telephone tel : mTel) {
            tels.add(tel.getNumber());
        }
        final SMSDialog smsDialog = new SMSDialog(this,tels,id);
        smsDialog.show();

        smsDialog.setClickListener(new SMSDialog.ClickListenerInterface() {

            @Override
            public void doCancel() {
                smsDialog.dismiss();
            }
            @Override
            public void doSend() {
                String call = smsDialog.getTargetTel();

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+call));
                //enforceCallingPermission("android.permission.CALL_PHONE","");
                startActivity(intent);
            }
        });
    }

    public void ShowSMSDialog() {
        int id = 1;
        List<String> tels = new ArrayList<>();
        for(Telephone tel : mTel) {
            tels.add(tel.getNumber());
        }
        final SMSDialog smsDialog = new SMSDialog(this,tels,id);
        smsDialog.show();

        smsDialog.setClickListener(new SMSDialog.ClickListenerInterface() {

            @Override
            public void doCancel() {
                smsDialog.dismiss();
            }
            @Override
            public void doSend() {
                String weather = "";
                String temperature = "";
                if(!smsDialog.getTargetTel().equals("")) {
                    for(Telephone tel : mTel) {
                        if(smsDialog.getTargetTel().equals(tel.getNumber())) {
                            City city = DatabaseHelper.getDaoMaster(getApplicationContext())
                                    .newSession().getCityDao().load(tel.getTelCityId());
                            weather = city.getWeatherInfo();
                            temperature=city.getTemperature();
                            break;
                        }
                    }
                    smsProcessor.sendSMS(getBaseContext(),smsDialog.getTargetTel(),"",weather,temperature);

                } else {
                    Toast.makeText(getBaseContext(),"Please Choose a telephone ",Toast.LENGTH_SHORT).show();
                }
                //smsProcessor.sendSMS(getBaseContext(),"18824110669","me",mTel.get(0).getCity().getWeatherInfo(),mTel.get(0).getCity().getTemperature());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContactDetails();
        // Update Weather info no matter any case
        updateAllWeather();
    }

    private void updateContactDetails() {

        if (contactId != null) {
            String _id = contactId;
            mContact = DatabaseHelper.getDaoMaster(getApplicationContext()).newSession().getContactDao()
                    .load(Long.parseLong(_id));
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
            textView_name.setText(cName);
            textView_desc.setText(cRelationship);
        }
        else {
            // I am not sure if the following is right.
            Long cityId = null;
            try {
                cityId = Telephone.getCityIdForTel(telephoneNum);
            } catch (Exception e) {}
            Telephone tel = new Telephone(null, telephoneNum, cityId, null);
            mTel = new ArrayList<Telephone>();
            mTel.add(tel);
        }


    }


    private void updateAllWeather() {
        for (Telephone t : mTel) {
            if (t.getTelCityId() != null) {
                String cCityCode = MainActivity.cityDao.load(t.getTelCityId()).getWeatherCode();

                Intent weaIntent = new Intent(this, WeatherService.class);
                weaIntent.putExtra(WeatherService.ACTION, WeatherService.REFRESH_REAL_WEATHER);
                weaIntent.putExtra(WeatherService.CITYCODE, cCityCode);

                startService(weaIntent);
            }
            else {
                return;
            }
        }
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
                case 0: return TelephoneTabFragment.newInstance(mTel);

                //TODO: Leasunhy
                case 1:
                    if (contactId == null)
                        return CallLogTabFragment.newInstance(null, telephoneNum);
                    else
                        return CallLogTabFragment.newInstance(contactId, null);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "Telephone Details";
                case 1: return "Recent Records";
            }
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditContactActivity.ADD_CONTACT || requestCode == EditContactActivity.EDIT_CONTACT)
            finish();
    }
}
