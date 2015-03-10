package com.li.hejion.Adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by li on 2014/8/11.
 */
public class ViewPagerAdapter extends PagerAdapter{


    //界面列表
    private ArrayList<View> views;

    public ViewPagerAdapter(ArrayList<View> views) {
        this.views=views;
    }


    @Override
    public int getCount() {
        if(views!=null){
            return views.size();
        }
        return 0;
    }

    //初始化position位置的界面
    @Override
    public Object instantiateItem(View view, int position) {
        // TODO Auto-generated method stub
        ((ViewPager)view).addView(views.get(position),0);
        return views.get(position);
    }
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view==o;
    }

    //销毁position位置的界面
    @Override
    public void destroyItem(View view, int position, Object arg2) {
        ((ViewPager) view).removeView(views.get(position));
    }
}
