package com.li.hejion.Utils;

import android.util.Log;

import com.li.hejion.HeApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HttpPostEmulator {
	
	// 每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
	private static final String BOUNDARY = "----------IIOIRJE762w93430HNjjfhwI";
    public static String mreponse="";
    String serverUrl;
	public String sendHttpPostRequest(ArrayList<FormFieldKeyValuePair> generalFormFields, ArrayList<UploadFileItem> filesToBeUploaded)
            throws Exception {

        mreponse="";

//		for (UploadFileItem ufi : filesToBeUploaded) {
        for (int i = 0; i < filesToBeUploaded.size(); i++) {
            StringBuffer sb = new StringBuffer();
            sb.append(HeApplication.TOKEN_KEY).append("=").append(HeApplication.TOKEN_VALUE).append("&")
                    .append(HeApplication.UPLOADID_KEY).append("=").append(HeApplication.getUUID()).append("&")
                    .append(HeApplication.SUBURL_KEY).append("=").append(HeApplication.SUBURL_VALUE);
            serverUrl =HttpUtils.UPLOADFILEURL +"?"+sb.toString();
            Log.e("upload_url",serverUrl);
            // 向服务器发送post请求
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            // 头
            String boundary = BOUNDARY;
            // 传输内容
            StringBuffer contentBody = new StringBuffer("--" + BOUNDARY);
            // 尾
            String endBoundary = "\r\n--" + boundary + "--\r\n";
            OutputStream out = connection.getOutputStream();
            // 1. 处理文字形式的POST请求
            if (null != generalFormFields) {
                for (FormFieldKeyValuePair ffkvp : generalFormFields) {
                    contentBody.append("\r\n").append("Content-Disposition: form-data; name=\"").append(ffkvp.getKey() + "\"").append("\r\n").append("\r\n").append(ffkvp.getValue()).append("\r\n").append("--").append(boundary);
                }
            }
            String boundaryMessage1 = contentBody.toString();
            out.write(boundaryMessage1.getBytes("utf-8"));
            // 2. 处理文件上传
            contentBody = new StringBuffer();
            contentBody.append("\r\n").append("Content-Disposition:form-data; name=\"").append(filesToBeUploaded.get(i).getFormFieldName() + "\"; ")
                    // form中field的名称
                    .append("filename=\"").append(filesToBeUploaded.get(i).getFileName() + "\"")
                    // 上传文件的文件名，包括目录
                    .append("\r\n").append("Content-Type:application/octet-stream").append("\r\n\r\n");
            String boundaryMessage2 = contentBody.toString();
            out.write(boundaryMessage2.getBytes("utf-8"));
            // 开始真正向服务器写文件

            String name = ImgSelectUtil.getName(filesToBeUploaded.get(i).getFileName());
            Log.e("file_name", name);

            if (name.indexOf(".jpg") != -1 || name.indexOf(".png") != -1 || name.indexOf(".JPG") != -1 || name.indexOf(".PNG") != -1 ||
                    name.indexOf(".JPEG") != -1 || name.indexOf(".jpeg") != -1 || name.indexOf(".gif") != -1 || name.indexOf(".GIF") != -1) {
                try {
                    byte[] bufferOut = ImgSelectUtil.getImageByte(filesToBeUploaded.get(i).getFileName());
                    out.write(bufferOut);
                    contentBody.append(BOUNDARY);
                    String boundaryMessage = contentBody.toString();
                    out.write(boundaryMessage.getBytes("utf-8"));
                    System.out.println(boundaryMessage);

                } catch (Exception e) {
                    Log.e("addIMageException", e.toString());
                }
            } else {
                File file = new File(filesToBeUploaded.get(i).getFileName());

                DataInputStream dis = new DataInputStream(new FileInputStream(file));
                int bytes = 0;
                byte[] bufferOut = new byte[(int) file.length()];
                bytes = dis.read(bufferOut);
                out.write(bufferOut, 0, bytes);
                dis.close();
                contentBody.append(BOUNDARY);
                String boundaryMessage = contentBody.toString();
                out.write(boundaryMessage.getBytes("utf-8"));
                System.out.println(boundaryMessage);
            }

            out.write((BOUNDARY + "--\r\n").getBytes("UTF-8"));
            // 3. 写结尾
            out.write(endBoundary.getBytes("utf-8"));
            out.flush();
            out.close();
            // 4. 从服务器获得回答的内容
            String strLine = "";
            String strResponse = "";
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((strLine = reader.readLine()) != null) {
                strResponse += strLine + "\n";
            }
            // System.out.print(strResponse);
            mreponse=mreponse+getUploadUrl(strResponse)+";";
            Log.e("Response",mreponse);

        }
        return mreponse;
    }


    public static String getUploadUrl(String response) {
        try {
            JSONObject object = new JSONObject(response);
            String name = object.getString("relurls").replace("\\", "");
            Log.e("getUploadName", name);
            return name;
        } catch (Exception e) {
            Log.e("getUploadUrl", e.toString());
        }
        return "";
    }


}