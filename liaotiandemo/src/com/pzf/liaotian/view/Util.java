package com.pzf.liaotian.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Util {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());

        return (int) scale;
    }

}
