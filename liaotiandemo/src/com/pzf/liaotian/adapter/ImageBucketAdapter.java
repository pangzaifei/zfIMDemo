package com.pzf.liaotian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.android.adapter.CommonBaseAdapter;
import com.baidu.android.itemview.helper.IData;
import com.baidu.android.itemview.helper.IStyle;
import com.pzf.liaotian.bean.album.ImageBucket;
import com.pzf.liaotian.config.ConstantKeys;
import com.pzf.liaotian.itemview.PickPhotoSingleTypeView;

/**
 * @desc:图集adapter
 * @author: pangzf
 * @date: 2015年1月22日 上午11:06:34
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class ImageBucketAdapter extends CommonBaseAdapter<ImageBucket> {
    
    public static final int MESSAGE_TYPE_INVALID = -1;
    private static final int VIEW_TYPE_COUNT = 9;
    
    public ImageBucketAdapter(Context arg0) {
        super(arg0);
    }

    @Override
    protected View generateView(int position, View convertView, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case ConstantKeys.ALBUM_IMAGE_GRIDVIEW_DISPLAYTYPE_SINGLE:
                    convertView = new PickPhotoSingleTypeView(mContext);
                    break;

                default:
                    break;
            }
        }

        IStyle item = mData.get(position);
        if (item != null) {
            IData<IStyle> data = (IData<IStyle>) convertView;
            data.setData(item);
        }

        return convertView;
    }
    

    @Override
    public int getItemViewType(int position) {

        if (position >= mData.size()) {
            return MESSAGE_TYPE_INVALID;
        }
        return getItem(position).getStyle();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}
