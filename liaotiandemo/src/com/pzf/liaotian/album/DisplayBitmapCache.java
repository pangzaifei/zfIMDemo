package com.pzf.liaotian.album;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;

import com.pzf.liaotian.bean.album.ImageTool;

/**
 * @desc:用于预览相关图片缓存
 * @author: pangzf
 * @date: 2014年11月13日 下午5:35:17
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com
 */
public class DisplayBitmapCache {
    private static DisplayBitmapCache instance = null;
    private HashMap<String, Bitmap> map = new HashMap<String, Bitmap>();
    private Context context = null;

    public static synchronized DisplayBitmapCache getInstance(Context c) {
        if (null == instance) {
            instance = new DisplayBitmapCache(c);
        }
        return instance;
    }

    private DisplayBitmapCache(Context c) {
        context = c;
    }

    public void set(String path, Bitmap bmp) {
        map.put(path, bmp);
    }

    public Bitmap get(String path) {
        if (map.containsKey(path)) {
            return map.get(path);
        } else {
            Bitmap bmp = ImageTool.getBigBitmapForDisplay(path, context);
            if (null != bmp) {
                map.put(path, bmp);
            }
            return bmp;
        }
    }
}
