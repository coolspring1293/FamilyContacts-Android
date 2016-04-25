package com.example.admin.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.model.Contact;
import com.example.admin.myapplication.model.ContactDao;
import com.example.admin.myapplication.model.DaoMaster;
import com.example.admin.myapplication.model.DaoSession;
import com.example.admin.myapplication.model.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by 关璐 on 2016/3/31.
 */
public class ContactsActivity extends Fragment{

    private View mParent;

    private FragmentActivity mActivity;

    private List<Map<String,Object>> data;

    private StickyListHeadersListView stickyList;

    private final int REQUESTCODE=1;

    public static ContactsActivity newInstance(int index) {
        ContactsActivity f = new ContactsActivity();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;

    }

    public int getShownIndex() {
        return getArguments().getInt("index",0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);



        return view;
    }

    private List<Map<String,Object>> getData() {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Cursor cursor = MainActivity.db.query(MainActivity.contactDao.getTablename(), MainActivity.contactDao.getAllColumns(), null, null, null, null, null);

        while(cursor.moveToNext()) {
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("name",cursor.getString(cursor.getColumnIndex("NAME")));
            map.put("title",cursor.getString(cursor.getColumnIndex("RELATIONSHIP")));
            map.put("id",cursor.getString(cursor.getColumnIndex("_id")));
            list.add(map);
        }

        return list;
    }

    private void addContactA() {
        Contact contactA = new Contact(null, "Guan Lu ", "Me", "");
        MainActivity.contactDao.insert(contactA);
        Log.d("Model", "Inserted new Contact, ID: " + contactA.getId());
    }
    private void addContactB() {
        Contact contactB = new Contact(null, "Liu Wang ", "Group Leader", "");
        MainActivity.contactDao.insert(contactB);
        Log.d("Model", "Inserted new Contact, ID: " + contactB.getId());
    }
    private void addContactC() {
        Contact contactB = new Contact(null, "Liu Siyuan ", "Friend", "");
        MainActivity.contactDao.insert(contactB);
        Log.d("Model", "Inserted new Contact, ID: " + contactB.getId());
    }


    static class ViewHoder {
        public TextView name;
        public TextView title;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater =null;

        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            ViewHoder hoder = null;
            if(converView == null) {
                hoder = new ViewHoder();

                converView = mInflater.inflate(R.layout.list_item,null);
                hoder.name = (TextView)converView.findViewById(R.id.contact_name);
                hoder.title=(TextView)converView.findViewById(R.id.contact_title);
                converView.setTag(hoder);
            } else {
                hoder=(ViewHoder)converView.getTag();
            }
            hoder.name.setText((String)data.get(position).get("name"));
            hoder.title.setText((String) data.get(position).get("title"));
            return converView;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.contactDao.deleteAll();
        addContactA();
        addContactA();
        addContactA();
        addContactA();
        addContactB();
        addContactB();
        addContactB();
        addContactB();
        addContactB();
        addContactC();
        addContactC();
        addContactC();
        addContactC();
        addContactC();
        addContactC();
        addContactC();

        data = getData();
        stickyList = (StickyListHeadersListView)getView().findViewById(R.id.test_list);
        MyTextAdapter adapter = new MyTextAdapter(getActivity());

        stickyList.setAdapter(adapter);


    }
    public class MyTextAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;

        public MyTextAdapter(Context context) {
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.contact_name);
                holder.title = (TextView) convertView.findViewById(R.id.contact_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText((String)data.get(position).get("name"));
            holder.title.setText((String)data.get(position).get("title"));
            stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String contactName = data.get(position).get("name").toString();
                    String contactId = data.get(position).get("id").toString();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("contactName",contactName);
                    bundle.putString("contactId",contactId);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUESTCODE);

                }
            });
            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.header, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            String headerText = "" + data.get(position).get("name").toString().subSequence(0, 1).charAt(0);
            holder.text.setText(headerText);

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return data.get(position).get("name").toString().subSequence(0, 1).charAt(0);
        }

        class HeaderViewHolder {
            TextView text;
        }

        class ViewHolder {
            TextView name;
            TextView title;
        }

    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
