package com.li.hejion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.li.hejion.Adapter.ImgGridAdapter;
import com.li.hejion.HttpEntity.DefaultMessageResponse;
import com.li.hejion.HttpEntity.LeaveWordRequest;
import com.li.hejion.HttpEntity.LeaveWordResponse;
import com.li.hejion.Widget.AudioDialog;
import com.li.hejion.Widget.AudioRecorder;
import com.li.hejion.Widget.ImgGridview;

import android.text.format.DateFormat;

import org.apache.http.NameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WriteMessActivity extends Activity implements View.OnClickListener,
        ImgGridAdapter.ImageSelect,AdapterView.OnItemClickListener{

    ImageButton audioBtn,videoBtn,imageBtn,backBtn;
    private Button sendBtn;
    public static final int VIDEO=0;
    public static final int IMAGE=1;
    private List<String> fileList;
    private List<String> filelistItem;
    private ImgGridAdapter adapter;
    private ImgGridview imgGridView;
    private HeApplication application;

    private static final int IMG_REQUEST_CODE =1;
    private static final int VIDEO_REQUEST_CODE=2;
    private Bitmap photo;
    private File imgFile,videoFile,audioFile;

    private EditText titleEdt,contentEdt;

    private final static String TITLE="title";
    private final static String CONTENT="content";
    private final static String IMAGEFILES="files";
    private Context context;
    private static String state = Environment.getExternalStorageState();
    AudioRecorder recorder;
    private boolean isLongClick;
    private String audioPath;
    private DefaultMessageResponse<LeaveWordResponse> sendResult;
    private List<NameValuePair> pair;
    LeaveWordRequest request;
    private AudioDialog dialog;
    private RelativeLayout videoLayout,voiceLayout;
    private ImageView videoColoseIv,voiceColoseIv;

    /**
     * 记录发布的视频路径
     */
    private static String mVideoPath="";

    /**
     * 记录发布的语音路径
     */
    private static String mVoicePath="";

    private MediaPlayer mediaPlayer;

    private ImageView voideoPlaytIv, voicePlayIv;
    /**
     * 判断音频是否已经点击
     */
    private boolean isVoicClick=false;
    /**
     *判断视频是否已经点击
     */
    private boolean isVideoClick=false;

//    private static String mVideoPath="",mVoicePath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_writemess);
        initView();

        if (savedInstanceState!=null){
            titleEdt.setText(savedInstanceState.getString(TITLE));
            contentEdt.setText(savedInstanceState.getString(CONTENT));
            application.setFileList(savedInstanceState.getStringArrayList(IMAGEFILES));

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE, titleEdt.getText().toString().trim());
        outState.putString(CONTENT, contentEdt.getText().toString().trim());
        outState.putStringArrayList(IMAGEFILES, (ArrayList<String>) application.getFileList());

    }

    @Override
    protected void onResume() {
        super.onResume();
        fileList=application.getFileList();

        Log.e("application.getFileList()", application.getFileList() + "");

        if (fileList.size()==0){
            fileList.add("");
        }
        adapter=new ImgGridAdapter(WriteMessActivity.this,fileList);
        imgGridView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        adapter.setImageSelect(this);

        if (!HeApplication.mVideoPath.equals("")){
            mhanddler.sendEmptyMessage(new Message().what=3);
        }


    }

    private void initView(){
        context=this;
        videoBtn =(ImageButton)findViewById(R.id.write_vedio_btn);
        videoBtn.setOnClickListener(this);
        backBtn=(ImageButton)findViewById(R.id.back);
        backBtn.setOnClickListener(this);

        imgGridView=(ImgGridview)findViewById(R.id.write_image_gv);
        imgGridView.setOnItemClickListener(this);
        application=(HeApplication)this.getApplication();



        fileList=new ArrayList<String>();

        titleEdt=(EditText)findViewById(R.id.write_title_edt);
        contentEdt=(EditText)findViewById(R.id.write_content_edt);

        imageBtn=(ImageButton)findViewById(R.id.write_image_btn);
        imageBtn.setOnClickListener(this);

        filelistItem=new ArrayList<String>();
//        adapter=new ImgGridAdapter(WriteMessActivity.this,fileList);
//        imgGridView.setAdapter(adapter);

        audioBtn=(ImageButton)findViewById(R.id.write_audio_btn);
        takeAudio();
        sendBtn=(Button)findViewById(R.id.write_send_btn);
        sendBtn.setOnClickListener(this);

        pair=new ArrayList<NameValuePair>();

        titleEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    titleEdt.setHint("");
                }else{
                    titleEdt.setHint("做个标题,更清晰明了哟~");
                }
            }
        });

        contentEdt.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    contentEdt.setHint("");
                }else{
                    contentEdt.setHint("发现了什么好东西，发表下吧~");
                }
            }
        });

        videoLayout=(RelativeLayout)findViewById(R.id.video_layout);
