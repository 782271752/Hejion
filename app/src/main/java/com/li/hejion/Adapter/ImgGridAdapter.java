package com.li.hejion.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.li.hejion.R;
import com.li.hejion.Utils.ImgCallBack;
import com.li.hejion.Utils.ImgSelectUtil;
import com.li.hejion.Utils.VideoSelectUtil;

import java.util.List;

/**
 * Created by li on 2014/7/19.
 */
public class ImgGridAdapter extends BaseAdapter {


    private Context context;
    private List<String> list;
    private LayoutInflater inflater;
    private ImgSelectUtil imgSelectUtil;
    private ImageSelect imageSelect;

    public ImgGridAdapter(Context context,List<String> list){
        this.context=context;
        this.list=list;
        this.inflater=LayoutInflater.from(context);
        imgSelectUtil =new ImgSelectUtil(context);
    }

    public void setImageSelect(ImageSelect select){
        this.imageSelect=select;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=new ViewHolder();
        if (view==null){
            view=inflater.inflate(R.layout.item_igv_write,null);
            holder.imageView=(ImageView)view.findViewById(R.id.write_gv_item_img);
            if (i==list.size()-1){
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.write_add));

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageSelect.select();
                    }
                });

            }else{
                String name=list.get(i).substring(list.get(i).length()-3,list.get(i).length());
                if(name.equals("jpg")||name.equals("png")||name.equals("gif")){
                    imgSelectUtil.imgExcute(holder.imageView,imgCallBack,list.get(i));
                }else{
                    holder.imageView.setImageBitmap(VideoSelectUtil.getVideoThumbnail(list.get(i),120,120, MediaStore.Video.Thumbnails.MICRO_KIND));
                }

            }
            view.setTag(holder);
        }else{
            holder=(ViewHolder)view.getTag();
        }
        return view;
    }

    class ViewHolder{
        ImageView imageView;
    }

    ImgCallBack imgCallBack=new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    /**
     * 添加图片接口
     */
    public interface ImageSelect{
        public void select();
    }
}
