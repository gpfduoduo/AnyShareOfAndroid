package com.guo.duoduo.anyshareofandroid.utils;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;


/**
 * Created by 郭攀峰 on 2015/9/26.
 */
public class ViewUtils
{

    private final static String tag = ViewUtils.class.getSimpleName();

    /** 获取每一个item在屏幕上的位置 */
    public static int[] getViewItemLocation(View view)
    {
        int[] location = new int[2]; //each item location
        view.getLocationInWindow(location);

        return location;
    }

}
