package com.li.hejion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.li.hejion.Adapter.ImgFileListAdapter;
import com.li.hejion.Utils.FileTraversal;
import com.li.hejion.Utils.ImgSelectUtil;
import com.li.hejion.Utils.VideoSelectUtil;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileListActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{

    ListView fileLv;
    ImgFileListAdapter listAdapter;
    List<FileTraversal> locallist;
    ImgSelectUtil imgSelectUtil;
    VideoSelectUtil videoSelectUtil;

    TextView titileTv;
    List<HashMap<String, String>> listdata;
    ImageButton backBtn;

    /**
     * 判断是 选择图片还是视频
     * 0：表示图片
     * 1：表示视频
     */
    int type=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_file_list);

        Intent intent=this.getIntent();
        if (intent.getExtras().getString("type").equals("img")){
            type=0;
        }else{
            type=1;
        }

        initView(type);
        init(type);



    }

    private void initView(int type){
        fileLv=(ListView)findViewById(R.id.image_file_lv);
        backBtn=(ImageButton)findViewById(R.id.back);
        backBtn.setOnClickListener(this);
        titileTv=(TextView)findViewById(R.id.file_title);
        if (type==0){
            titileTv.setText("选择图片");
        }else{
            titileTv.setText("选择视频");
        }

    }

    private void init(int type){
        listdata=new ArrayList<HashMap<String,String>>();
        Bitmap bitmap[] = null;
        if (type==0){
            imgSelectUtil =new ImgSelectUtil(this);
            locallist= imgSelectUtil.LocalImgFileList();

            if (locallist!=null) {
                bitmap=new Bitmap[locallist.size()];
                for (int i = 0; i < locallist.size(); i++) {
                    HashMap<String, String> map=new HashMap<String, String>();
                    map.put("filecount", locallist.get(i).filecontent.size()+"张");
                    map.put("imgpath", locallist.get(i).filecontent.get(0)==null?null:(locallist.get(i).filecontent.get(0)));
                    map.put("filename", locallist.get(i).filename);
                    listdata.add(map);
                }
            }
        }else{
            videoSelectUtil=new VideoSelectUtil(this);
            locallist=videoSelectUtil.LocalVedioFileList();

            if (locallist!=null) {
                bitmap=new Bitmap[locallist.size()];
                for (int i = 0; i < locallist.size(); i++) {
                    HashMap<String, String> map=new HashMap<String, String>();
                    map.put("filecount", locallist.get(i).filecontent.size()+"个");
                    map.put("imgpath", locallist.get(i).filecontent.get(0)==null?null:(locallist.get(i).filecontent.get(0)));
                    map.put("filename", locallist.get(i).filename);
                    listdata.add(map);
                }
            }

        }

        listAdapter=new ImgFileListAdapter(this, listdata,type);
        fileLv.setAdapter(listAdapter);
        fileLv.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent=new Intent(this,ImgSelectActivity.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("data", locallist.get(i));
        bundle.putInt("type",type);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
