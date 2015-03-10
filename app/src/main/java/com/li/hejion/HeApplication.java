package com.li.hejion;

import android.app.Application;
import android.content.Context;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by li on 2014/7/18.
 */
public class HeApplication extends Application {


    /**
     * 产品ID
     */
    public final static String PDID_VALUE ="hj";
    public final static String PDID_KEY ="pdid";
    public final static String USERNAME_KEY ="username";
    public final static String PASSWORD_KEY ="password";
    public final static String TITLE_KEY ="title";
    public final static String CONTENT_KEY ="content";
    public final static String FILE_KEY ="file";
    public final static String SESSIONID_KEY ="se";
    public static String SESSIONId_VALUE="";

    public final static String TOKEN_KEY="token";
    public final static String SUBURL_KEY="suburl";
    public final static String TOKEN_VALUE="f5522307d17451836d4203112737967f";
    public final static String SUBURL_VALUE="hj/lw/app";
    public final static String UPLOADID_KEY="uploadid";
    public final static String FILENAME_KEY="fn";

    /**
     * 个人信息
     */
    public static String HEAD_IMG="";
    public static String NAME="";
    public static String SEX="";
    public static String AREA="";
    public static String INTRODUCE="";

    public static ImageLoader imageLoader = ImageLoader.getInstance();

    private static List<String> fileList;

    /**
     * 语音路径
     */
    public static String mVoicePath="";

    /**
     *  视频路径
     */
    public static String mVideoPath="";

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        init();

    }

    /**
     * 图片展示参数
     * @return displayImageOptions
     */
    public static DisplayImageOptions getOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.headborder)
                .showImageForEmptyUri(R.drawable.headborder)
                .showImageOnFail(R.drawable.headborder)
                .cacheInMemory()
                .cacheOnDisc()
                .displayer(new RoundedBitmapDisplayer(5))
                .build();

        return options;
    }

    /**
     * 初始化图片下载
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .enableLogging()
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void init(){
        fileList=new ArrayList<String>();
    }

    /**
     * 保存选中的图片
     * @param fileList
     */
    public void setFileList(List<String> fileList){
        this.fileList=fileList;
    }


    public void addFile(List<String> fileList){
        if (this.fileList.size()>0&&this.fileList.get(this.fileList.size()-1).equals("")){
            this.fileList.remove(this.fileList.size()-1);
        }
        this.fileList.addAll(fileList);
        this.fileList.add("");
    }

    /**
     * 获取选中的图片
     * @return
     */
    public List<String> getFileList(){
        return fileList;
    }

    /**
     * 删除选中图片
     * @param postion
     */
    public void removeFlie(int postion){
        fileList.remove(postion);
    }

    public void removeAllFile(){
        fileList.clear();
    }

    /**
     * 生成32位 UUID
     * @return
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

}
