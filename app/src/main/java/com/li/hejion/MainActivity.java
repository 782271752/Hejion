package com.li.hejion;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.li.hejion.HttpEntity.DefaultMessageResponse;
import com.li.hejion.HttpEntity.ServerHelper;
import com.li.hejion.HttpEntity.UserInfoResponse;
import com.li.hejion.Utils.DialogUtils;
import com.li.hejion.Utils.HttpUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    ImageButton writeBtn,readBtn,infoBtn,backBtn,exitBtn,headBtn,editBtn;
    DefaultMessageResponse<UserInfoResponse> userInfoResult;
    TextView nameTv,areaTv,introduceTv;
    UserInfoAsync async=null;
    Context context;
    List<NameValuePair> parm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!HeApplication.HEAD_IMG.equals("")){
            HeApplication.imageLoader.displayImage(HeApplication.HEAD_IMG,headBtn,
                    HeApplication.getOptions()); //设置头像
            nameTv.setText(HeApplication.NAME);
//            sexTv.setText(HeApplication.SEX);
            areaTv.setText(HeApplication.AREA);
            introduceTv.setText(HeApplication.INTRODUCE);
        }else{
            DialogUtils.showWaitDialog(this);

            if (async != null && async.getStatus().equals(AsyncTask.Status.RUNNING)) {
                async.cancel(true);
            }
            async=new UserInfoAsync();
            async.execute();
        }
    }

    private void initView(){
        context=this;
        writeBtn=(ImageButton)findViewById(R.id.write_meeage_btn);
        writeBtn.setOnClickListener(this);

        readBtn=(ImageButton)findViewById(R.id.main_read_message_btn);
        readBtn.setOnClickListener(this);

        infoBtn=(ImageButton)findViewById(R.id.main_info_btn);
        infoBtn.setOnClickListener(this);

        backBtn=(ImageButton)findViewById(R.id.back);
        backBtn.setOnClickListener(this);

        exitBtn=(ImageButton)findViewById(R.id.main_exit_btn);
        exitBtn.setOnClickListener(this);

        editBtn=(ImageButton)findViewById(R.id.main_btn_edit);
        editBtn.setOnClickListener(this);

        headBtn=(ImageButton)findViewById(R.id.main_head_btn);
        nameTv=(TextView)findViewById(R.id.main_name);
        areaTv=(TextView)findViewById(R.id.main_city);
        introduceTv=(TextView)findViewById(R.id.main_introduce);

        parm=new ArrayList<NameValuePair>();
        parm.add(new BasicNameValuePair("se",HeApplication.SESSIONId_VALUE));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.write_meeage_btn:
                Intent intent=new Intent(MainActivity.this,WriteMessActivity.class);

                startActivity(intent);
                break;

            case R.id.main_info_btn:
            case R.id.main_btn_edit:
                intent=new Intent(MainActivity.this,InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.main_exit_btn:
                intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                HeApplication.HEAD_IMG="";
                this.finish();
                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.main_read_message_btn:
                intent=new Intent(MainActivity.this,ReadMessActivity.class);
                intent.putExtra("url",HttpUtils.WEB_URL+"?se="+HeApplication.SESSIONId_VALUE);
                Log.e("url", HttpUtils.WEB_URL+"?se="+HeApplication.SESSIONId_VALUE);
                startActivity(intent);
                break;
            case R.id.main_head_btn:

                break;

        }
    }

    /**
     * 异步获取用户信息
     */
    public class UserInfoAsync extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            userInfoResult= ServerHelper.getResponseEntity(new UserInfoResponse(), 1, parm, HttpUtils.getUserinfoUrl());
            if(userInfoResult!=null){
                return userInfoResult.getBody().getErrorcode();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("0")){
                HeApplication.imageLoader.displayImage(userInfoResult.getBody().getUserInfoResult().getHeading(),headBtn,
                        HeApplication.getOptions()); //设置头像
                nameTv.setText(userInfoResult.getBody().getUserInfoResult().getNickname());

                if (userInfoResult.getBody().getUserInfoResult().getSex().equals("1")){
//                    sexTv.setText("男");
                    HeApplication.SEX="男";
                }else if(userInfoResult.getBody().getUserInfoResult().getSex().equals("2")){
//                    sexTv.setText("女");
                    HeApplication.SEX="女";
                }else{
//                    sexTv.setText("未知");
                    HeApplication.SEX="未知";
                }

                areaTv.setText(userInfoResult.getBody().getUserInfoResult().getArea());
                introduceTv.setText(userInfoResult.getBody().getUserInfoResult().getIntroduction());

                HeApplication.HEAD_IMG=userInfoResult.getBody().getUserInfoResult().getHeading();
                HeApplication.NAME=userInfoResult.getBody().getUserInfoResult().getNickname();
//                HeApplication.SEX=sexTv.getText().toString();
                HeApplication.AREA=userInfoResult.getBody().getUserInfoResult().getArea();
                HeApplication.INTRODUCE=userInfoResult.getBody().getUserInfoResult().getIntroduction();

            }else if(s.equals("")){
                Toast.makeText(context, "网络连接错误", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,userInfoResult.getBody().getMassage(),Toast.LENGTH_LONG).show();
            }

            DialogUtils.cancelWaitDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HeApplication.HEAD_IMG="";
    }
}
