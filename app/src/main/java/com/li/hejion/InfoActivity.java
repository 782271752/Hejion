package com.li.hejion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.li.hejion.HttpEntity.DefaultMessageResponse;
import com.li.hejion.HttpEntity.ServerHelper;
import com.li.hejion.HttpEntity.UpdateResponse;
import com.li.hejion.HttpEntity.UserInfoResponse;
import com.li.hejion.Utils.DialogUtils;
import com.li.hejion.Utils.HttpPostEmulator;
import com.li.hejion.Utils.HttpUtils;
import com.li.hejion.Utils.ImgSelectUtil;
import com.li.hejion.Utils.UploadFileItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class InfoActivity extends Activity implements View.OnClickListener{

    ImageButton backBtn;
    List<NameValuePair> parm;
    DefaultMessageResponse<UserInfoResponse> userInfoResult;
    Context context;

    UserInfoAsync async=null;
    private ImageView headImg;
    private EditText nameTv,sexTv,areaTv,introduceTv;

    TextView changeImgTv;
    private AsyncImg asyncImg=null;
    private Button saveBtn;

    private DefaultMessageResponse<UpdateResponse> result;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_info);
            init();
            initView();
        }

        @Override
        protected void onResume() {
            super.onResume();

            if (!HeApplication.HEAD_IMG.equals("")){
                HeApplication.imageLoader.displayImage(HeApplication.HEAD_IMG,headImg,
                        HeApplication.getOptions()); //设置头像
                nameTv.setText(HeApplication.NAME);
                sexTv.setText(HeApplication.SEX);
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

    private void init(){
        context=this;
        parm=new ArrayList<NameValuePair>();
        parm.add(new BasicNameValuePair("se",HeApplication.SESSIONId_VALUE));
    }

    private void initView(){
        backBtn=(ImageButton)findViewById(R.id.back);
        backBtn.setOnClickListener(this);

        headImg=(ImageView)findViewById(R.id.info_head_img);
        nameTv=(EditText)findViewById(R.id.info_name);
        sexTv=(EditText)findViewById(R.id.info_sex);
        areaTv=(EditText)findViewById(R.id.info_area);
        introduceTv=(EditText)findViewById(R.id.info_introduce);
        changeImgTv=(TextView)findViewById(R.id.info_change_img);
        changeImgTv.setOnClickListener(this);

        saveBtn=(Button)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.info_change_img:
                showDialog(0);
                break;
            case R.id.save_btn:
                List<NameValuePair> pairs=new ArrayList<NameValuePair>();


                String sex="";
                if(sexTv.getText().toString().trim().equals("男")){
                    sex="1";
                }else if(sexTv.getText().toString().trim().equals("女")){
                    sex="2";
                }else{
                    sex="0";
                }
                DialogUtils.showWaitDialog(context);
                String updateUrl="?nickname="+nameTv.getText().toString().trim()+"&sex="+sex+"&area="+areaTv.getText().toString().trim()
                        +"&introduction="+introduceTv.getText().toString().trim()+"&se="+HeApplication.SESSIONId_VALUE+"&headimg="+HeApplication.HEAD_IMG;
//                pairs.add(new BasicNameValuePair("nickname",nameTv.getText().toString().trim()));
//                pairs.add(new BasicNameValuePair("sex",sex));
//                pairs.add(new BasicNameValuePair("area",areaTv.getText().toString().trim()));
//                pairs.add(new BasicNameValuePair("introduction",introduceTv.getText().toString().trim()));
//                pairs.add(new BasicNameValuePair("se",HeApplication.SESSIONId_VALUE));
                new UpdateHeadAsync().execute(updateUrl);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog=null;
        AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setIcon(R.drawable.login_logo);

        builder.setItems(R.array.image, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 0:
                        camera();
                        break;
                    case 1:
                        gallery();
                        break;
                }

            }
        });
        dialog=builder.create();
        return dialog;
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
                HeApplication.imageLoader.displayImage(userInfoResult.getBody().getUserInfoResult().getHeading(),headImg,
                        HeApplication.getOptions()); //设置头像
                nameTv.setText(userInfoResult.getBody().getUserInfoResult().getNickname());

                if (userInfoResult.getBody().getUserInfoResult().getSex().equals("1")){
                    sexTv.setText("男");
                }else if(userInfoResult.getBody().getUserInfoResult().getSex().equals("2")){
                    sexTv.setText("女");
                }else{
                    sexTv.setText("未知");
                }

                areaTv.setText(userInfoResult.getBody().getUserInfoResult().getArea());
                introduceTv.setText(userInfoResult.getBody().getUserInfoResult().getIntroduction());

                HeApplication.HEAD_IMG=userInfoResult.getBody().getUserInfoResult().getHeading();
                HeApplication.NAME=userInfoResult.getBody().getUserInfoResult().getNickname();
                HeApplication.SEX=sexTv.getText().toString();
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

    private static final int PHOTO_REQUEST_GALLERY=0;
    private static final int PHOTO_REQUEST_CAREMA=1;
    private static final int PHOTO_REQUEST_CUT=3;
    private static String state = Environment.getExternalStorageState();
    private static File tempFile;


    /*
     * 从相册获取
     */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    "/head.jpg");
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (state.equals(Environment.MEDIA_MOUNTED)) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(InfoActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
//                this.headImg.setImageBitmap(bitmap);
                if(bitmap!=null){
                    if(saveBitmap2file(bitmap,filename)){
                        if (asyncImg != null && asyncImg.getStatus().equals(AsyncTask.Status.RUNNING)) {
                            asyncImg.cancel(true);
                        }
                        asyncImg=new AsyncImg();
                        asyncImg.execute();
                    }

                }else{
                    Log.e("saveFile_False","");
                }

            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    private class AsyncImg extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

            ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();
            ufi.add(new UploadFileItem("file",Environment.getExternalStorageDirectory()
                    + "/Hejion/img"+ filename));
            Log.e("files",Environment.getExternalStorageDirectory()
                    + "/Hejion/img"+ filename);
            HttpPostEmulator hpe = new HttpPostEmulator();
            String response = null;
            try {
                response = hpe.sendHttpPostRequest(null, ufi);
            } catch (Exception e) {
                e.printStackTrace();
                response="";
            }
            Log.e("response",response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            if (!s.equals("")){
                s=s.replace(";","");
                Log.e("头像",s);
                DialogUtils.showWaitDialog(context);

                String sex="";
                if(sexTv.getText().toString().trim().equals("男")){
                    sex="1";
                }else if(sexTv.getText().toString().trim().equals("女")){
                    sex="2";
                }else{
                    sex="0";
                }

                s="?headimg="+s+"&se="+HeApplication.SESSIONId_VALUE+"&nickname="+nameTv.getText().toString().trim()+"&sex="+sex+"&area="+areaTv.getText().toString().trim()
                        +"&introduction="+introduceTv.getText().toString().trim();
                new UpdateHeadAsync().execute(s);
            }else{

            }

        }
    }

    /**
     * 更新头像
     */
    private class UpdateHeadAsync extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {


//            String reponse=HttpUtils.httpPost(pairs,HttpUtils.getUserinfoUrl());

            Log.e("get_url",HttpUtils.UPDATEINFOURL+strings[0]);
            result=ServerHelper.getResponseEntity(new UpdateResponse(),0,null,HttpUtils.UPDATEINFOURL+strings[0]);
            if (result!=null){
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("")){
                Toast.makeText(context,"更新失败",Toast.LENGTH_LONG).show();
                return;
            }
            DialogUtils.cancelWaitDialog();
            if (result.getBody().getErrorcode().equals("0")){
                Toast.makeText(context,"保存成功",Toast.LENGTH_LONG).show();
                new UserInfoAsync().execute();
                return;
            }else{
                Toast.makeText(context,result.getBody().getMassage(),Toast.LENGTH_LONG).show();
                return;
            }




        }
    }

    private static final String filename="/headimg.jpg";

    /**
     * 将图片保存到本地文件
     * @param bmp
     * @param filename
     * @return
     */
    static boolean  saveBitmap2file(Bitmap bmp,String filename){
        Bitmap.CompressFormat format= Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {

            if (state.equals(Environment.MEDIA_MOUNTED)){
                if (creatDir()) {
                    stream = new FileOutputStream(Environment.getExternalStorageDirectory()
                            + "/Hejion/img" + filename);
                }
            }else{
                return false;
            }

        } catch (FileNotFoundException e) {
            Log.e("bitmap2file",e.toString());
            return false;
        }

        return bmp.compress(format, quality, stream);
    }

    public static boolean creatDir() {
        if (state.equals(Environment.MEDIA_MOUNTED)) {

                String saveDir = Environment.getExternalStorageDirectory()
                        + "/Hejion/img";
                File dir = new File(saveDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return true;

        }
        return false;
    }
    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}
