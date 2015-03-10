package com.li.hejion.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ImgSelectUtil {

    Context context;

    public ImgSelectUtil(Context context) {
        this.context=context;
    }

    /**
     * 获取全部图片地址
     * @return
     */
    public ArrayList<String>  listAlldir(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri uri = intent.getData();
        ArrayList<String> list = new ArrayList<String>();
        String[] proj ={MediaStore.Images.Media.DATA};
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

    public List<FileTraversal> LocalImgFileList(){
        List<FileTraversal> data=new ArrayList<FileTraversal>();
        String filename="";
        List<String> allimglist=listAlldir();
        List<String> retulist=new ArrayList<String>();
        if (allimglist!=null) {
            Set set = new TreeSet();
            String []str;
            for (int i = 0; i < allimglist.size(); i++) {
                retulist.add(getfileinfo(allimglist.get(i)));
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
                for (int j = 0; j < allimglist.size(); j++) {
                    if (data.get(i).filename.equals(getfileinfo(allimglist.get(j)))) {
                        data.get(i).filecontent.add(allimglist.get(j));
                    }
                }
            }
        }
        return data;
    }

    //显示原生图片尺寸大小
    public Bitmap getPathBitmap(Uri imageFilePath,int dw,int dh)throws FileNotFoundException{
        //获取屏幕的宽和高
        /**
         * 为了计算缩放的比例，需要获取整个图片的尺寸，而不是图片
         * BitmapFactory.Options类中有一个布尔型变量inJustDecodeBounds，将其设置为true
         * 这样，获取到的就是图片的尺寸，而不用加载图片了。
         * 当设置这个值的时候，接着就可以从BitmapFactory.Options的outWidth和outHeight中获取到值
         */
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        //由于使用了MediaStore存储，这里根据URI获取输入流的形式
        Bitmap pic = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageFilePath),
                null, op);

        int wRatio = (int) Math.ceil(op.outWidth / (float) dw); //计算宽度比例
        int hRatio = (int) Math.ceil(op.outHeight / (float) dh); //计算高度比例

        /**
         * 需要判断是否需要缩放以及到底对宽还是高进行缩放。
         * 如果高和宽不是全都超出了屏幕，那么无需缩放。
         * 如果高和宽都超出了屏幕大小，则如何选择缩放呢》
         * 这需要判断wRatio和hRatio的大小
         * 大的一个将被缩放，因为缩放大的时，小的应该自动进行同比率缩放。
         * 缩放使用的还是inSampleSize变量
         */
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false; //注意这里，一定要设置为false，因为上面我们将其设置为true来获取图片尺寸了
        pic = BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(imageFilePath), null, op);

        return pic;
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

    public void imgExcute(ImageView imageView,ImgCallBack icb, String... params){
        LoadBitAsynk loadBitAsynk=new LoadBitAsynk(imageView,icb);
        loadBitAsynk.execute(params);
    }

    public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap>{

        ImageView imageView;
        ImgCallBack icb;

        LoadBitAsynk(ImageView imageView,ImgCallBack icb){
            this.imageView=imageView;
            this.icb=icb;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=null;
            try {
                if (params!=null) {
                    for (int i = 0; i < params.length; i++) {
                        bitmap=getPathBitmap(Uri.fromFile(new File(params[i])), 600, 600);
                    }
                }
            } catch (FileNotFoundException e) {
                Log.e("loadBitAsync",e.toString());
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result!=null) {
//				imageView.setImageBitmap(result);
                icb.resultImgCall(imageView, result);
            }
        }




    }

    /**
     * 图片按比例大小压缩方法
     * @param srcPath
     * @return
     */
    public static byte[] getImageByte(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static byte[] compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        byte[] data = baos.toByteArray();
        return data;
    }

    /**
     * 获取文件的名称
     * @param data
     * @return
     */
    public static String getName(String data){
        String filename[]= data.split("/");
        if (filename!=null) {
            return filename[filename.length-1];
        }
        return null;
    }



	
}
