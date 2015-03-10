package com.li.hejion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.os.Handler;
import android.app.Activity;

import com.li.hejion.Utils.SharePreferenceUtil;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!SharePreferenceUtil.getPreference(WelcomeActivity.this,SharePreferenceUtil.SESSION_ID).equals("")){

                    HeApplication.SESSIONId_VALUE =SharePreferenceUtil.getPreference(WelcomeActivity.this,SharePreferenceUtil.SESSION_ID);
                    Log.e("se",HeApplication.SESSIONId_VALUE);
                    Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }else{
                    Intent intent=new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }



            }
        },3000);




    }


}
