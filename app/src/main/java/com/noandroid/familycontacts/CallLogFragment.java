package com.noandroid.familycontacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import static com.noandroid.familycontacts.MainActivity.*;


/**
 * Created by 关璐 on 2016/3/31.
 * Edited by leasunhy on 2016/5/9
 */
public class CallLogFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View mParent;

    private FragmentActivity mActivity;

    private TextView mText;

    public static CallLogFragment newInstance(int index) {
        CallLogFragment f = new CallLogFragment();

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
        View view = inflater.inflate(R.layout.fragment_records, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mParent=getView();

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

    @Override
    public void onResume() {
        super.onResume();
        updateListContent();
    }

    private void updateListContent() {
        String contactQueryStr =
                "SELECT " +
                    " contact._id as contactid, contact.name as display_name, count(*) as count," +
                    " max(record.time) as time, record.status, telephone.number " +
                " FROM " + " record join telephone join contact " +
                " WHERE record.telephone_number = telephone.number AND telephone.contact_id = contact._id " +
                " GROUP BY " + " contact._id";
        String telephoneQueryStr =
                "SELECT " +
                    " -1 as contactid, telephone_number as display_name, count(*) as count," +
                    " max(time) as time, status, telephone_number as number " +
                " FROM " + " record " +
                " WHERE " + " telephone_number not in (SELECT number FROM telephone) " +
                " GROUP BY " + " telephone_number";
        String queryStr =
                "SELECT _ROWID_ as _id, * " +
                " FROM " + "(" + contactQueryStr + " UNION ALL " + telephoneQueryStr + ")" + " ORDER BY " + "time DESC";
        //String[] from = { "display_name", "count", "record.time", "record.status", "telephone.number" };
        Log.d("SQL", contactQueryStr);
        Log.d("SQL", telephoneQueryStr);
        Log.d("SQL", queryStr);
        Cursor cursor = db.rawQuery(queryStr, null);
        CallLogCursorAdapter adapter = new CallLogCursorAdapter(getContext(), cursor, 0);
        ListView lv = (ListView)getView().findViewById(android.R.id.list);
        lv.setOnItemClickListener(this);
        lv.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.setClass(getContext(), ContactDetailsActivity.class);
        Bundle bundle = new Bundle();
        CallLogCursorAdapter.ViewHolder holder = (CallLogCursorAdapter.ViewHolder)view.getTag();
        Long contactid = holder.contactid;
        if (contactid < 0) {  // stranger
            bundle.putString("telephoneNum", holder.telephone);
        } else {
            bundle.putString("contactId", contactid.toString());
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

