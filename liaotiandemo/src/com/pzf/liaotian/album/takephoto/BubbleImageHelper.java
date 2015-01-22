package com.pzf.liaotian.album.takephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * @desc:合成图片
 * @author: pangzf
 * @date: 2014年11月12日 下午3:36:46
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class BubbleImageHelper {
    private Context context = null;
    private static BubbleImageHelper instance = null;

    public static synchronized BubbleImageHelper getInstance(Context c) {
        if (null == instance) {
            instance = new BubbleImageHelper(c);
        }
        return instance;
    }

    private BubbleImageHelper(Context c) {
        context = c;
    }

    private Bitmap getScaleImage(Bitmap bitmap, float width, float height) {
        if (null == bitmap || width < 0.0f || height < 0.0f) {
            return null;
        }
        Matrix matrix = new Matrix();
        float scaleWidth = width / bitmap.getWidth();
        float scaleHeight = height / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public Bitmap getBubbleImageBitmap(Bitmap srcBitmap,
            int backgroundResourceID) {
        if (null == srcBitmap) {
            return null;
        }
        Bitmap background = null;
        background = BitmapFactory.decodeResource(context.getResources(),
                backgroundResourceID);
        if (null == background) {
            return null;
        }

        Bitmap mask = null;
        Bitmap newBitmap = null;
        mask = srcBitmap;

        float srcWidth = (float) srcBitmap.getWidth();
        float srcHeight = (float) srcBitmap.getHeight();
        if (srcWidth < (float) CommonUtil.getImageMessageItemMinWidth(context)
                && srcHeight < (float) CommonUtil
                        .getImageMessageItemMinHeight(context)) {
            srcWidth = CommonUtil.getImageMessageItemMinWidth(context);
            srcHeight = (float) CommonUtil
                    .getImageMessageItemMinHeight(context);
            Bitmap tmp = getScaleImage(background, srcWidth, srcHeight);
            if (null != tmp) {
                background = tmp;
            } else {
                tmp = getScaleImage(srcBitmap,
                        (float) CommonUtil
                                .getImageMessageItemDefaultWidth(context),
                        (float) CommonUtil
                                .getImageMessageItemDefaultHeight(context));
                if (null != tmp) {
                    mask = tmp;
                }
            }
        }

        Config config = background.getConfig();
        if (null == config) {
            config = Bitmap.Config.ARGB_8888;
        }

        newBitmap = Bitmap.createBitmap(background.getWidth(),
                background.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(background, 0, 0, null);

        Paint paint = new Paint();

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        int left = 0;
        int top = 0;
        int right = mask.getWidth();
        int bottom = mask.getHeight();
        if (mask.getWidth() > background.getWidth()) {
            left = (mask.getWidth() - background.getWidth()) / 2;
            right = mask.getWidth() - left;
        }

        if (mask.getHeight() > background.getHeight()) {
            top = (mask.getHeight() - background.getHeight()) / 2;
            bottom = mask.getHeight() - top;
        }

        newCanvas.drawBitmap(mask, new Rect(left, top, right, bottom),
                new Rect(0, 0, background.getWidth(), background.getHeight()),
                paint);

        return newBitmap;
    }
}
