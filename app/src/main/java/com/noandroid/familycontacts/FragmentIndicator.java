package com.noandroid.familycontacts;

import android.content.Context;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;


/**
 * Created by 关璐 on 2016/3/31.
 */
public class FragmentIndicator extends LinearLayout implements OnClickListener{
    private int mDefaultIndicator = 0;

    private static int mCurIndicator;

    private static View[] mIndicators;

    private OnIndicateListener mOnIndicateListener;

    private static final String TAG_TEXT_0 = "text_tag_0";
    private static final String TAG_TEXT_1 = "text_tag_1";
    private static final String TAG_TEXT_2 = "text_tag_2";

    private static final String TAG_ICON_0 = "icon_tag_0";
    private static final String TAG_ICON_1 = "icon_tag_1";
    private static final String TAG_ICON_2 = "icon_tag_2";

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_GRAY = 0xFF888888;
//    private static final int COLOR_DARK =  0xFF093D5A;
    private static final int COLOR_DARK = 0xFF28463E;

    private static final int HIGH = 120;

    FragmentIndicator(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public FragmentIndicator(Context context,AttributeSet attrs) {
        super(context,attrs);

        mCurIndicator = mDefaultIndicator;
        setOrientation(LinearLayout.HORIZONTAL);
        init();

    }


    private View createIndicator(int stringResID, int stringColor,String textTag,
                                 String iconTag,int iconResID){
        LinearLayout view = new LinearLayout(getContext());
        view.setOrientation(LinearLayout.VERTICAL);

        view.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,HIGH,1));
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        //icon
        ImageView iconView = new ImageView(getContext());
        iconView.setTag(iconTag);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, 1));
        iconView.setImageResource(iconResID);
        iconView.setPadding(0, 3, 0, -7);
        //text
        TextView textView = new TextView(getContext());
        textView.setTag(textTag);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
        textView.setTextColor(COLOR_GRAY);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setText(stringResID);
        textView.setPadding(0,0,0,0);

        view.addView(iconView);
        view.addView(textView);
        return view;
    }


    private void init() {
        mIndicators = new View[3];
        //默认第一个界面
        mIndicators[0] = createIndicator(R.string.tab_contacts,COLOR_DARK,TAG_TEXT_0,
                TAG_ICON_0,R.drawable.ic_contacts_24dp);
        mIndicators[0].setBackgroundColor(COLOR_DARK);
        mIndicators[0].setTag(Integer.valueOf(0));
        mIndicators[0].setOnClickListener(this);
        TextView text=(TextView)mIndicators[0].findViewWithTag(TAG_TEXT_0);
        text.setTextColor(Color.LTGRAY);
        addView(mIndicators[0]);

        mIndicators[1] = createIndicator(R.string.tab_records,COLOR_DARK,TAG_TEXT_1,
                TAG_ICON_1,R.drawable.ic_recent_24dp);
        mIndicators[1].setBackgroundColor(COLOR_WHITE);
        mIndicators[1].setTag(Integer.valueOf(1));
        mIndicators[1].setOnClickListener(this);
        addView(mIndicators[1]);


        mIndicators[2] = createIndicator(R.string.tab_dialing,COLOR_DARK,TAG_TEXT_2,
                TAG_ICON_2,R.drawable.ic_phone_24dp);
        mIndicators[2].setBackgroundColor(COLOR_WHITE);
        mIndicators[2].setTag(Integer.valueOf(2));
        mIndicators[2].setOnClickListener(this);
        addView(mIndicators[2]);

    }

    public static void setIndicator(int which) {
        mIndicators[mCurIndicator].setBackgroundColor(COLOR_WHITE);
        TextView prevText;
        ImageView prevIcon;

        switch(mCurIndicator) {
            case 0:
                prevText=(TextView)mIndicators[mCurIndicator].findViewWithTag(TAG_TEXT_0);
                prevIcon=(ImageView)mIndicators[mCurIndicator].findViewWithTag(TAG_ICON_0);
                prevText.setTextColor(COLOR_GRAY);
                prevIcon.setImageResource(R.drawable.ic_contacts_24dp);
                break;
            case 1:
                prevText=(TextView)mIndicators[mCurIndicator].findViewWithTag(TAG_TEXT_1);
                prevIcon=(ImageView)mIndicators[mCurIndicator].findViewWithTag(TAG_ICON_1);
                prevText.setTextColor(COLOR_GRAY);
                prevIcon.setImageResource(R.drawable.ic_recent_24dp);
                break;
            case 2:
                prevText=(TextView)mIndicators[mCurIndicator].findViewWithTag(TAG_TEXT_2);
                prevIcon=(ImageView)mIndicators[mCurIndicator].findViewWithTag(TAG_ICON_2);
                prevText.setTextColor(COLOR_GRAY);
                prevIcon.setImageResource(R.drawable.ic_phone_24dp);
                break;
        }


        mIndicators[which].setBackgroundColor(COLOR_DARK);
        TextView curText;
        ImageView curIcon;

        switch(which) {
            case 0:
                curText=(TextView)mIndicators[which].findViewWithTag(TAG_TEXT_0);
                curIcon=(ImageView)mIndicators[which].findViewWithTag(TAG_ICON_0);
                curText.setTextColor(COLOR_WHITE);
                curIcon.setImageResource(R.drawable.ic_contacts_24dp_black);
                break;
            case 1:
                curText=(TextView)mIndicators[which].findViewWithTag(TAG_TEXT_1);
                curIcon=(ImageView)mIndicators[which].findViewWithTag(TAG_ICON_1);
                curText.setTextColor(COLOR_WHITE);
                curIcon.setImageResource(R.drawable.ic_recent_24dp_black);
                break;
            case 2:
                curText=(TextView)mIndicators[which].findViewWithTag(TAG_TEXT_2);
                curIcon=(ImageView)mIndicators[which].findViewWithTag(TAG_ICON_2);
                curText.setTextColor(COLOR_WHITE);
                curIcon.setImageResource(R.drawable.ic_phone_24dp_black);
                break;
        }
        mCurIndicator = which;
    }


    public interface OnIndicateListener {
        public void OnIndicate(View v,int which);
    }

    public void setOnIndicateListener(OnIndicateListener listener) {
        mOnIndicateListener = listener;
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(mOnIndicateListener !=null) {
            int tag = (Integer)v.getTag();
            switch(tag) {
                case 0:
                    if(mCurIndicator !=0) {
                        mOnIndicateListener.OnIndicate(v, 0);
                        setIndicator(0);
                    }
                    break;

                case 1:
                    if(mCurIndicator !=1) {
                        mOnIndicateListener.OnIndicate(v, 1);
                        setIndicator(1);
                    }
                    break;

                case 2:
                    if(mCurIndicator !=2) {
                        mOnIndicateListener.OnIndicate(v, 2);
                        setIndicator(2);
                    }
                    break;

                default:

                    break;
            }
        }

    }


}
