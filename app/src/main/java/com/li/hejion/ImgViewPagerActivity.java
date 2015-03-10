package com.li.hejion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Matrix;
import android.widget.TextView;
import android.widget.Toast;

import com.li.hejion.Adapter.ViewPagerAdapter;
import com.li.hejion.R;
import com.li.hejion.Utils.ImgCallBack;
import com.li.hejion.Utils.ImgSelectUtil;

import java.util.ArrayList;
import java.util.List;

public class ImgViewPagerActivity extends Activity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    private ViewPager viewPager;

    private ViewPagerAdapter vpAdapter;

    /**
     * 定义一个ArrayList来存放View
     */
    private ArrayList<View> views;
    /**
    *记录当前选中位置
     */
    private int currentIndex=0;

    private ArrayList<String> files;
    private ImgSelectUtil imgSelectUtil;
    private Context context;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mode = NONE;
    private float oldDist;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private PointF start = new PointF();
    private PointF mid = new PointF();

    private ImageView delbtn;
    private TextView countTv;

    HeApplication application;

    private int[] currentIdexs={};
    private boolean isPre=false;
    private int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_pager);
        initView();
        init();

    }

    public void initView(){
        application=(HeApplication)this.getApplication();
        views = new ArrayList<View>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpAdapter = new ViewPagerAdapter(views);
        delbtn=(ImageView)findViewById(R.id.view_pager_delet_btn);
        delbtn.setOnClickListener(this);
        countTv=(TextView)findViewById(R.id.view_pager_count);

    }
    public void init(){
        context=this;
        files=new ArrayList<String>();
        imgSelectUtil =new ImgSelectUtil(this);
        // 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);

        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                800,800);


        // 初始化引导图片列表
        Bundle bundle=this.getIntent().getExtras();
        if (bundle!=null){
            if (!bundle.getString("pre").equals("true")){
                delbtn.setVisibility(View.VISIBLE);
                isPre=false;
            }else{
                delbtn.setVisibility(View.GONE);
                isPre=true;
            }

            files=bundle.getStringArrayList("files");
            if (!isPre){
                files.remove(files.size()-1);
                position=bundle.getInt("position");


            }
            Log.e("preView_files",files.toString());
            countTv.setText(currentIndex+1+"/"+files.size()+"");
            currentIdexs=new int[files.size()];

        }else{
            return;
        }
        for (int i = 0; i < files.size(); i++) {
            LinearLayout keyboard = new LinearLayout(this);
            keyboard.setLayoutParams(mParams);
            keyboard.setGravity(Gravity.CENTER);
            ImageView imgview = new ImageView(this);
            imgview.setLayoutParams(imgParams);

//            imgview.setOnTouchListener(new View.OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
//
//                    ImageView view = (ImageView)v;
//                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                        case MotionEvent.ACTION_DOWN:
//
//                            savedMatrix.set(matrix);
//                            //保存起始点
//                            start.set(event.getX(), event.getY());
//                            mode = DRAG;
//                            break;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_POINTER_UP:
//                            mode = NONE;
//                            break;
//                        //多点触控
//                        case MotionEvent.ACTION_POINTER_DOWN:
//                            oldDist = spacing(event);
//                            if(oldDist > 10f){
//                                savedMatrix.set(matrix);
//                                midPoint(mid, event);
//                                mode = ZOOM;
//                            }
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            if(mode == DRAG){
//                                matrix.set(savedMatrix);
//                                matrix.postTranslate(event.getX() - start.x,
//                                        event.getY() - start.y);
//                            }else if(mode == ZOOM){
//                                float newDist = spacing(event);
//                                if(newDist > 10f){
//                                    matrix.set(savedMatrix);
//                                    float scale = newDist / oldDist;
//                                    matrix.postScale(scale, scale, mid.x, mid.y);
//                                }
//                            }
//                            break;
//                    }
//                    Log.v("tag", "ttt");
//                    view.setImageMatrix(matrix);
//                    return true;
//                }
//            });


//            keyboard.addView(imgview);
            imgview.setLayoutParams(mParams);
            imgSelectUtil.imgExcute(imgview, imgCallBack, files.get(i));
            views.add(imgview);
        }

        // 设置数据
        viewPager.setAdapter(vpAdapter);
        // 设置监听
        viewPager.setOnPageChangeListener(this);


        viewPager.setCurrentItem(position);

    }

    //两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    //中点坐标
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    ImgCallBack imgCallBack=new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_pager_delet_btn:
//                Toast.makeText(ImgViewPagerActivity.this,currentIndex+"",Toast.LENGTH_LONG).show();
                Log.e("onclick",currentIdexs[currentIndex]+"");
                if (currentIdexs[currentIndex]==99) {
                    currentIdexs[currentIndex]=0;
                    List<String> file=new ArrayList<String>();
                    file.add(files.get(currentIndex).toString());
                    application.addFile(file);
                    delbtn.setImageDrawable(ImgViewPagerActivity.this.getResources().getDrawable(R.drawable.image_check_pressed));
                }else{
                    application.removeFlie(currentIndex);
                    currentIdexs[currentIndex]=99;
                    delbtn.setImageDrawable(ImgViewPagerActivity.this.getResources().getDrawable(R.drawable.image_check_normal));
                }

//                files.remove(currentIndex);

                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
//        Toast.makeText(ImgViewPagerActivity.this,i+"",Toast.LENGTH_LONG).show();
        currentIndex=i;
        countTv.setText(i+1+"/"+files.size()+"");
        Log.e("pageSelect",currentIdexs[currentIndex]+"");
        if (!isPre){
            if(currentIdexs[currentIndex]==99){
                delbtn.setImageDrawable(ImgViewPagerActivity.this.getResources().getDrawable(R.drawable.image_check_normal));
            }else{
                delbtn.setImageDrawable(ImgViewPagerActivity.this.getResources().getDrawable(R.drawable.image_check_pressed));
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
