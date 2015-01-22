package com.pzf.liaotian.view;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.pzf.liaotian.R;

/**
 * @desc: gif动画处字符串处理类
 * @author: pangzf
 * @date: 2014年11月4日 下午2:23:11
 * @blog:http://blog.csdn.net/pangzaifei/article/details/43023625
 * @github:https://github.com/pangzaifei/zfIMDemo
 * @qq:1660380990
 * @email:pzfpang451@163.com 
 */
public class GifExpressionUtil {
    public static String matching = "f0[0-9]{2}|f10[0-7]"; // 正则表达式，用来判断消息内是否有表情

    public static SpannableString getExpressionString(Context context,
            String str, Hashtable<Integer, GifDrawalbe> cache,
            Vector<GifDrawalbe> drawables) {
        SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern
                .compile(matching, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context, spannableString, sinaPatten, 0, cache,
                    drawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    /**
     * 拼串
     * 
     * @param context
     * @param spannableString
     * @param patten
     * @param start
     * @param cache
     * @param drawables
     * @throws Exception
     */
    public static void dealExpression(Context context,
            SpannableString spannableString, Pattern patten, int start,
            Hashtable<Integer, GifDrawalbe> cache, Vector<GifDrawalbe> drawables)
            throws Exception {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
            Field field = R.drawable.class.getDeclaredField(key);
            int id = Integer.parseInt(field.get(null).toString());
            if (id != 0) {
                GifDrawalbe mSmile = null;
                if (cache.containsKey(id)) {
                    mSmile = cache.get(id);
                } else {
                    mSmile = new GifDrawalbe(context, id);
                    cache.put(id, mSmile);
                }
                ImageSpan span = new ImageSpan(mSmile, ImageSpan.ALIGN_BASELINE);
                int mstart = matcher.start();
                int end = mstart + key.length();
                // ===如果是猪头和鲜花，用普通图片就好
                if (key.contains("f007") || key.contains("f008")) {
                    ImageSpan ivSpan = new ImageSpan(context,
                            BitmapFactory.decodeResource(
                                    context.getResources(), id));
                    spannableString.setSpan(ivSpan, mstart, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    spannableString.setSpan(span, mstart, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (!drawables.contains(mSmile))
                        drawables.add(mSmile);
                }

            }
        }
    }
}