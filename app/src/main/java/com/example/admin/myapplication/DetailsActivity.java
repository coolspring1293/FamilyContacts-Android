package com.example.admin.myapplication;

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

/**
 * Created by liuw53 on 4/14/16.
 */
public class DetailsActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ContactDao contactDao;


    private String number = "18824110669";
    private static int Width;
    private static int Height;
    private GoogleApiClient client;
    private String path = Environment.getExternalStorageDirectory() + "/familycontact/icon/xiaoxin/";
    private ImageView tmp_iv;
    private EditText tmp_et;
    private de.hdodenhof.circleimageview.CircleImageView img;
    private android.support.v7.widget.AppCompatImageButton bg;
    final String icon_name = "xiaoxin_";
    final String icon_end  = ".jpg";

    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private ImageView iv_image;

    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;


    private String mName;

    private String mAvatar;
    private String mWeather;
    private String mRelationship;
    private String mTele_info = "Not found telephone number";
    private City   mCity;

    private List<Telephone> mTel;


    private Contact mContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView textview_weather = (TextView) findViewById(R.id.weather_info);
        TextView textview_relationship = (TextView) findViewById(R.id.relations);
        TextView textview_tel = (TextView) findViewById(R.id.tel_loc_info);

        ImageView imageView_avatar = (ImageView) findViewById(R.id.img_avatar);

        Bundle bundle = this.getIntent().getExtras();
        String contactName = bundle.getString("contactName");
        String contactId = bundle.getString("contactId");
        toolbar.setTitle(contactName);
        toolbar.setSubtitle(contactId);
        //toolbar.setLogo(R.drawable.allen_xie_icon);


        img = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.img_avatar);
        bg =  (android.support.v7.widget.AppCompatImageButton)findViewById(R.id.button_bg_change_avatar);

        setSupportActionBar(toolbar);
        WindowManager wm = this.getWindowManager();
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();

        //TODO find list<tel> according to contacId ,  and display


        // (TODO):Liu Wang
        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        updateListContent();

        //TODO find list<tel> according to contacId ,  and display
        getBasicInfo(contactId);


        // 输出
        textview_relationship.setText(mRelationship);

        textview_tel.setText(mTele_info);

        if(null != mCity) {
            // TODO add weather query
            String displayStr = String.format("%s: the weather code of which is %s.",
                    mCity.getCityname(), mCity.getWeatherCode());
            mWeather = displayStr;

        } else {
            mWeather = "No location and weather data";
        }

        textview_weather.setText(mWeather);

        if (mAvatar != null) {
            imageView_avatar.setImageBitmap(getDiskBitmap(mAvatar));
            // TODO (Guan Lu) 自动更新背景
        }


        /*
        call fab 修改，如果为电话，就传递
         */
        FloatingActionButton fab_call = (FloatingActionButton) findViewById(R.id.fab_call);
        assert fab_call != null;
        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (mTel.isEmpty()){
                    intent.putExtra("tel", "");
                }
                else {
                    intent.putExtra("tel", mTele_info);
                }
                setResult(1, intent);
                finish();//结束当前的activity的生命周期


               /* City myCity = daoSession.getTelInitialDao().queryBuilder().where(
                        TelInitialDao.Properties.Initial.eq("1301996")).build().unique().getCity();
                String displayStr = String.format("1301996 is in %s, the weather code of which is %s.",
                        myCity.getCityname(), myCity.getWeatherCode());
                Snackbar.make(view, displayStr, Snackbar.LENGTH_LONG).setAction("Action", null).show();*/
            }
        });



        // TODO: message sending
        FloatingActionButton fab_message = (FloatingActionButton) findViewById(R.id.fab_message);
        FloatingActionButton fab_favorite = (FloatingActionButton) findViewById(R.id.fab_fave);




        AppCompatImageButton btn = (AppCompatImageButton) findViewById(R.id.button_bg_change_avatar);
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


    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    // 通过主键获取联系人的姓名、电话
    private void getBasicInfo(String _id) {
        //mContact = (Contact)daoSession.getContactDao().queryBuilder().where(
        //         ContactDao.Properties.Id.eq(_id)).build().unique().getClass());

        mName = daoSession.getContactDao().queryBuilder().where(
                ContactDao.Properties.Id.eq(_id)).build().unique().getName();
        mRelationship = daoSession.getContactDao().queryBuilder().where(
                ContactDao.Properties.Id.eq(_id)).build().unique().getRelationship();
        mAvatar = daoSession.getContactDao().queryBuilder().where(
                ContactDao.Properties.Id.eq(_id)).build().unique().getAvatar();
        mTel =  daoSession.getContactDao().queryBuilder().where(
                ContactDao.Properties.Id.eq(_id)).build().unique().getTelephones();

        // TODO get all telephone list
        if (!mTel.isEmpty()) {
            mTele_info = ((Telephone)mTel.get(0)).getNumber();
            mCity = daoSession.getTelInitialDao().queryBuilder().where(
                    TelInitialDao.Properties.Initial.eq(mTele_info.substring(0, 6))
            ).build().unique().getCity();
        }
    }

    protected void dialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("get pic from ");
        builder.setTitle("avatar");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* GetPicFromGallery();*/
                gallery();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Camera",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* GetPicFromCamera();*/
                camera();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /*
	 * 从相册获取
	 */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                //Toast.makeText(MainActivity.this, "未找到存储卡，无法存储照片！", 0).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                img.setImageBitmap(bitmap);
                Bitmap bitmap_bg = data.getParcelableExtra("data");
                bg.setImageBitmap(big(blurBitmap(bitmap_bg)));

                try {
                    saveBitmapToFile(bitmap, path + "/familycontact/icon/tmp_file/1.png");
                }
                catch (IOException e) {
                    // Log.e(TAG_ERROR, e.getMessage(), e);
                }
            }
            try {
                // 将临时文件删除

                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public Bitmap blurBitmap(Bitmap bitmap){

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getApplicationContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        float radius = 25.0f;
        blurScript.setRadius(radius);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        //bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;

    }

    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale((float)Width/(float)bitmap.getWidth(),(float)Height / (float)bitmap.getHeight() * 0.4f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    public static void saveBitmapToFile(Bitmap bitmap, String _file)  throws IOException
    {//_file = <span style="font-family: Arial, Helvetica, sans-serif;">getSDPath()+"</span><span style="font-family: Arial, Helvetica, sans-serif;">/xx自定义文件夹</span><span style="font-family: Arial, Helvetica, sans-serif;">/hot.png</span><span style="font-family: Arial, Helvetica, sans-serif;">"</span>
        FileOutputStream os = null;
        try {
            File file = new File(_file);
            // String _filePath_file.replace(File.separatorChar +
            // file.getName(), "");
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Log.e(TAG_ERROR, e.getMessage(), e);
                }
            }
        }
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