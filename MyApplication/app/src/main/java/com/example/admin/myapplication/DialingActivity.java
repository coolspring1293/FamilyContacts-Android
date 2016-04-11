package com.example.admin.myapplication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 关璐 on 2016/3/31.
 */
public class DialingActivity extends Fragment {
    private View mParent;

    private FragmentActivity mActivity;

    private TextView mText;
    private Button tab_1,tab_2,tab_3,tab_4,tab_5,tab_6,tab_7,tab_8,tab_9,tab_0,tab_star,tab_jing,tab_call,back;
    private TextView tel;
    public static DialingActivity newInstance(int index) {
        DialingActivity f = new DialingActivity();

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
        View view = inflater.inflate(R.layout.fragment_dialing, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParent=getView();
        mActivity = getActivity();

        tab_1 = (Button)mParent.findViewById(R.id.tab_1);
        tab_2 = (Button)mParent.findViewById(R.id.tab_2);
        tab_3 = (Button)mParent.findViewById(R.id.tab_3);
        tab_4 = (Button)mParent.findViewById(R.id.tab_4);
        tab_5 = (Button)mParent.findViewById(R.id.tab_5);
        tab_6 = (Button)mParent.findViewById(R.id.tab_6);
        tab_7 = (Button)mParent.findViewById(R.id.tab_7);
        tab_8 = (Button)mParent.findViewById(R.id.tab_8);
        tab_9 = (Button)mParent.findViewById(R.id.tab_9);
        tab_0 = (Button)mParent.findViewById(R.id.tab_0);
        tab_star = (Button)mParent.findViewById(R.id.tab_star);
        tab_jing = (Button)mParent.findViewById(R.id.tab_jing);
        tab_call = (Button)mParent.findViewById(R.id.tab_call);
        back = (Button)mParent.findViewById(R.id.back);
        tel = (TextView)mParent.findViewById(R.id.text_show);
        tab_1.setOnClickListener(new tabClick());
        tab_2.setOnClickListener(new tabClick());
        tab_3.setOnClickListener(new tabClick());
        tab_4.setOnClickListener(new tabClick());
        tab_5.setOnClickListener(new tabClick());
        tab_6.setOnClickListener(new tabClick());
        tab_7.setOnClickListener(new tabClick());
        tab_8.setOnClickListener(new tabClick());
        tab_9.setOnClickListener(new tabClick());
        tab_0.setOnClickListener(new tabClick());
        tab_call.setOnClickListener(new tabClick());
        tab_jing.setOnClickListener(new tabClick());
        tab_star.setOnClickListener(new tabClick());
        back.setOnClickListener(new tabClick());

    }
    private class tabClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String temp = "";
            if(v.getId() ==R.id.tab_1) {
                temp = tel.getText().toString() + "1";
            }
            if(v.getId() ==R.id.tab_2) {
                temp = tel.getText().toString() + "2";
            }
            if(v.getId() ==R.id.tab_3) {
                temp = tel.getText().toString() + "3";
            }
            if(v.getId() ==R.id.tab_4) {
                temp = tel.getText().toString() + "4";
            }
            if(v.getId() ==R.id.tab_5) {
                temp = tel.getText().toString() + "5";
            }
            if(v.getId() ==R.id.tab_6) {
                temp = tel.getText().toString() + "6";
            }
            if(v.getId() ==R.id.tab_7) {
                temp = tel.getText().toString() + "7";
            }if(v.getId() ==R.id.tab_8) {
                temp = tel.getText().toString() + "8";
            }
            if(v.getId() ==R.id.tab_9) {
                temp = tel.getText().toString() + "9";
            }if(v.getId() ==R.id.tab_0) {
                temp = tel.getText().toString() + "0";
            }
            if(v.getId() ==R.id.tab_star) {
                temp = tel.getText().toString() + "*";
            }
            if(v.getId() ==R.id.tab_jing) {
                temp = tel.getText().toString() + "#";
            }
            if(v.getId() ==R.id.back) {
                if(tel.getText().toString().length()!=0) {
                    Toast.makeText(getActivity(),"into",Toast.LENGTH_SHORT).show();
                    temp = tel.getText().toString();
                    temp.substring(0, temp.length() - 1);
                }
            }
            if(v.getId() ==R.id.tab_call) {
                if(tel.getText().toString().length()!= 0 ) {
                    Toast.makeText(getActivity(),tel.getText().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + tel.getText().toString()));
                    startActivity(intent);
                } else {
                    //Toast.makeText(getActivity(),tel.getText().toString().length(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(),"请输入号码",Toast.LENGTH_SHORT).show();
                }
            }
            tel.setText(temp);

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
