package com.li.hejion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.li.hejion.HttpEntity.DefaultMessageResponse;
import com.li.hejion.HttpEntity.LoginResponse;
import com.li.hejion.HttpEntity.ServerHelper;
import com.li.hejion.Utils.DialogUtils;
import com.li.hejion.Utils.HttpUtils;
import com.li.hejion.Utils.SharePreferenceUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener{

    Button loginBtn;
    EditText userEdt,psdEdt;
    CheckBox remerCb;

    DefaultMessageResponse<LoginResponse> loginResult;
    List<NameValuePair> param;
    LoginAsync async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        init();
        initView();
    }

    private void init(){
        param=new ArrayList<NameValuePair>();
    }

    private void initView(){
        loginBtn=(Button)findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        userEdt=(EditText)findViewById(R.id.login_username_edt);
        psdEdt=(EditText)findViewById(R.id.login_psd_edt);
        remerCb=(CheckBox)findViewById(R.id.remerber_agree_cb);

        if(Build.VERSION.SDK_INT>=16){
            initEdt();
        }
    }

    private void initEdt(){
        userEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String key=userEdt.getText().toString().trim();
                if (key.equals("")){
                    userEdt.setBackground(getResources().getDrawable(R.drawable.login_username_edt_normal));
                }else{
                    userEdt.setBackground(getResources().getDrawable(R.drawable.login_username_edt_pressed));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        psdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String key=psdEdt.getText().toString().trim();
                if (key.equals("")){
                    psdEdt.setBackground(getResources().getDrawable(R.drawable.login_psd_edt_normal));
                }else{
                    psdEdt.setBackground(getResources().getDrawable(R.drawable.login_psd_edt_pressed));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:

                DialogUtils.showWaitDialog(LoginActivity.this);

                if (TextUtils.isEmpty(userEdt.getText().toString().trim())||TextUtils.isEmpty(psdEdt.getText().toString().trim())){
                    return;
                }

                param.add(new BasicNameValuePair(HeApplication.USERNAME_KEY,userEdt.getText().toString().trim()));
                param.add(new BasicNameValuePair(HeApplication.PASSWORD_KEY,psdEdt.getText().toString().trim()));
                param.add(new BasicNameValuePair(HeApplication.PDID_KEY,HeApplication.PDID_VALUE));


                if (async != null && async.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    async.cancel(true);
                }

                async=new LoginAsync();
                async.execute();


                break;
        }
    }

    public class LoginAsync extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {

            loginResult= ServerHelper.getResponseEntity(new LoginResponse(),1,param, HttpUtils.getLoginUrl());


            if (loginResult!=null){
                return loginResult.getBody().getErrorcode();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("")) {
                Toast.makeText(LoginActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                DialogUtils.cancelWaitDialog();
                return;
            }
            if (s.equals("0")){

                if (remerCb.isChecked()){
                    SharePreferenceUtil.setPreference(LoginActivity.this,SharePreferenceUtil.USERNAME,userEdt.getText().toString().trim());
                    SharePreferenceUtil.setPreference(LoginActivity.this,SharePreferenceUtil.PASSWORD,psdEdt.getText().toString().trim());
                    SharePreferenceUtil.setPreference(LoginActivity.this,SharePreferenceUtil.SESSION_ID,loginResult.getBody().getSessionId());
                }

                HeApplication.SESSIONId_VALUE =loginResult.getBody().getSessionId();

                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this,loginResult.getBody().getMassage(),Toast.LENGTH_LONG).show();
            }

            DialogUtils.cancelWaitDialog();
        }
    }


}
