package com.li.hejion;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.li.hejion.HttpEntity.DefaultMessageResponse;
import com.li.hejion.HttpEntity.LeaveWordRequest;
import com.li.hejion.HttpEntity.LeaveWordResponse;
import com.li.hejion.HttpEntity.ServerHelper;
import com.li.hejion.Utils.HttpPostEmulator;
import com.li.hejion.Utils.HttpUtils;
import com.li.hejion.Utils.ImgSelectUtil;
import com.li.hejion.Utils.UploadFileItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UploadService extends IntentService {

    public final static String TAG="UPLOADSERVICE";
    LeaveWordRequest request;
    String title,content;
    List<String> files;
    public String errorid="";
    public String uploadid="";
    public String relurls="";
    public List<NameValuePair> wordPair;
    public DefaultMessageResponse<LeaveWordResponse> leaveWordResponse;

    public UploadService() {
        super(TAG);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        request=new LeaveWordRequest();
        files=new ArrayList<String>();
        wordPair=new ArrayList<NameValuePair>();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }


    /**
     * 在状态栏显示通知
     */
    private void showNotification(){
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性
        Notification notification =new Notification(R.drawable.icon_he,
                "发表留言", System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //闪光时间，毫秒

        // 设置通知的事件消息
        CharSequence contentTitle ="发表留言"; // 通知栏标题
        CharSequence contentText ="正在发送数据，请稍候"; // 通知栏内容
//        Intent notificationIntent =new Intent(UploadService.this, MainActivity.class); // 点击该通知后要跳转的Activity
//        PendingIntent contentItent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, contentTitle, contentText, null);

        // 把Notification传递给NotificationManager
        notificationManager.notify(0, notification);
    }
    //删除通知
    private void clearNotification(){
        // 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }

    public class UploadFileAsync extends AsyncTask<LeaveWordRequest,Void,String>{
        @Override
        protected String doInBackground(LeaveWordRequest... leaveWordRequests) {
            title=leaveWordRequests[0].getTitle();

            content=leaveWordRequests[0].getContent();
            files=leaveWordRequests[0].getFiles();
            try{



                ArrayList<UploadFileItem> ufi = new ArrayList<UploadFileItem>();
                if (files.size()>1){
                    for (int i=0;i<files.size()-1;i++){

                        ufi.add(new UploadFileItem(ImgSelectUtil.getName(files.get(i)),files.get(i)));
                        Log.e("files.get"+i,files.get(i));
                    }
                    HttpPostEmulator hpe = new HttpPostEmulator();
                    String response = hpe.sendHttpPostRequest(null, ufi);
                    Log.e("response",response);
                    return response;
                }


            }catch (Exception e){
                Log.e("上传失败",e.toString());
                clearNotification();
                return "";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Toast.makeText(UploadService.this,s,Toast.LENGTH_LONG).show();
//            try{
//                JSONObject object=new JSONObject(s);
//                errorid=object.getString("errorid");
//                uploadid=object.getString("uploadid");
//                relurls=object.getString("relurls").replace("\\","");
            Log.e("relurls",s);
//                if (errorid.equals("0")){
            if (s.equals("")){
                Toast.makeText(UploadService.this,"发布失败，请重试",Toast.LENGTH_LONG).show();
                clearNotification();
                return;
            }

            wordPair=new ArrayList<NameValuePair>();
            wordPair.add(new BasicNameValuePair(HeApplication.TITLE_KEY,title));
            wordPair.add(new BasicNameValuePair(HeApplication.CONTENT_KEY,content));
            wordPair.add(new BasicNameValuePair(HeApplication.PDID_KEY,HeApplication.PDID_VALUE));
            wordPair.add(new BasicNameValuePair(HeApplication.SESSIONID_KEY,HeApplication.SESSIONId_VALUE));

            wordPair.add(new BasicNameValuePair(HeApplication.FILE_KEY,s));

            new LeaveWordAsync().execute(wordPair);
//                }

//            }catch (Exception e){
//                Log.e("UploadResponseToJson",e.toString());
//            }



        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"onHandleIntent");
        if (intent!=null&&intent.getExtras()!=null){
            request=(LeaveWordRequest)intent.getSerializableExtra("message");
            Log.e("message",request.getTitle()+"---"+request.getContent()+"---"+request.getFiles().size());

            if (request.getFiles().size()>1) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new UploadFileAsync().execute(request);
                    }
                }).start();
            }else{
                wordPair=new ArrayList<NameValuePair>();
                wordPair.add(new BasicNameValuePair(HeApplication.TITLE_KEY,request.getTitle()));
                wordPair.add(new BasicNameValuePair(HeApplication.CONTENT_KEY,request.getContent()));
                wordPair.add(new BasicNameValuePair(HeApplication.PDID_KEY,HeApplication.PDID_VALUE));
                wordPair.add(new BasicNameValuePair(HeApplication.SESSIONID_KEY,HeApplication.SESSIONId_VALUE));
                wordPair.add(new BasicNameValuePair(HeApplication.FILE_KEY,""));
                new LeaveWordAsync().execute(wordPair);
            }

            showNotification();


        }
    }


    /**
     * 异步发布留言
     */
    public class LeaveWordAsync extends AsyncTask<List<NameValuePair>,Void,String>{
        @Override
        protected String doInBackground(List<NameValuePair>... lists) {

            leaveWordResponse= ServerHelper.getResponseEntity(new LeaveWordResponse(),1,lists[0],HttpUtils.getLeaveWordUrl());
            if (leaveWordResponse!=null){
                return leaveWordResponse.getBody().getErrorcode();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("")){
                Toast.makeText(UploadService.this,"发布失败，请重试",Toast.LENGTH_LONG).show();
                clearNotification();
                return;
            }

            if (s.equals("0")){
                HeApplication.mVideoPath="";
                HeApplication.mVoicePath="";
            }
            Toast.makeText(UploadService.this,leaveWordResponse.getBody().getMassage(),Toast.LENGTH_LONG).show();
            clearNotification();
            super.onPostExecute(s);
        }
    }
}
