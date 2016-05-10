package com.noandroid.familycontacts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by guanlu on 16/5/9.
 */

public class LoginDialog extends Dialog {

    private Context context;
    private String title;
    private ClickListenerInterface clickListenerInterface;
    private EditText login_name;
    private EditText login_password;
    private EditText register_name;
    private EditText register_password;


    public interface ClickListenerInterface {

        public void doLogin();

        public void doCancel();

        public void doTurn();

        public void doRegister();

    }

    public LoginDialog(Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(1);
    }

    public void init(int which) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view1 = inflater.inflate(R.layout.login_dialog, null);
        View view2 = inflater.inflate(R.layout.register_dialog,null);

        Animation mDisappearAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mDisappearAction.setDuration(500);
        Animation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        if (which ==1) {

            setContentView(view1);
            view1.startAnimation(mShowAction);
        }

        if(which ==2) {
            view1.setVisibility(View.INVISIBLE);
            view1.startAnimation(mDisappearAction);
            setContentView(view2);
            view1.startAnimation(mDisappearAction);
            view2.startAnimation(mShowAction);
        }
        login_name = (EditText) view1.findViewById(R.id.login_name);
        login_password = (EditText) view1.findViewById(R.id.login_password);

        register_name = (EditText) view2.findViewById(R.id.register_name);
        register_password = (EditText) view2.findViewById(R.id.register_password);

        Button login = (Button) view1.findViewById(R.id.login);
        Button register = (Button) view1.findViewById(R.id.register);

        Button register_ok = (Button) view2.findViewById(R.id.register_ok);
        Button register_cancel = (Button) view2.findViewById(R.id.register_cancel);

        login.setOnClickListener(new clickListener());
        register.setOnClickListener(new clickListener());
        register_ok.setOnClickListener(new clickListener());
        register_cancel.setOnClickListener(new clickListener());

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
                case R.id.login:
                    clickListenerInterface.doLogin();
                    break;
                case R.id.register:
                    clickListenerInterface.doTurn();
                    break;
                case R.id.register_ok:
                    clickListenerInterface.doRegister();
                    break;
                case R.id.register_cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

    public String getUserName() {
        return login_name.getText().toString();
    }

    public String getRigisterUserName() {
        return register_name.getText().toString();
    }

    public String getPassword() {
        return login_password.getText().toString();
    }

    public String getRegisterPassword() {
        return register_password.getText().toString();
    }
}

