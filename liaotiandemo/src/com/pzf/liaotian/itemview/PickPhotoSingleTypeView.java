package com.pzf.liaotian.itemview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.itemview.BaseLinearLayout;
import com.pzf.liaotian.R;
import com.pzf.liaotian.album.BitmapCache;
import com.pzf.liaotian.album.BitmapCache.ImageCallback;
import com.pzf.liaotian.bean.album.ImageBucket;

/**
 * @desc:拍照type为single的itemview
 * @author: pangzf
 * @date: 2015年1月22日 上午11:09:27
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class PickPhotoSingleTypeView extends BaseLinearLayout<ImageBucket> {

    private ImageView mIv;
    private TextView mName;
    private TextView mCount;
    private ImageView mAlbumArrow;
    private BitmapCache mCache;
    
    ImageCallback mCallback = new ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                Object... params) {
            try {
                if (null != imageView && null != bitmap) {
                    String url = (String) params[0];
                    if (null != url && url.equals((String) imageView.getTag())) {
                        ((ImageView) imageView).setImageBitmap(bitmap);
                    } else {
                        Log.e("fff", "callback, bmp not match");
                    }
                } else {
                    Log.e("fff", "callback, bmp null");
                }
            } catch (Exception e) {
                Log.e("fff", e.getMessage());
            }
        }
    };

    // private ImageLoader mImageLoader;

    public PickPhotoSingleTypeView(Context arg0) {
        super(arg0);
        mCache = BitmapCache.getInstance();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.vw_takephoto_item_image_pick;
    }

    @Override
    protected void initView() {
        mIv = (ImageView) findViewById(R.id.image);
        mName = (TextView) findViewById(R.id.name);
        mCount = (TextView) findViewById(R.id.count);
        mAlbumArrow = (ImageView) findViewById(R.id.im_album_arrow);
    }

    @Override
    protected void notifyDataChanged() {

        handleData();

    }
    

    private void handleData() {
        ImageBucket item = mItem;
        mCount.setText("(" + item.count + ")");
        String nameStr = item.bucketName;
        if (nameStr.length() > 14) {
            nameStr = nameStr.substring(0, 14) + "...";
        }
        mName.setText(nameStr);
        if (item.imageList != null && item.imageList.size() > 0) {
            String thumbPath = item.imageList.get(0).getThumbnailPath();
            String sourcePath = item.imageList.get(0).getImagePath();
            mIv.setTag(sourcePath);
            Bitmap bmp = mCache.getCacheBitmap(thumbPath, sourcePath);
            if (bmp != null) {
                mIv.setImageBitmap(bmp);
            } else {
                mCache.displayBmp(mIv, thumbPath, sourcePath, mCallback);
            }

        } else {
            mIv.setImageBitmap(null);
            Log.e("fff", "no images in bucket " + item.bucketName);
        }

    }

}
