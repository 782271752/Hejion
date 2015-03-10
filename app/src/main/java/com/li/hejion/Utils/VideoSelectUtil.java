package com.li.hejion.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by li on 2014/7/28.
 */
public class VideoSelectUtil {

    Context context;

    public VideoSelectUtil(Context context){
        this.context=context;
    }

    /**
     * 获取本地所有视频文件
     * @return
     */
    public ArrayList<String> listAlldir(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        Uri uri = intent.getData();
        ArrayList<String> list = new ArrayList<String>();
        String[] proj ={MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);//managedQuery(uri, proj, null, null, null);
        if (cursor==null){
            return list;
        }
        while(cursor.moveToNext()){
            String path =cursor.getString(0);
            list.add(new File(path).getAbsolutePath());
        }
        return list;
    }

    public List<FileTraversal> LocalVedioFileList(){
        List<FileTraversal> data=new ArrayList<FileTraversal>();
        String filename="";
        List<String> allvediolist=listAlldir();
        List<String> retulist=new ArrayList<String>();
        if (allvediolist!=null) {
            Set set = new TreeSet();
            String []str;
            for (int i = 0; i < allvediolist.size(); i++) {
                retulist.add(getfileinfo(allvediolist.get(i)));
            }
            for (int i = 0; i < retulist.size(); i++) {
                set.add(retulist.get(i));
            }

            str= (String[]) set.toArray(new String[0]);//set把重复的名称去掉
            for (int i = 0; i < str.length; i++) {
                filename=str[i];
                FileTraversal ftl= new FileTraversal();
                ftl.filename=filename;
                data.add(ftl);
            }



            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < allvediolist.size(); j++) {
                    if (data.get(i).filename.equals(getfileinfo(allvediolist.get(j)))) {
                        data.get(i).filecontent.add(allvediolist.get(j));
                    }
                }
            }
        }
        return data;
    }
    /**
     * 得到存放文件的文件夹名称
     * @param data  文件的路径
     * @return
     */
    public String getfileinfo(String data){
        String filename[]= data.split("/");
        if (filename!=null) {
            return filename[filename.length-2];
        }
        return null;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


}
