package com.example.admin.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 关璐 on 2016/3/31.
 */
public class RecordsActivity extends Fragment {
    private View mParent;

    private FragmentActivity mActivity;

    private TextView mText;
    private List<Map<String,Object>> data;
    private ListView recordList;
    public static RecordsActivity newInstance(int index) {
        RecordsActivity f = new RecordsActivity();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;

    }
    static class ViewHolder {
        public ImageView img;
        public TextView name;
        public TextView time;
    }
    class RecordsAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;


        public RecordsAdapter(Context context) {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.record_list_item,null);
                holder.img = (ImageView)convertView.findViewById(R.id.record_img);
                holder.name = (TextView)convertView.findViewById(R.id.record_name);
                holder.time = (TextView)convertView.findViewById(R.id.record_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.img.setBackgroundResource((Integer)data.get(position).get("img"));
            holder.name.setText((String) data.get(position).get("name"));
            holder.time.setText((String)data.get(position).get("time"));
            return convertView;
        }

    }
    private List<Map<String,Object>> getData() {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String, Object> map;
        for(int i = 0; i< 10;i++) {
            map = new HashMap<String,Object>();
            map.put("img",R.drawable.button);
            map.put("name","name");
            map.put("time","April 5 23:00");
            list.add(map);
        }
        return list;
    }
    public int getShownIndex() {
        return getArguments().getInt("index",0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mParent=getView();
        data = getData();
        recordList = (ListView)mParent.findViewById(R.id.record_list);
        RecordsAdapter adapter = new RecordsAdapter(mActivity);
        recordList.setAdapter(adapter);
        //mText = (TextView)mParent.findViewById(R.id.fragment_current_task);

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
