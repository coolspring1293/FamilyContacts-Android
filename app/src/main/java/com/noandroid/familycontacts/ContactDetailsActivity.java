package com.noandroid.familycontacts;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.net.Uri;
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
import android.widget.*;

import com.noandroid.familycontacts.model.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import de.greenrobot.dao.query.QueryBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;


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
    private boolean haveID = true;

    final String PATH = Environment.getExternalStorageDirectory() + "/com.noandroid.familycontacts/icon/";

    /* Contact Basic Info Start */
    public String contactName = "No Name";
    public String contactId = null;
    public TextView textView_desc;
    public TextView textView_name;


    public Contact mContact;
    private String cName = null;
    private String cRelationship = null;
    private Boolean cAvatar = false;
    private List<Telephone> mTel;

    /* Contact Basic Info End */


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
            contactName = bundle.getString("contactName");
            textView_name.setText(contactName);

            // From record and no id but Single telephone Exit
            if (null == bundle.getString("contactId")) {

                button_add_contact.setImageResource(R.drawable.ic_add_circle_24dp);

                button_add_contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(ContactDetailsActivity.this, EditContactActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("contactId", contactId);
                        bundle.putString("tmp_tel", contactName);
                        intent.putExtras(bundle);
                        //startActivityForResult(intent, REQUESTCODE);
                        startActivity(intent);
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
                        startActivity(intent);
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
        loadRecords();
    }

    public class RecordRecyclerViewAdapter extends RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder> {
        private LayoutInflater inflater;
        private List<Record> mRecords;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/M/d EEE H:m", Locale.ENGLISH);

        public String formatDate(Date date) {
            return dateFormatter.format(date);
        }

        public String formatDuration(int duration) {
            if (duration >= 60)
                return String.format("%dm%ds", duration / 60, duration % 60);
            else
                return String.format("%ds", duration);
        }

        public RecordRecyclerViewAdapter(Context context, List<Record> list) {
            super();
            if (list == null)
                throw new IllegalArgumentException("List must not be null");
            this.mRecords = list;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.contact_detail_record_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Record record = mRecords.get(position);
            holder.icon.setImageResource(RecordCursorAdapter.getIconIdForCallType(record.getStatus()));
            holder.phone_number.setText(record.getTelephoneNumber());
            holder.date.setText(formatDate(record.getTime()));
            holder.duration.setText(formatDuration(record.getDuration()));
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView phone_number;
            public TextView date;
            public TextView duration;

            public Long contactid;
            public String telephone;

            public ViewHolder(View view) {
                super(view);
                icon = (ImageView)view.findViewById(R.id.icon);
                phone_number = (TextView)view.findViewById(R.id.phone_number);
                date = (TextView)view.findViewById(R.id.date);
                duration = (TextView)view.findViewById(R.id.duration);
            }
        }
    }

    private void loadRecords() {
        RecyclerView rv = (RecyclerView) view2.findViewById(android.R.id.list);
        rv.setHasFixedSize(true);
        Long contactId = Long.parseLong(this.contactId);
        // TODO(leasunhy): implement records for strangers
        if (contactId == null) return;
        Log.e("DEBUG", this.contactId);
        Log.e("DEBUG", contactId.toString());
        QueryBuilder<Record> query = MainActivity.recordDao.queryBuilder();
        query.join(RecordDao.Properties.TelephoneNumber, Telephone.class, TelephoneDao.Properties.Number)
             .where(TelephoneDao.Properties.ContactId.eq(contactId));
        List<Record> records = query.build().list();
        rv.setAdapter(new RecordRecyclerViewAdapter(getApplicationContext(), records));
>>>>>>> finish record displaying in detail page
    }




    @Override
    public void onResume() {
        super.onResume();
        updateContactDetails();
        // Update Weather info no matter any case
        updateAllWeather();
    }

    private void updateContactDetails() {

        if (haveID) {
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

            textView_desc.setText(cRelationship);
        }
        else {
            // No id, get tel number by contactName
            String singleTelephoneNumber = contactName;

            // I am not sure if the following is right.
            Telephone tempTel = new Telephone(null, singleTelephoneNumber,
                     MainActivity.telDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(
                            singleTelephoneNumber.substring(0, 7))).build().unique().getTelCityId(),
                    null);

            mTel.add(tempTel);
        }


    }


    private void updateAllWeather() {
        for (Telephone t : mTel) {
            if (t.getCity() != null) {
                String cCityCode = t.getCity().getWeatherCode();

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
                case 1: return TelephoneTabFragment.newInstance(null);
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



}
