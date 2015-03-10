package com.li.hejion.Widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.li.hejion.R;


public class AudioDialog extends Dialog
{
	private Context context;
	private String ly;
    public ImageView imageView;
    private int currentIndex=0;
    private int[] drawables={R.drawable.radio1,R.drawable.radio2,R.drawable.radio3,R.drawable.radio4,R.drawable.radio5,R.drawable.radio2};

	public AudioDialog(Context context, String str)
	{
		super(context);
		this.context = context;
		this.ly = str;
	}

    public AudioDialog(Context context,String str,int theme){

        super(context,theme);
        this.context = context;
        this.ly = str;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);


		setContentView(R.layout.my_dialog);

        imageView=(ImageView)findViewById(R.id.my_dialog_image);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    currentIndex=(currentIndex+1)%drawables.length;
                    Message msg=new Message();
                    msg.what=0;
                    mhandler.sendEmptyMessage(msg.what);
                    try{
                        Thread.sleep(500);
                    }catch (Exception e){
                        Log.e("my_dialog", e.toString());
                    }
                }
            }
        }).start();
	}

    public Handler mhandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    imageView.setImageDrawable(context.getResources().getDrawable(drawables[currentIndex]));
                    break;
            }
        }
    };

}