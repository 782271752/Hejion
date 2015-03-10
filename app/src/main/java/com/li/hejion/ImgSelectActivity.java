package com.li.hejion;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.hejion.Adapter.ImgSelectAdapter;
import com.li.hejion.Utils.FileTraversal;
import com.li.hejion.Utils.ImgCallBack;
import com.li.hejion.Utils.ImgSelectUtil;
import com.li.hejion.Utils.VideoSelectUtil;

public class ImgSelectActivity extends Activity implements OnClickListener{

	Bundle bundle;
	FileTraversal fileTraversal;
	GridView imgGridView;
	ImgSelectAdapter imgsAdapter;
	LinearLayout select_layout;
	ImgSelectUtil imgSelectUtil;
	RelativeLayout relativeLayout2;
	HashMap<Integer, ImageView> hashImage;
    HashMap<Integer,TextView> hashView;
    TextView titleTv;

	ArrayList<String> filelist;
    Button completeBtn,preBtn;
    ImageButton backBtn;
    int type;



    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_select);
		initView();
    }

    private void initView(){
        imgGridView=(GridView) findViewById(R.id.gridView1);
        titleTv=(TextView)findViewById(R.id.image_select_title);
        bundle= getIntent().getExtras();
        fileTraversal=bundle.getParcelable("data");
        type=bundle.getInt("type");
        imgsAdapter=new ImgSelectAdapter(this, fileTraversal.filecontent,onItemClickClass,type);
        imgGridView.setAdapter(imgsAdapter);
        select_layout=(LinearLayout) findViewById(R.id.selected_image_layout);
        relativeLayout2=(RelativeLayout) findViewById(R.id.relativeLayout2);
        completeBtn=(Button)findViewById(R.id.image_select_complete_btn);
        completeBtn.setOnClickListener(this);
        backBtn=(ImageButton)findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        hashImage=new HashMap<Integer, ImageView>();
        hashView=new HashMap<Integer, TextView>();
        filelist=new ArrayList<String>();
//		imgGridView.setOnItemClickListener(this);
        imgSelectUtil =new ImgSelectUtil(this);
        preBtn=(Button)findViewById(R.id.image_select_pre_btn);
        preBtn.setOnClickListener(this);
        if(type==0){
            titleTv.setText("我的相片流");
            relativeLayout2.setVisibility(View.VISIBLE);
        }else{
            titleTv.setText("我的视频");
            relativeLayout2.setVisibility(View.GONE);
        }



    }
	

	
	@SuppressLint("NewApi")
	public ImageView iconImage(String filepath,int index,CheckBox checkBox) throws FileNotFoundException{
		LayoutParams params=new LayoutParams(relativeLayout2.getMeasuredHeight()-10, relativeLayout2.getMeasuredHeight()-10);
		ImageView imageView=new ImageView(this);
		imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxWidth(40);
        imageView.setMaxHeight(40);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		imageView.setBackgroundResource(R.drawable.imgbg);
		float alpha=100;
        if(Build.VERSION.SDK_INT>=11) {
            imageView.setAlpha(alpha);
        }
        if (type==0){
            imgSelectUtil.imgExcute(imageView, imgCallBack, filepath);
        }else{
            imageView.setImageBitmap(VideoSelectUtil.getVideoThumbnail(filepath,50,50, MediaStore.Video.Thumbnails.MICRO_KIND));
        }

//		imageView.setOnClickListener(new ImgOnclick(filepath,checkBox));
		return imageView;
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
            case R.id.image_select_complete_btn:
                sendfiles();
                break;
            case R.id.image_select_pre_btn:

                if (filelist.size()==0){
                    return;
                }

                Intent intent=new Intent(this,ImgViewPagerActivity.class);
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("files",filelist);
                bundle.putString("pre","true");
                intent.putExtras(bundle);
                Log.e("filelist",filelist.toString());
                startActivity(intent);
                break;

            case R.id.back:
                finish();
                break;
        }

    }

    class ImgOnclick implements OnClickListener{
		String filepath;
		CheckBox checkBox;
		public ImgOnclick(String filepath,CheckBox checkBox) {
			this.filepath=filepath;
			this.checkBox=checkBox;
		}
		@Override
		public void onClick(View arg0) {
			checkBox.setChecked(false);

			select_layout.removeView(arg0);
			filelist.remove(filepath);

		}
	}
	
	ImgSelectAdapter.OnItemClickClass onItemClickClass=new ImgSelectAdapter.OnItemClickClass() {
		@Override
		public void OnItemClick(View v, int Position, CheckBox checkBox,ImageView bg) {
			String filapath=fileTraversal.filecontent.get(Position);
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				select_layout.removeView(hashImage.get(Position));
                bg.setVisibility(View.GONE);
                select_layout.removeView(hashView.get(Position));
				filelist.remove(filapath);

                if (filelist.size()==0){
                    preBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_complete_normal));
                }

                if (type==1) {
                    HeApplication.mVideoPath = "";
                }
			}else {
				try {
                    if (type==1&&!HeApplication.mVideoPath.equals("")){
                        return;
                    }
					checkBox.setChecked(true);
                    bg.setVisibility(View.VISIBLE);
					ImageView imageView=iconImage(filapath, Position,checkBox);
                    TextView tv = new TextView(ImgSelectActivity.this);
                    tv.setText("    ");

					if (imageView !=null) {
						hashImage.put(Position, imageView);
                        hashView.put(Position,tv);

                        if (type==0){
                            filelist.add(filapath);
                            preBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_preview_pressed));
                        }else{
                            HeApplication.mVideoPath=filapath;
                        }

						select_layout.addView(imageView);

                        select_layout.addView(tv);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	};
	


	public void sendfiles(){
		Intent intent =new Intent(this, WriteMessActivity.class);
        ((HeApplication)ImgSelectActivity.this.getApplication()).addFile(filelist);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清楚中间的activity
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//不重新启动新的WriteMessActivity
		startActivity(intent);
		
	}

    public interface SetCheckBox{
        public void setCheckBox(ArrayList<String> files,CheckBox checkBox);
    }
}
