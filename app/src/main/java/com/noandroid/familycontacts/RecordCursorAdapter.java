package com.noandroid.familycontacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.noandroid.familycontacts.model.Telephone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by leasunhy on 5/9/16.
 */
public class RecordCursorAdapter extends CursorAdapter {
    private LayoutInflater inflater;
    private Context context;
    private static SimpleDateFormat dateOnlyFormatter = new SimpleDateFormat("yyyy-MM-d", Locale.ENGLISH);
    private static SimpleDateFormat timeOnlyFormatter = new SimpleDateFormat("h:m a", Locale.ENGLISH);
    private static SimpleDateFormat datetimeFormatter = new SimpleDateFormat("MMM d h:m a", Locale.ENGLISH);

    private static Calendar dateOnlyCalendar(Calendar cal) {
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal;
    }

    private static String formatDate(Date date) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        dateOnlyCalendar(yesterday);
        if (date.before(yesterday.getTime()))
            return dateOnlyFormatter.format(date);
        if (date.after(dateOnlyCalendar(Calendar.getInstance()).getTime()))
            return timeOnlyFormatter.format(date);
        return "Yesterday " + timeOnlyFormatter.format(date);
    }

    private static int getIconIdForCallType(int type) {
        switch (type) {
            case CallLog.Calls.MISSED_TYPE:
                return R.drawable.ic_call_missed_24dp;
            case CallLog.Calls.INCOMING_TYPE:
                return R.drawable.ic_call_received_24dp;
            case CallLog.Calls.OUTGOING_TYPE:
                return R.drawable.ic_call_made_24dp;
        }
        // impossible
        return R.drawable.ic_call_made_24dp;
    }

    RecordCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.record_list_item, viewGroup, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        Long contactid = cursor.getLong(cursor.getColumnIndex("contactid"));
        holder.contactid = contactid;

        // TODO(leasunhy): set image
        int status = cursor.getInt(cursor.getColumnIndex("status"));
        holder.icon.setImageResource(getIconIdForCallType(status));

        String displayName = cursor.getString(cursor.getColumnIndex("display_name"));
        holder.display_name.setText(displayName);

        String telNum = cursor.getString(cursor.getColumnIndex("number"));
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        String info = "";
        if (count > 1)
            info += String.format("(%d)", count);
        if (contactid >= 0)  // if not stranger
            info += " " + telNum;
        holder.phone_number.setText(info);
        holder.telephone = telNum;

        Date date = new Date(cursor.getLong(cursor.getColumnIndex("time")) * 1000);
        holder.date.setText(formatDate(date));

        holder.location.setText(Telephone.getLocationForTel(telNum));
    }

    public class ViewHolder {
        public ImageView icon;
        public TextView display_name;
        public TextView phone_number;
        public TextView date;
        public TextView location;

        public Long contactid;
        public String telephone;

        ViewHolder(View view) {
            icon = (ImageView)view.findViewById(R.id.icon);
            phone_number = (TextView)view.findViewById(R.id.phone_number);
            display_name = (TextView)view.findViewById(R.id.display_name);
            location = (TextView)view.findViewById(R.id.location);
            date = (TextView)view.findViewById(R.id.date);
        }
    }
}
