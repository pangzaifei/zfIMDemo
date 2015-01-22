package com.pzf.liaotian.itemview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.baidu.android.itemview.BaseLinearLayout;
import com.pzf.liaotian.R;
import com.pzf.liaotian.album.BitmapCache;
import com.pzf.liaotian.album.BitmapCache.ImageCallback;
import com.pzf.liaotian.bean.album.ImageItem;

/**
 * @desc:相册的gridview中的itemview
 * @author: pangzf
 * @date: 2015年1月9日 下午2:17:26
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class ImageGridSingleTypeView extends BaseLinearLayout<ImageItem> {

    private ImageView mIv;
    private ImageView mSelected;
    // private ImageLoader mImageLoader;'
    public boolean isAllow=true;//这个稍后处理，可以通过这个来解决，图片滑动不加载等

    ImageCallback callback = new ImageCallback() {
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
    private BitmapCache mCache;

    public ImageGridSingleTypeView(Context context) {
        super(context);
        mCache = BitmapCache.getInstance();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.vw_takephoto_item_image_grid;
    }

    @Override
    protected void initView() {
        mIv = (ImageView) findViewById(R.id.image);
        mSelected = (ImageView) findViewById(R.id.isselected);

    }

    @Override
    protected void notifyDataChanged() {
        handleData();
    }

    /**
     * 处理图片数据
     */
    private void handleData() {
        // Log.e("fff", "本地图片地址:" + mItem.getImagePath());
        // String testurl="http://avatar.csdn.net/2/E/A/1_a220315410.jpg";
        // mImageLoader.get("file://" + mItem.getImagePath(), new
        // ImageListener() {
        //
        // @Override
        // public void onErrorResponse(VolleyError arg0) {
        // Logger.TEST.error("下载图片失败");
        // }
        //
        // @Override
        // public void onResponse(ImageContainer container, boolean isCache) {
        // if (container != null) {
        // Bitmap bitmap = container.getBitmap();
        // if (bitmap != null) {
        // mIv.setImageBitmap(bitmap);
        // }
        // }
        // }
        // });
        mIv.setTag(mItem.getImagePath());

        Bitmap bmp = mCache.getCacheBitmap(mItem.getThumbnailPath(),
                mItem.getImagePath());
        if (null != bmp) {
            mIv.setImageBitmap(bmp);
        } else {
            if (isAllow) {
                mCache.displayBmp(mIv, mItem.getThumbnailPath(),
                        mItem.getImagePath(), callback);
            } else {
                mIv.setImageResource(R.drawable.zf_default_album_grid_image);
            }
            mCache.displayBmp(mIv, mItem.getThumbnailPath(),
                  mItem.getImagePath(), callback);
        }

        if (mItem.isSelected()) {
            mSelected
                    .setImageResource(R.drawable.ic_takephoto_album_img_selected);
        } else {
            mSelected
                    .setImageResource(R.drawable.ic_takephoto_album_img_select_nor);
        }
    }
}
