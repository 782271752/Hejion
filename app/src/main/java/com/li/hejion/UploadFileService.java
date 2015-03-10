package com.li.hejion;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 上传文件
 * 2014.8.5
 */
public class UploadFileService extends Service {


    public UploadFileService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        new UploadFileAsnyc().execute();
        if (intent!=null&&intent.getExtras()!=null){
            try{
                new UploadFileAsnyc().execute();
            }catch (Exception e){
                Log.e("UploadFile__addpart",e.toString());
            }

        }
    }

    public class UploadFileAsnyc extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

            try {
                MultipartEntity entity=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
                entity.addPart(HeApplication.USERNAME_KEY,new StringBody("hejiong",Charset.forName("UTF-8")));
                entity.addPart(HeApplication.PASSWORD_KEY,new StringBody("123", Charset.forName("UTF-8")));
                entity.addPart(HeApplication.PDID_KEY,new StringBody(HeApplication.PDID_VALUE));
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost postRequest = new HttpPost(HeApplication.FILE_KEY);
                postRequest.setEntity(entity);
                HttpResponse response = httpClient.execute(postRequest);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null) {
                    s = s.append(sResponse);
                }
                System.out.println("Response: " + s);
                return s.toString();
//                UploadUtils.uploadFile(new File("/storage/sdcard0/hfdatabase/img/3/20140724094338.jpg"), HttpUtils.UPLOADFILEURL);
            } catch (Exception e) {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
