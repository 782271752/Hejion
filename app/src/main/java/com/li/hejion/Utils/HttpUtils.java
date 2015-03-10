package com.li.hejion.Utils;

import android.util.Log;

import com.li.hejion.ReadMessActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.protocol.HTTP;

import java.util.List;

/**
 * Created by li on 2014/7/26.
 */
public class HttpUtils {


//  public final static String  BASE="http://test.wefans.com/mx";
    public final static String  BASE="http://218.244.139.183/mx_srv2";
    public final static String BASE_URL= BASE+"/user";
    public final static String LOGIN="/UserLogin.do";
    public final static String USERINFO="/UserGetSelfInfo.do";
    public final static String LEAVEWORD="/LeavewordPushlish.do";

//    public final static String WEB_URL="http://test.wefans.com/static/hejiong/jiongyan/jiongyan.html";
    public final static String WEB_URL="http://hejiong.wefans.com/hejiong/jiongyan/jiongyan.html";
    /**
     * 2014.8.5
     * 上传文件路径
     */
    public final static String UPLOADFILEURL ="http://218.244.139.183:8080/media/upload.do";

    /**
     * 修改个人信息
     */
    public final static String UPDATEINFOURL=BASE_URL+"/UserSaveInfo.do";

    public final static String getLoginUrl(){
        return BASE_URL+LOGIN;
    }

    public final static String getUserinfoUrl(){
        return BASE_URL+USERINFO;
    }

    public final static String getLeaveWordUrl(){
        return BASE_URL+LEAVEWORD;
    }


    /**
     * get请求
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        String str = "";
        try {
            HttpGet request = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            request.setHeader("User-Agent", ReadMessActivity.ua);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                str = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            Log.e("httpGet",e.toString());
            str="";
        }
        Log.e("HttpGet_Response",str);
        return str;
    }

    /**
     * post请求
     * @param param  提交字段
     * @param params 路径
     * @return
     */
    public static String httpPost(List<NameValuePair> param, String...params)
    {
        String rs="";
        try {
            HttpClient httpClient= new DefaultHttpClient();////生成一个http客户端对象
            HttpParams httpparams=httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpparams, 20000);//设置连接超时时间
            HttpConnectionParams.setSoTimeout(httpparams, 600000);//设置请求超时
            HttpPost post=new HttpPost(params[0]);//客户端向服务器发送请求,返回一个响应对象
            post.setEntity(new UrlEncodedFormEntity(param,HTTP.UTF_8));
            post.setHeader("User-Agent",ReadMessActivity.ua);
            HttpResponse response =new DefaultHttpClient().execute(post);
            if(response.getStatusLine().getStatusCode()==200)
            {
                rs=EntityUtils.toString(response.getEntity());
            }

        } catch (Exception e) {
            Log.e("httpPost",e.toString());
            rs="";
        }
        Log.e("HttpPost_Response",rs);
        return rs;

    }

}
