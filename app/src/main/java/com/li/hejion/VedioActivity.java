package com.li.hejion;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VedioActivity extends Activity implements View.OnClickListener{

    private Button startBtn;
    private TextView timeTv;
    private SurfaceView mSurfaceview;
    private MediaRecorder mediaRecorder;
    private boolean isRecording;
    private int type=0;
    private File mRecVedioPath;
    private File mRecAudioFile;
    private int second = 0;
    private int  minute=0;
    private int hour=0;
    private boolean bool;
    protected Camera camera;
    protected boolean isPreview=true;
    private SurfaceHolder mSurfaceHolder;
    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_vedio);
        init();
        initView();

    }

    private void initView(){
        startBtn=(Button)findViewById(R.id.camera_btn);
        startBtn.setOnClickListener(this);
        mSurfaceview=(SurfaceView)findViewById(R.id.surfaceview);
        mSurfaceview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        timeTv=(TextView)findViewById(R.id.camera_time);

        holder = mSurfaceview.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewFrameRate(20); // 每秒30帧
                    parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
                    parameters.set("jpeg-quality", 100);// 照片质量
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                    isPreview = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mSurfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                mSurfaceHolder = holder;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (camera != null) {
                    if (isPreview) {
                        camera.stopPreview();
                        isPreview = false;
                    }
                    camera.release();
                    camera = null; // 记得释放
                }
                mSurfaceview = null;
                mSurfaceHolder = null;
                mediaRecorder = null;
            }
        });
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private Handler handler = new Handler();
    private Runnable task = new Runnable() {
        public void run() {
            if (bool) {
                handler.postDelayed(this, 1000);
                second++;
                if (second >= 60) {
                    minute++;
                    second = second % 60;
                }
                if (minute >= 60) {
                    hour++;
                    minute = minute % 60;
                }
                timeTv.setText(format(hour) + ":" + format(minute) + ":"
                        + format(second));
                if (second ==31){
                    stop();
                }

            }
        }
    };

    /*
	 * 格式化时间
	 */
    public String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    private void init(){
        // 设置缓存路径
        mRecVedioPath = new File(Environment.getExternalStorageDirectory()
                + "/Hejion/vedio");
        if (!mRecVedioPath.exists()) {
            mRecVedioPath.mkdirs();
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (type==0) {
            startBtn.setText("停止");
            start();
            type=1;
        }else{
            startBtn.setText("开始");
            stop();
            type=0;
        }

    }

    protected void start() {
        try {

//            mRecAudioFile = File.createTempFile("Vedio", ".mp4",
//                    mRecVedioPath);
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new Date()) + ".mp4";

            mRecAudioFile=new File(mRecVedioPath,fileName);
            if (!mRecAudioFile.exists()){
                try{
                    mRecAudioFile.createNewFile();
                }catch (Exception e){
                    Toast.makeText(VedioActivity.this, "录制出错", Toast.LENGTH_LONG).show();
                    return;
                }

            }

            if (isPreview) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            // 设置音频录入源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置视频图像的录入源
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);



            // 设置录入媒体的输出格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置音频的编码格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            // 设置视频的编码格式
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

            // 设置视频的采样率，每秒30帧
            mediaRecorder.setVideoFrameRate(20);
            // 设置录制视频文件的输出路径
            mediaRecorder.setOutputFile(mRecAudioFile
                    .getAbsolutePath());

            mediaRecorder.setVideoSize(640, 480);

            if(Build.VERSION.SDK_INT>=8) {
                mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
            }

            second = 0;
            minute = 0;
            hour = 0;
            bool = true;

            // 设置捕获视频图像的预览界面
            mediaRecorder.setPreviewDisplay(mSurfaceview.getHolder().getSurface());

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {

                    bool = false;
                    timeTv.setText(format(hour) + ":" + format(minute) + ":"
                            + format(second));
                    // 发生错误，停止录制
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording=false;
                    Toast.makeText(VedioActivity.this, "录制出错", Toast.LENGTH_LONG).show();
                }
            });

            // 准备、开始
            mediaRecorder.prepare();
            handler.postDelayed(task, 1000);
            mediaRecorder.start();
            isRecording = true;
            Toast.makeText(VedioActivity.this, "开始录像", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void stop() {
        if (isRecording) {

            bool = false;
            timeTv.setText(format(hour) + ":" + format(minute) + ":"
                    + format(second));

            // 如果正在录制，停止并释放资源
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording=false;
            Toast.makeText(VedioActivity.this, "停止录像，保存文件中", Toast.LENGTH_LONG).show();
            startBtn.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                stop();
                finish();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
