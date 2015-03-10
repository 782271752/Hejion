package com.li.hejion.Adapter;

import java.util.ArrayList;
import java.util.List;

import com.li.hejion.R;
import com.li.hejion.Utils.ImgCallBack;
import com.li.hejion.Utils.ImgSelectUtil;
import com.li.hejion.Utils.VideoSelectUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

public class ImgSelectAdapter extends BaseAdapter {

	Context context;
	List<String> data;
	public Bitmap bitmaps[];
	ImgSelectUtil imgSelectUtil;
	OnItemClickClass onItemClickClass;
	private int index=-1;
	private int type;

	List<View> holderlist;
	public ImgSelectAdapter(Context context, List<String> data, OnItemClickClass onItemClickClass,int type) {
		this.context=context;
		this.data=data;
		this.onItemClickClass=onItemClickClass;
		bitmaps=new Bitmap[data.size()];
		imgSelectUtil =new ImgSelectUtil(context);
		holderlist=new ArrayList<View>();
        this.type=type;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder holder;
		if (arg0 != index && arg0 > index) {
			index=arg0;
			arg1=LayoutInflater.from(context).inflate(R.layout.img_select_item, null);
			holder=new Holder();
			holder.imageView=(ImageView) arg1.findViewById(R.id.imageView1);
			holder.checkBox=(CheckBox) arg1.findViewById(R.id.checkBox1);
            holder.videoView=(ImageView)arg1.findViewById(R.id.video_img);
            holder.checkbox_bg=(ImageView)arg1.findViewById(R.id.checkbox_bg);
			arg1.setTag(holder);
			holderlist.add(arg1);
		}else {
			holder= (Holder)holderlist.get(arg0).getTag();
			arg1=holderlist.get(arg0);
		}
		if (bitmaps[arg0] == null) {
            if (type==0){
                imgSelectUtil.imgExcute(holder.imageView,new ImgClallBackLisner(arg0), data.get(arg0));
                holder.videoView.setVisibility(View.GONE);
            }else{
                holder.imageView.setImageBitmap(VideoSelectUtil.getVideoThumbnail(data.get(arg0),90,90, MediaStore.Video.Thumbnails.MICRO_KIND));
                holder.videoView.setVisibility(View.VISIBLE);
            }

		}
		else {
			holder.imageView.setImageBitmap(bitmaps[arg0]);
            holder.videoView.setVisibility(View.GONE);
		}
		arg1.setOnClickListener(new OnPhotoClick(arg0, holder.checkBox,holder.checkbox_bg));
		return arg1;
	}
	
	class Holder{
		ImageView imageView;
		CheckBox checkBox;
        ImageView videoView;
        ImageView checkbox_bg;
	}

	public class ImgClallBackLisner implements ImgCallBack {
		int num;
		public ImgClallBackLisner(int num) {
			this.num=num;
		}
		
		@Override
		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
			bitmaps[num]=bitmap;
			imageView.setImageBitmap(bitmap);
		}
	}

	public interface OnItemClickClass{
		public void OnItemClick(View v, int Position, CheckBox checkBox,ImageView checkBg);
	}
	
	class OnPhotoClick implements OnClickListener{
		int position;
		CheckBox checkBox;
        ImageView checkBg;
		
		public OnPhotoClick(int position,CheckBox checkBox,ImageView bg) {
			this.position=position;
			this.checkBox=checkBox;
            this.checkBg=bg;
		}
		@Override
		public void onClick(View v) {
			if (data!=null && onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(v, position, checkBox,checkBg);
			}
		}
	}
	
}
