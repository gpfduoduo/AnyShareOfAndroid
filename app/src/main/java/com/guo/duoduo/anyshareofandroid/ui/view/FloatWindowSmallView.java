package com.guo.duoduo.anyshareofandroid.ui.view;


import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.guo.duoduo.anyshareofandroid.R;
import com.guo.duoduo.anyshareofandroid.ui.uientity.LocationPoint;


public class FloatWindowSmallView extends LinearLayout
{

    private static final String tag = FloatWindowSmallView.class.getSimpleName();

    private static final int DURATION_TIME = 800;
    /** 用于更新小悬浮窗的位置 */
    private WindowManager windowManager;
    /** 被移动的图标 */
    private ImageView floatImg;
    /** 小悬浮窗的参数 */
    private WindowManager.LayoutParams mParams;
    private Context context;
    private float xTo;
    private float yTo;

    public FloatWindowSmallView(Context context, Drawable icon, float xto, float yto)
    {
        super(context);
        this.context = context;
        xTo = xto;
        yTo = yto;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_file_select, this);
        floatImg = (ImageView) findViewById(R.id.float_img);
        floatImg.setImageDrawable(icon);
    }

    /** 用于移动图标 */
    public void launchImg()
    {
        LocationPoint start = new LocationPoint(mParams.x, mParams.y);
        LocationPoint end = new LocationPoint(0, 0);
        ValueAnimator locationAnimation = ValueAnimator.ofObject(new PointEvaluator(),
            start, end);

        locationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                LocationPoint location = (LocationPoint) animation.getAnimatedValue();
                mParams.x = location.x;
                mParams.y = location.y;

                if (location.x == 0 && location.y == 0)
                {
                    MyWindowManager.removeSmallWindow(context);
                }
                else
                {
                    windowManager.updateViewLayout(FloatWindowSmallView.this, mParams);
                }
            }
        });

        locationAnimation.setDuration(DURATION_TIME);
        locationAnimation.start();
    }

    /** 将小悬浮窗的参数传入，用于更新小悬浮窗的位置 */
    public void setParams(WindowManager.LayoutParams params)
    {
        mParams = params;
    }

    public static class PointEvaluator implements TypeEvaluator
    {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue)
        {
            LocationPoint startPoint = (LocationPoint) startValue;
            LocationPoint endPoint = (LocationPoint) endValue;
            float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
            float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
            LocationPoint point = new LocationPoint((int) x, (int) y);
            return point;
        }
    }
}
