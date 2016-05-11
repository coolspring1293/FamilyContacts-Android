package com.noandroid.familycontacts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by guanlu on 16/5/9.
 */

public class SMSDialog extends Dialog {

    private Context context;
    private String title;
    private int _id =1;
    private ClickListenerInterface clickListenerInterface;
    private List<String> tels;
    private String targetTel ="";


    public interface ClickListenerInterface {

        public void doCancel();

        public void doSend();

    }

    public SMSDialog(Context context, List<String> tel,int id) {
        super(context);
        this.context = context;
        tels = tel;
        _id = id;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public String getTargetTel() {
        return targetTel;
    }
    public void init() {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.sms_dialog, null);
        setContentView(view);

        ListView telsList = (ListView)view.findViewById(R.id.call_list);

        ArrayAdapter names = new ArrayAdapter(context,android.R.layout.simple_list_item_1,tels);

        telsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                targetTel = parent.getItemAtPosition(position).toString();
            }
        });

        telsList.setAdapter(names);
        Button cancel = (Button) view.findViewById(R.id.sms_cancel);

        Button send = (Button) view.findViewById(R.id.sms_send);

        if(_id == 0 ) {
            send.setText("CALL");
        }
        cancel.setOnClickListener(new clickListener());
        send.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        dialogWindow.setAttributes(lp);


    }

    public void setClickListener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.sms_send:
                    clickListenerInterface.doSend();
                    break;
                case R.id.sms_cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

}