//        videoLayout.setOnClickListener(this);
        voiceLayout=(RelativeLayout)findViewById(R.id.voice_layout);
//        voiceLayout.setOnClickListener(this);
        videoColoseIv=(ImageView)findViewById(R.id.video_close_btn);
        videoColoseIv.setOnClickListener(this);
        voiceColoseIv=(ImageView)findViewById(R.id.voice_close_btn);
        voiceColoseIv.setOnClickListener(this);

        voicePlayIv =(ImageView)findViewById(R.id.voice_play);
        voicePlayIv.setOnClickListener(this);
        voideoPlaytIv=(ImageView)findViewById(R.id.video_play);
        voideoPlaytIv.setOnClickListener(this);

    }



    /**
     * 录音
     */
    public void takeAudio(){
        audioBtn.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                if (isVoicClick){
                    Toast.makeText(context,"只允许上传一个录音",Toast.LENGTH_LONG).show();
                    return false;
                }

                isLongClick=true;

                audioPath=getAudioPath();
//                vedioDialog.showRecordDialog(context, "正在录音");
                dialog=new AudioDialog(context,"正在录音",R.style.AsyncTaskDialog);
                dialog.show();
                return false;
            }
        });

        audioBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isLongClick){
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_UP:
                            try{
                                recorder.stop();
//                                vedioDialog.cancelRecordDialog();
                                dialog.dismiss();

                                mhanddler.sendEmptyMessage(new Message().what=2);

                            }catch (Exception e){
                                Log.e("Audio_Stop",e.toString());
                            }
                            isLongClick=false;
                        break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 得到录音的地址
     */
    public String getAudioPath(){
        if(state.equals(Environment.MEDIA_MOUNTED)){
            String saveDir=Environment.getExternalStorageDirectory()+"/Hejion/audio";
            String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".ram";
            File dir=new File(saveDir);
            if (!dir.exists()){
                    dir.mkdirs();
            }
            audioFile=new File(saveDir,name);

            if (!audioFile.exists()){
                try{
                    audioFile.createNewFile();
                }catch (Exception e){
                    Log.e("audiofile",e.toString());
                    return "";
                }
            }

            recorder=new AudioRecorder(audioFile.getPath());
            HeApplication.mVoicePath=audioFile.getPath();
            try{
                recorder.start();
            }catch (IOException e){
                Log.e("audioIoException",e.toString());
                return "";

            }
        return name;

        }
        return "";
    }


    /**
     * 录像
     */
    public void takeVideo(){

        if (state.equals(Environment.MEDIA_MOUNTED)) {

            String saveDir = Environment.getExternalStorageDirectory()
                    + "/Hejion/video";
            String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".mp4";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            videoFile=new File(saveDir,name);

            if (!videoFile.exists()){
                try{
                    videoFile.createNewFile();
                }catch (Exception e){
                    Log.e("videofile",e.toString());
                }
            }

            Intent  intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
            startActivityForResult(intent,VIDEO_REQUEST_CODE);

        }else{
            Toast.makeText(WriteMessActivity.this,
                    "请插入手机内存卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 拍照
     */
    public void takePhoto(){
        Intent intent;
        if (state.equals(Environment.MEDIA_MOUNTED)) {

            String saveDir = Environment.getExternalStorageDirectory()
                    + "/Hejion/img";
            String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            imgFile = new File(saveDir, name);
            if (!imgFile.exists()){
                try{
                    imgFile.createNewFile();
                }catch (Exception e){
                    Log.e("imgfile",e.toString());
                    return;
                }
            }

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
            startActivityForResult(intent, IMG_REQUEST_CODE);
        } else {
            Toast.makeText(WriteMessActivity.this,
                    "请插入手机内存卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog=null;
        Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setIcon(R.drawable.login_logo);
        switch (id) {
            case VIDEO:

                builder.setItems(R.array.vedio, new OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0:
                                takeVideo();
                                break;
                            case 1:
                                Intent intent=new Intent(WriteMessActivity.this,FileListActivity.class);
                                intent.putExtra("type","video");
                                startActivity(intent);
                                break;
                        }

                    }
                });
                dialog=builder.create();
                break;

            case IMAGE:
                builder.setItems(R.array.image, new OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                Intent intent=new Intent(WriteMessActivity.this,FileListActivity.class);
                                intent.putExtra("type","img");
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dialog=builder.create();
                break;

        }
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== IMG_REQUEST_CODE){

            Log.e("resultCode",requestCode+"---"+imgFile+"--");

            if (resultCode!=RESULT_OK|| imgFile ==null){
                Toast.makeText(context,"照片拍摄不成功，请重新拍摄",Toast.LENGTH_LONG).show();
                HeApplication.mVoicePath="";
                return;
            }

            filelistItem.add(imgFile.getPath());
            application.addFile(filelistItem);
            filelistItem.clear();

        }else if(requestCode==VIDEO_REQUEST_CODE){

            Log.e("resultCode",requestCode+"---"+videoFile+"--");

            if (resultCode!=RESULT_OK||videoFile==null){
                Toast.makeText(context,"视频录制不成功，请重新拍摄",Toast.LENGTH_LONG).show();
                HeApplication.mVideoPath="";
                return;
            }
            HeApplication.mVideoPath=videoFile.getPath();
            mhanddler.sendEmptyMessage(new Message().what=3);
//            filelistItem.add(videoFile.getPath());
//            application.addFile(filelistItem);
//            filelistItem.clear();

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.write_vedio_btn:
                if (isVideoClick){
                    Toast.makeText(context,"只允许上传一个视频",Toast.LENGTH_LONG).show();
                    return;
                }
                showDialog(VIDEO);
                break;
            case R.id.write_image_btn:
                showDialog(IMAGE);
                break;
            case R.id.back:
                if (application.getFileList().size()>1||!TextUtils.isEmpty(titleEdt.getText().toString().trim())
                        ||!TextUtils.isEmpty(contentEdt.getText().toString().trim())){
                    showAlertDialog(context);
                }else{
                    this.finish();
                }
                break;
            case R.id.write_send_btn:
                request=new LeaveWordRequest();

                if (TextUtils.isEmpty(titleEdt.getText().toString().trim())){
                    Toast.makeText(context,"标题不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(contentEdt.getText().toString().trim())){
                    Toast.makeText(context,"内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                List<String> file=new ArrayList<String>();
                if (!HeApplication.mVideoPath.equals("")&&!HeApplication.mVideoPath.equals(mVideoPath)){
                    mVideoPath=HeApplication.mVideoPath;
                    file.add(HeApplication.mVideoPath);
                    application.addFile(file);
                }
                Log.e("mVoicePath",HeApplication.mVoicePath);
                if (!HeApplication.mVoicePath.equals("")&&!HeApplication.mVoicePath.equals(mVoicePath)){
                    mVoicePath=HeApplication.mVoicePath;
                    file.add(HeApplication.mVoicePath);
                    application.addFile(file);
                }

                Log.e("application.getFileList()",application.getFileList()+"");
                request.setTitle(titleEdt.getText().toString().trim());
                request.setContent(contentEdt.getText().toString().trim());
                request.setFiles(application.getFileList());
                Intent intent=new Intent(WriteMessActivity.this,UploadService.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("message",(Serializable)request);
                intent.putExtras(bundle);
                startService(intent);
                break;
            case R.id.video_close_btn:
                showDeleteDialog(context,0);
                break;
            case R.id.voice_close_btn:
                showDeleteDialog(context,1);
                break;
            case R.id.voice_play:

                mediaPlayer = new MediaPlayer();
                String url = HeApplication.mVoicePath;
                Log.e("voicePath",url);
                if (url.equals("")||mediaPlayer==null){
                    return;
                }
                try
                {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //			mediaPlayer.setDataSource(path);
        //			mediaPlayer.prepare();
        //			mediaPlayer.start();

                    File path = new File(url);
                    FileInputStream fis = new FileInputStream(path);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    mhanddler.sendEmptyMessage(new Message().what=5);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mhanddler.sendEmptyMessage(new Message().what=4);
                        }
                    },mediaPlayer.getDuration());


                }
                catch (IllegalArgumentException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalStateException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            case R.id.video_play:

                if (HeApplication.mVideoPath.equals("")){
                    return;
                }
                intent = new Intent(Intent.ACTION_VIEW);
                String type = "video/mp4";
                Uri uri = Uri.parse(HeApplication.mVideoPath);
                intent.setDataAndType(uri, type);
                startActivity(intent);

                break;
            }

    }

    private Handler mhanddler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
//                    videoBtn.setEnabled(true);
                    videoLayout.setVisibility(View.GONE);
                    HeApplication.mVideoPath="";
                    isVideoClick=false;
                    break;
                case 1:
//                    audioBtn.setEnabled(true);
                    voiceLayout.setVisibility(View.GONE);
                    HeApplication.mVoicePath="";
                    isVoicClick=false;
                    break;
                case 2:
//                    audioBtn.setEnabled(false);
                    voiceLayout.setVisibility(View.VISIBLE);
                    isVoicClick=true;
                    break;
                case 3:
//                    videoBtn.setEnabled(false);
                    videoLayout.setVisibility(View.VISIBLE);;
                    isVideoClick=true;
                    break;
                case 4:
                    voicePlayIv.setImageDrawable(getResources().getDrawable(R.drawable.voice_bg_normal));
                    voicePlayIv.setEnabled(true);
                    break;
                case 5:
                    voicePlayIv.setImageDrawable(getResources().getDrawable(R.drawable.voice_bg_pressed));
                    voicePlayIv.setEnabled(false);
                    break;
            }
        }
    };

    @Override
    public void select() {
        showDialog(IMAGE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("WriteDestroy","onDestroy");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (application.getFileList().size()>1||!TextUtils.isEmpty(titleEdt.getText().toString().trim())
                        ||!TextUtils.isEmpty(contentEdt.getText().toString().trim())){
                    showAlertDialog(context);
                    return true;
                }
                return super.onKeyDown(keyCode, event);

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 弹出警告窗口
     * @param context
     */
    public void showAlertDialog(Context context) {
        final Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("有未发布的数据，是否退出？")
                .setIcon(getResources().getDrawable(R.drawable.icon_he))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        application.removeAllFile();
                        HeApplication.mVideoPath="";
                        HeApplication.mVoicePath="";
                        WriteMessActivity.this.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 删除语音和视频的警告窗口
     * @param context
     * @param type
     */
    public void showDeleteDialog(Context context, final int type){
        String message;
        if(type==0){
            message="是否删除当前的视频文件？";
        }else{
            message="是否删除当前的音频文件？";
        }
        final Dialog dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage(message)
                .setIcon(getResources().getDrawable(R.drawable.icon_he))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       if (type==0){
                           HeApplication.mVideoPath="";
                           mhanddler.sendEmptyMessage(new Message().what=0);
                       }else{
                           HeApplication.mVoicePath="";
                           mhanddler.sendEmptyMessage(new Message().what=1);
                       }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent=new Intent(WriteMessActivity.this,ImgViewPagerActivity.class);
        Bundle bundle=new Bundle();
        bundle.putStringArrayList("files",(ArrayList)application.getFileList());
        bundle.putString("pre", "false");
        bundle.putInt("position",i);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
