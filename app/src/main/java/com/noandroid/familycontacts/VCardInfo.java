package com.noandroid.familycontacts;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;

/**
 * 联系人信息包装类
 */
public class VCardInfo {

    /** MUST exist */
    private String name; // 姓名

    /** 联系人电话信息 */
    public static class PhoneInfo {
        /** 联系电话类型 */
        public int type;
        /** 联系电话 */
        public String number;
    }

    private List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>(); // 联系号码

    /**
     * 构造联系人信息
     * @param name 联系人姓名
     */
    public VCardInfo(String name) {
        this.name = name;
    }

    /** 姓名 */
    public String getName() {
        return name;
    }
    /** 姓名 */
    public VCardInfo setName(String name) {
        this.name = name;
        return this;
    }
    /** 联系电话信息 */
    public List<PhoneInfo> getPhoneList() {
        return phoneList;
    }
    /** 联系电话信息 */
    public VCardInfo setPhoneList(List<PhoneInfo> phoneList) {
        this.phoneList = phoneList;
        return this;
    }



    /**
     * 联系人备份/还原操作
     */
    public static class ContactHandler {

        private static ContactHandler instance_ = new ContactHandler();

        private final String FILE_NAME = "/com.noandroid.familycontacts/contacts.vcf";

        /** 获取实例 */
        public static ContactHandler getInstance(){
            return instance_;
        }

        /**
         * 从联系人List到vCard文件
         */
        public void backupContacts(Context context, List<VCardInfo> infos){

            try {

                String path = Environment.getExternalStorageDirectory() + FILE_NAME ;
                File file = new File(path);
                if(file.exists()){
                    // Do something
                }
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");

                VCardComposer composer = new VCardComposer();

                for (VCardInfo info : infos)
                {
                    ContactStruct contact = new ContactStruct();
                    contact.name = info.getName();
                    // 获取联系人电话信息, 添加至 ContactStruct
                    List<VCardInfo.PhoneInfo> numberList = info
                            .getPhoneList();
                    for (VCardInfo.PhoneInfo phoneInfo : numberList)
                    {
                        contact.addPhone(phoneInfo.type, phoneInfo.number,
                                null, true);
                    }
                    String vcardString = composer.createVCard(contact,
                            VCardComposer.VERSION_VCARD30_INT);
                    writer.write(vcardString);
                    writer.write("\n");

                    writer.flush();
                }
                writer.close();


                Toast.makeText(context, "Backup Successfully", Toast.LENGTH_SHORT).show();
            } catch (VCardException | IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * 获取vCard文件中的联系人信息
         * @return
         */
        public List<VCardInfo> restoreContacts() throws Exception {
            List<VCardInfo> contactInfoList = new ArrayList<VCardInfo>();

            VCardParser parse = new VCardParser();
            VDataBuilder builder = new VDataBuilder();
            String file = Environment.getExternalStorageDirectory() + FILE_NAME;

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            String vcardString = "";
            String line;
            while((line = reader.readLine()) != null) {
                vcardString += line + "\n";
            }
            reader.close();

            boolean parsed = parse.parse(vcardString, "UTF-8", builder);

            if(!parsed){
                throw new VCardException("Could not parse vCard file: "+ file);
            }

            List<VNode> pimContacts = builder.vNodeList;

            for (VNode contact : pimContacts) {

                ContactStruct contactStruct=ContactStruct.constructContactFromVNode(contact, 1);
                // 获取备份文件中的联系人电话信息
                List<ContactStruct.PhoneData> phoneDataList = contactStruct.phoneList;
                List<VCardInfo.PhoneInfo> phoneInfoList = new ArrayList<VCardInfo.PhoneInfo>();
                for(ContactStruct.PhoneData phoneData : phoneDataList){
                    VCardInfo.PhoneInfo phoneInfo = new VCardInfo.PhoneInfo();
                    phoneInfo.number=phoneData.data;
                    phoneInfo.type=phoneData.type;
                    phoneInfoList.add(phoneInfo);
                }

                VCardInfo info = new VCardInfo(contactStruct.name).setPhoneList(phoneInfoList);
                contactInfoList.add(info);
            }

            return contactInfoList;
        }
    }
}

