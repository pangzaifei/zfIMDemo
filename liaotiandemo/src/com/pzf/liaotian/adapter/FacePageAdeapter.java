package com.pzf.liaotian.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.pzf.liaotian.view.JazzyViewPager;

/**
 * viewpager的adapter
 * @author pangzf
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class FacePageAdeapter extends PagerAdapter {

    // 界面列表
    private List<View> views;
    private JazzyViewPager viewPager;

    public FacePageAdeapter(List<View> lv, JazzyViewPager viewPager) {
        super();
        this.views = lv;
        this.viewPager = viewPager;
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finishUpdate(View container) {
        super.finishUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(views.get(position), 0);
        viewPager.setObjectForPosition(views.get(position), position);// 这句很重要,没有这句就没有效果
        return views.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }

    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setPrimaryItem(View container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void startUpdate(View container) {
        super.startUpdate(container);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

}
