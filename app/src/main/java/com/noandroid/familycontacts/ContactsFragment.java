package com.noandroid.familycontacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by 关璐 on 2016/3/31.
 * Modified by liuw53 on 2016/5/1.
 */
public class ContactsFragment extends Fragment {

    private View mParent;
    private FragmentActivity mActivity;
    private List<Map<String, Object>> data;
    private StickyListHeadersListView stickyList;
    private final int REQUESTCODE = 1;
    private ImageButton button_add_contact;
    //search
    private ImageButton button_search_contact;
    private AutoCompleteTextView search_text;
    private List<String> index = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();


    public static ContactsFragment newInstance(int index) {
        ContactsFragment f = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        return view;
    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        nameList.clear();
        index.clear();
        Cursor cursor = MainActivity.db.query(MainActivity.contactDao.getTablename(), MainActivity.contactDao.getAllColumns(), null, null, null, null, "pinyin ASC");
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", cursor.getString(cursor.getColumnIndex("NAME")));
            map.put("title", cursor.getString(cursor.getColumnIndex("RELATIONSHIP")));
            map.put("id", cursor.getString(cursor.getColumnIndex("_id")));
            map.put("pinyin", cursor.getString(cursor.getColumnIndex("PINYIN")));
            list.add(map);
            nameList.add(cursor.getString(cursor.getColumnIndex("NAME")));
            index.add(map.get("name").toString());
        }
        return list;
    }


    static class ViewHoder {
        public TextView name;
        public TextView title;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;

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
            if (converView == null) {
                hoder = new ViewHoder();

                converView = mInflater.inflate(R.layout.list_item, null);
                hoder.name = (TextView) converView.findViewById(R.id.contact_name);
                hoder.title = (TextView) converView.findViewById(R.id.contact_title);
                converView.setTag(hoder);
            } else {
                hoder = (ViewHoder) converView.getTag();
            }
            hoder.name.setText((String) data.get(position).get("name"));
            hoder.title.setText((String) data.get(position).get("title"));
            return converView;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //MainActivity.contactDao.deleteAll();

        button_add_contact = (ImageButton) getView().findViewById(R.id.contact_add);

        button_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*addContact();
                Snackbar.make(view, "Added contact", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                Intent intent = new Intent();
                intent.setClass(getActivity(), EditContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("contactId", null);
                intent.putExtras(bundle);
                //startActivityForResult(intent, REQUESTCODE);
                startActivity(intent);

            }
        });

        button_search_contact = (ImageButton) getView().findViewById(R.id.contact_search);
        search_text = (AutoCompleteTextView) getView().findViewById(R.id.search_text);


        button_search_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_text.getVisibility() == View.GONE) {
                    search_text.setVisibility(View.VISIBLE);

                } else {
                    search_text.setVisibility(View.GONE);

                }
            }
        });


        search_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView listview = (ListView) parent;
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                TextView textview = (TextView) view;

                //Toast.makeText(getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                String name = parent.getItemAtPosition(position).toString();
                int a = index.indexOf(name);
                Log.d("INDEX", String.valueOf(a));
                stickyList.setSelection(a);
            }
        });
    }

    /**
     * 更新数据
     */
    private void updateData() {
        data = getData();
        stickyList = (StickyListHeadersListView) getView().findViewById(R.id.test_list);
        MyTextAdapter adapter = new MyTextAdapter(getActivity());
        stickyList.setAdapter(adapter);

        ArrayAdapter names = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, nameList);

        search_text.setAdapter(names);
        search_text.setThreshold(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        search_text.setText("");
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
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

            holder.name.setText((String) data.get(position).get("name"));
            holder.title.setText((String) data.get(position).get("title"));
            stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String contactName = data.get(position).get("name").toString();
                    String contactId = data.get(position).get("id").toString();
                    Intent intent = new Intent();
                    // intent.setClass(getActivity(), DetailsActivity.class);
                    intent.setClass(getActivity(), ContactDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("telephoneNum", contactName);
                    bundle.putString("contactId", contactId);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUESTCODE);
                    //startActivity(intent);
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
            if (data.get(position).get("pinyin") != null) {
                String headerText = "" + Character.toUpperCase(data.get(position).get("pinyin").toString().subSequence(0, 1).charAt(0));
                holder.text.setText(headerText);
            }

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return data.get(position).get("pinyin").toString().subSequence(0, 1).charAt(0);
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
