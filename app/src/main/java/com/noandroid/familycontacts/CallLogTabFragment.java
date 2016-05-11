package com.noandroid.familycontacts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.noandroid.familycontacts.model.*;
import de.greenrobot.dao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by leasunhy on 5/11/16.
 */
public class CallLogTabFragment extends Fragment {
    private RecyclerView mRootView;

    public static CallLogTabFragment newInstance(String contactId, String telNum) {
        Bundle args = new Bundle();
        args.putString("contactId", contactId);
        args.putString("telNum", telNum);
        CallLogTabFragment fragment = new CallLogTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (RecyclerView) inflater.inflate(R.layout.fragment_record_details, container, false);
        return mRootView;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView rv = (RecyclerView) mRootView.findViewById(android.R.id.list);
        rv.setHasFixedSize(true);
        Bundle args = getArguments();
        String contactIdStr = args.getString("contactId");
        RecordDao recordDao = DatabaseHelper.getDaoMaster(getContext()).newSession().getRecordDao();
        if (contactIdStr != null) {  // known contacts
            Long contactId = Long.parseLong(contactIdStr);
            if (contactId == null) return;
            QueryBuilder<Record> query = recordDao.queryBuilder();
            query.join(RecordDao.Properties.TelephoneNumber, Telephone.class, TelephoneDao.Properties.Number)
                    .where(TelephoneDao.Properties.ContactId.eq(contactId));
            List<Record> records = query.build().list();
            rv.setAdapter(new RecordRecyclerViewAdapter(getContext(), records));
        } else {  // strangers
            String telNum = args.getString("telNum");
            List<Record> records = recordDao.queryBuilder()
                    .where(RecordDao.Properties.TelephoneNumber.eq(telNum)).build().list();
            rv.setAdapter(new RecordRecyclerViewAdapter(getContext(), records));
        }
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
            holder.icon.setImageResource(CallLogCursorAdapter.getIconIdForCallType(record.getStatus()));
            holder.phone_number.setText(record.getTelephoneNumber());
            holder.date.setText(formatDate(record.getTime()));
            holder.duration.setText(formatDuration(record.getDuration()));
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView phone_number;
            public TextView date;
            public TextView duration;

            public ViewHolder(View view) {
                super(view);
                icon = (ImageView)view.findViewById(R.id.icon);
                phone_number = (TextView)view.findViewById(R.id.phone_number);
                date = (TextView)view.findViewById(R.id.date);
                duration = (TextView)view.findViewById(R.id.duration);
            }
        }
    }
}
