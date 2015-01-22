package com.pzf.liaotian.bean.album;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;

/**
 * @desc:图片处理
 * @author: pangzf
 * @date: 2014年11月12日 下午3:18:01
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class ImageTool {
    private final static int MAX_NUM_PIXELS = 320 * 490;
    private final static int MIN_SIDE_LENGTH = 350;

    /**
     * 
     * @Description 生成图片的压缩图
     * @param filePath
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath) {
        if (null == filePath || !new File(filePath).exists())
            return null;
        Bitmap bitmap = null;
        int degree = readPictureDegree(filePath);
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, MAX_NUM_PIXELS);
            opts.inJustDecodeBounds = false;
            // if (opts.inSampleSize == 1) {
            // bitmap = BitmapFactory.decodeFile(filePath, opts);
            //
            // } else {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
            // }
        } catch (Exception e) {
            return null;
        }
        Bitmap newBitmap = rotaingImageView(degree, bitmap);
        return newBitmap;
    }

    private static Bitmap rotaingImageView(int degree, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    private static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? MIN_SIDE_LENGTH : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap getBigBitmapForDisplay(String imagePath,
            Context context) {
        if (null == imagePath || !new File(imagePath).exists())
            return null;
        try {
            int degeree = PhotoHandler.readPictureDegree(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null)
                return null;
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(dm);
            float scale = bitmap.getWidth() / (float) dm.widthPixels;
            Bitmap newBitMap = null;
            if (scale > 1) {
                newBitMap = zoomBitmap(bitmap,
                        (int) (bitmap.getWidth() / scale),
                        (int) (bitmap.getHeight() / scale));
                bitmap.recycle();
                Bitmap resultBitmap = PhotoHandler.rotaingImageView(degeree,
                        newBitMap);
                return resultBitmap;
            }
            Bitmap resultBitmap = PhotoHandler
                    .rotaingImageView(degeree, bitmap);
            return resultBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (null == bitmap) {
            return null;
        }
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) width / w);
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
                    true);
            return newbmp;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得图片的旋转角度
     * 
     * @param filePath
     * @return
     */
    private static int readPictureDegree(String filePath) {
        // 获得图片的角度
        int degreen = 0;
        ExifInterface ef;
        try {
            ef = new ExifInterface(filePath);
            int orientation = ef.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degreen = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degreen = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degreen = 270;
                    break;

                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return degreen;
    }
}
