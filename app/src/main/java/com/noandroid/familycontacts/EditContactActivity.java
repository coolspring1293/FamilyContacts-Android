package com.noandroid.familycontacts;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.widget.AppCompatImageButton;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;


public class EditContactActivity extends Activity {


    final String PATH = Environment.getExternalStorageDirectory() + "/com.noandroid.familycontacts/icon/";

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


    // About changing the avatar
    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private de.hdodenhof.circleimageview.CircleImageView icon_image;
    private android.support.v7.widget.AppCompatImageButton icon_background;
    private static int Width;
    private static int Height;
    private static Bitmap mBitmap;
    private static Bitmap bitmap_bg;


    private Button button_ok;

    private String mName;
    private String mRelationship;
    private Boolean mAvatar = false;
    private String sTel;
    private List<Telephone> mTel;


    public boolean haveId = false;
    private String contactId;
    private String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);


        /* Background */
        icon_background = (AppCompatImageButton) findViewById(R.id.button_bg_change_avatar_edit);
        icon_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        icon_image = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.img_avatar);
        WindowManager wm = this.getWindowManager();
        Width = wm.getDefaultDisplay().getWidth();
        Height = wm.getDefaultDisplay().getHeight();
         /* Background End */


        /* Get ID from Contact Fragment */
        Bundle bundle = this.getIntent().getExtras();
        if (!bundle.isEmpty()) {
            contactId = bundle.getString("contactId");
        }
        if (null != contactId) {
            _id = contactId;
            haveId = true;
        }


        atct_name = (AutoCompleteTextView) findViewById(R.id.edit_name);
        atct_telephone = (AutoCompleteTextView) findViewById(R.id.edit_telephone);
        atct_relationship = (AutoCompleteTextView) findViewById(R.id.edit_relationship);

        if (null != contactId) {
            _id = contactId;
            haveId = true;
            Contact mContact = MainActivity.daoSession.getContactDao().queryBuilder().where(
                    ContactDao.Properties.Id.eq(_id)).build().unique();
            if (null != mContact) {
                mName = mContact.getName();
                mAvatar = mContact.getAvatar();
                mRelationship = mContact.getRelationship();
                mTel = mContact.getTelephones();
            }

            if (!mTel.isEmpty()) {
                sTel = (mTel.get(0)).getNumber();
            }
            atct_name.setText(mName);
            atct_telephone.setText(sTel);
            atct_relationship.setText(mRelationship);


        } else {
            atct_name.setText("");
            atct_telephone.setText("");
            atct_relationship.setText("");
        }



        DaoMaster.DevOpenHelper helper = DatabaseHelper.getDB(this);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        contactDao = daoSession.getContactDao();
        telDao = daoSession.getTelephoneDao();
        telInitialDao = daoSession.getTelInitialDao();
        //updateListContent();

        button_ok = (Button) findViewById(R.id.edit_ok);


        button_ok.setOnClickListener(new View.OnClickListener() {		//修改
            @Override
            public void onClick(View v) {




                if (haveId == false) {
                    Contact contact = new Contact(
                            null,
                            atct_name.getText().toString(),
                            atct_relationship.getText().toString(),
                            mAvatar
                    );


                    // TODO
                    String telStr = atct_telephone.getText().toString();
                    contactDao.insert(contact);
                    _id = contact.getId().toString();



                    Telephone tel = new Telephone(null, telStr,
                            telInitialDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(
                                    telStr.substring(0, 7))).build().unique().getTelinitCityId(),
                            contact.getId());
                    telDao.insert(tel);


                    Toast.makeText(getApplicationContext(), "Added",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    Contact contact = new Contact(
                            Long.parseLong(_id),
                            atct_name.getText().toString(),
                            atct_relationship.getText().toString(),
                            mAvatar
                    );
                    contactDao.update(contact);

                    telDao.deleteInTx(contact.getTelephones());


                    String telStr = atct_telephone.getText().toString();
                    Telephone tel = new Telephone(null, telStr,
                            telInitialDao.queryBuilder().where(TelInitialDao.Properties.Initial.eq(
                                    telStr.substring(0, 7))).build().unique().getTelinitCityId(),
                            contact.getId());
                    telDao.insert(tel);
                    Toast.makeText(getApplicationContext(), "Edited",
                            Toast.LENGTH_SHORT).show();
                }
                writeImagetoSD();

                finish();
            }
        });


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





    private void updateListContent() {
        Cursor cursor = db.query(contactDao.getTablename(), contactDao.getAllColumns(), null, null, null, null, null);
        String[] from = { ContactDao.Properties.Name.columnName, ContactDao.Properties.Relationship.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                from, to, 0);
        ((ListView)findViewById(R.id.listView)).setAdapter(adapter);

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
                mBitmap = data.getParcelableExtra("data");
                icon_image.setImageBitmap(mBitmap);
                bitmap_bg = data.getParcelableExtra("data");
                icon_background.setImageBitmap(big(blurBitmap(bitmap_bg)));

            }
            try {
                saveBitmapToFile(mBitmap, PATH + "hhhhh" + ".png");
            } catch (IOException e) {
                // Log.e(TAG_ERROR, e.getMessage(), e);
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

    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale((float)Width/(float)bitmap.getWidth(),(float)Height / (float)bitmap.getHeight() * 0.4f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }


}