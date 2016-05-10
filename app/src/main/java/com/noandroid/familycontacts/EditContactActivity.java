package com.noandroid.familycontacts;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
import android.text.InputType;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.view.View;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditContactActivity extends Activity {


    final String PATH = Environment.getExternalStorageDirectory() + "/com.noandroid.familycontacts/icon/";

    private AutoCompleteTextView atct_name;
    // private AutoCompleteTextView atct_telephone;
    // private AutoCompleteTextView atct_avatar;
    private AutoCompleteTextView atct_relationship;


    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private ContactDao contactDao;
    private TelInitialDao telInitialDao;
    private TelephoneDao telDao;


    // About changing the avatar
    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private CircleImageView icon_image;
    private AppCompatImageButton icon_background;
    private static int Width;
    private static int Height;
    private static Bitmap mBitmap;
    private static Bitmap bitmap_bg;


    private static Context context;
    public BitmapProcessor bitmapProcessor = new BitmapProcessor();


    // private Button button_ok;
    private FloatingActionButton button_ok;
    private String mName;
    private String mRelationship;
    private Boolean mAvatar = false;
    private String sTel;
    private List<Telephone> mTel;


    public boolean haveId = false;
    private String contactId;
    private String _id;


    private Bitmap bitmap;


    ///////////////////////////////
    private ImageButton add_button;
    private static int id = 100;
    private Button bt;
    private int count = 0;
    private HashMap<Integer, AutoCompleteTextView> hm = new HashMap<>();

    public String fill_tel = "";

    LinearLayout lin = null;
    LinearLayout.LayoutParams LP_FW = null;
    RelativeLayout newSingleRL = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);


        /* DB Init Start */
        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        telDao = daoSession.getTelephoneDao();
        telInitialDao = daoSession.getTelInitialDao();
        //updateListContent();
        /* DB Init End * */


        /* Background */
        icon_background = (AppCompatImageButton) findViewById(R.id.button_bg_change_avatar_edit);

        icon_image = (CircleImageView) findViewById(R.id.img_avatar);
        WindowManager wm = this.getWindowManager();
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();


        add_button = (ImageButton) findViewById(R.id.edit_add);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateUI();
            }
        });

        lin = (LinearLayout) findViewById(R.id.list_Lin);
        LP_FW = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        newSingleRL = new RelativeLayout(this);

        /* Background End */

        /* Context */
        context = getApplicationContext();

        /* Get ID from Contact Fragment */
        Bundle bundle = this.getIntent().getExtras();
        if (!bundle.isEmpty()) {
            contactId = bundle.getString("contactId");
        }

        atct_name = (AutoCompleteTextView) findViewById(R.id.edit_name);
        atct_relationship = (AutoCompleteTextView) findViewById(R.id.edit_relationship);

        if (null != contactId) {
            _id = contactId;
            haveId = true;
            // set Avatar
            bitmap = ContactDetailsActivity.getDiskBitmap(PATH + _id + ".png");
            icon_image.setImageBitmap(bitmap);
            icon_background.setImageBitmap(bitmapProcessor.AfterBlurring(context, bitmap, Width, Height));
            Contact mContact = MainActivity.daoSession.getContactDao().queryBuilder().where(
                    ContactDao.Properties.Id.eq(_id)).build().unique();
            if (null != mContact) {
                mName = mContact.getName();
                mAvatar = mContact.getAvatar();
                mRelationship = mContact.getRelationship();
                mTel = mContact.getTelephones();
            }

            if (!mTel.isEmpty()) {
                for (Telephone telephone : mTel) {
                    fill_tel = telephone.getNumber();
                    CreateUI();
                }
            }

            // Clear the database telephone data about this contact
            atct_name.setText(mName);
            atct_relationship.setText(mRelationship);
            // Notice
            // telDao.deleteInTx(mContact.getTelephones());


        } else {
            icon_image.setImageResource(R.drawable.default_avatar);
            atct_name.setText("");
            atct_relationship.setText("");
            CreateUI();
        }




        /* 头像按钮的相应 */
        icon_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

         /* 提交按钮的相应 */
        button_ok = (FloatingActionButton) findViewById(R.id.edit_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {        //修改
            @Override
            public void onClick(View v) {

                if (haveId == false) {
                    Contact contact = new Contact(
                            null,
                            atct_name.getText().toString(),
                            atct_relationship.getText().toString(),
                            mAvatar
                    );

                    contactDao.insert(contact);
                    _id = contact.getId().toString();

                    ArrayList<String> l = getAllACTVData();
                    // Delete old all and add add
                    telDao.deleteInTx(contact.getTelephones());
                    for (String tmp : l) {
                        if (tmp.length() > 6) {
                            Telephone tel = new Telephone(null, tmp,
                                    telInitialDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(
                                            tmp.substring(0, 7))).build().unique().getTelinitCityId(),
                                    contact.getId());
                            telDao.insert(tel);
                        } else {
                            // 避免添加空值
                            if (tmp != "") {
                                Telephone tel = new Telephone(null, tmp, null, contact.getId());
                                telDao.insert(tel);
                            }
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Added",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Contact contact = new Contact(
                            Long.parseLong(_id),
                            atct_name.getText().toString(),
                            atct_relationship.getText().toString(),
                            mAvatar
                    );
                    contactDao.update(contact);

                    telDao.deleteInTx(contact.getTelephones());

                    ArrayList<String> l = getAllACTVData();
                    // Delete old all and add add
                    telDao.deleteInTx(contact.getTelephones());
                    for (String tmp : l) {
                        if (tmp.length() > 6) {
                            Telephone tel = new Telephone(null, tmp,
                                    telInitialDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(
                                            tmp.substring(0, 7))).build().unique().getTelinitCityId(),
                                    contact.getId());
                            telDao.insert(tel);
                        } else {
                            Telephone tel = new Telephone(null, tmp, null, contact.getId());
                            telDao.insert(tel);
                        }
                    }

                    Toast.makeText(getApplicationContext(), "Edited",
                            Toast.LENGTH_SHORT).show();
                }
                writeImagetoSD();

                finish();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void CreateUI() {
        count++;
        newSingleRL = generateSingleLayout(id);
        lin.addView(newSingleRL, LP_FW);
        fill_tel = "";
    }


    private RelativeLayout generateSingleLayout(int imageID) {
        final RelativeLayout layout_root_relative = new RelativeLayout(this);

        LinearLayout layout_sub_Lin = new LinearLayout(this);

        //layout_sub_Lin.setBackgroundColor(Color.argb(0xff, 0xaa, 0xaa, 0xaa));

        layout_sub_Lin.setOrientation(LinearLayout.VERTICAL);
        layout_sub_Lin.setPadding(10, 10, 0, 0);


        final AutoCompleteTextView actv = new AutoCompleteTextView(this);
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        actv.setTextSize(20);
        actv.setSingleLine();
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
        actv.setInputType(inputType);


        Double tW = new Double(Width * 0.85);
        actv.setWidth(tW.intValue());
        actv.setHeight(120);

        actv.setId(View.generateViewId());


        actv.setLayoutParams(LP_WW);


        layout_sub_Lin.addView(actv);

        RelativeLayout.LayoutParams RL_MW = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //RL_MW.setMargins(5, 5, 10, 5);
        RL_MW.addRule(RelativeLayout.LEFT_OF, imageID);
        layout_root_relative.addView(layout_sub_Lin, RL_MW);


        ImageButton imageView = new ImageButton(this);
        RelativeLayout.LayoutParams RL_WW = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // imageView.setPadding(5, 5, 5, 5);


        imageView.setId(View.generateViewId());
        RL_WW.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageView.setLayoutParams(RL_WW);
        imageView.setClickable(true);
        imageView.setId(View.generateViewId());
        imageView.setBackgroundColor(Color.argb(0x00, 0xff, 0xff, 0xff));
        imageView.setImageResource(R.drawable.ic_remove_circle_32dp);

        actv.setText(fill_tel);


        hm.put(count, actv);
        final int tmp = count;


        layout_root_relative.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                hm.remove(tmp);
                Toast.makeText(getApplicationContext(), "Remove: " + count,
                        Toast.LENGTH_SHORT).show();
                layout_root_relative.setVisibility(View.GONE);

            }
        });
        return layout_root_relative;
    }


    private ArrayList<String> getAllACTVData() {
        ArrayList<String> l = new ArrayList<>();
        Iterator iter = hm.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, AutoCompleteTextView> entry = (Map.Entry<Integer, AutoCompleteTextView>) iter.next();
            AutoCompleteTextView val = entry.getValue();
            if (isNumeric(val.getText().toString()) && val.getText().toString() != "") {
                l.add(val.getText().toString());
            }
        }
        return l;
    }

    // the 0-9 only
    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }


    private void writeImagetoSD() {
        if (mBitmap != null) {
            try {
                saveBitmapToFile(mBitmap, PATH + _id.toString() + ".png");
                Contact tmp_contact = new Contact(
                        Long.parseLong(_id),
                        atct_name.getText().toString(),
                        atct_relationship.getText().toString(),
                        true
                );
                contactDao.update(tmp_contact);

            } catch (IOException e) {
                // Log.e(TAG_ERROR, e.getMessage(), e);
            }
        }
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


    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
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
            if (data != null) {
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(EditContactActivity.this,
                        "Can not find the SD card! Storing files failed!", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                mBitmap = data.getParcelableExtra("data");
                icon_image.setImageBitmap(mBitmap);
                bitmap_bg = data.getParcelableExtra("data");
                icon_background.setImageBitmap(bitmapProcessor.AfterBlurring(context, bitmap_bg, Width, Height));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public static void saveBitmapToFile(Bitmap bitmap, String _file) throws IOException {
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


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EditContact Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.noandroid.familycontacts/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "EditContact Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.noandroid.familycontacts/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}