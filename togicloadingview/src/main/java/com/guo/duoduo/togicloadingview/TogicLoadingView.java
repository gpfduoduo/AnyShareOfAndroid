package com.guo.duoduo.togicloadingview;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;


/**
 * Created by 10129302 郭攀峰 on 15-7-15.
 */
public class TogicLoadingView extends View
{
    private static final String tag = TogicLoadingView.class.getSimpleName();

    /**
     * 动画 0-1的时间
     */
    private float mInterpolatedTime;
    /**
     * 动画
     */
    private TogicLoadingAnimation tlAnimation;
    /**
     * 圆点画笔
     */
    private Paint paint;
    /**
     * 圆点的半径
     */
    private final int CIRCLE_RADIUS = 20;
    /**
     * 动画完成一次
     */
    private boolean sign = true;

    public TogicLoadingView(Context context)
    {
        super(context);
        init();
    }

    public TogicLoadingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TogicLoadingView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public TogicLoadingView(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void setVisibility(int visibility)
    {
        if (getVisibility() != visibility)
        {
            super.setVisibility(visibility);
            if (visibility == GONE || visibility == INVISIBLE)
                stopAnimation();
            else
                startAnimation();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.GONE || visibility == View.INVISIBLE)
            stopAnimation();
        else
            startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (getVisibility() != View.VISIBLE)
            return;
        if (sign)
        {
            canvas.save();
            canvas.rotate(180 * mInterpolatedTime, CIRCLE_RADIUS * 3, getHeight() / 2);
            canvas.drawCircle(CIRCLE_RADIUS, getHeight() / 2, CIRCLE_RADIUS, paint);
            canvas.drawCircle(CIRCLE_RADIUS * 5, getHeight() / 2, CIRCLE_RADIUS, paint);
            canvas.restore();
            canvas.drawCircle(CIRCLE_RADIUS * 9, getHeight() / 2, CIRCLE_RADIUS, paint);
        }
        else
        {
            canvas.save();
            canvas.drawCircle(CIRCLE_RADIUS, getHeight() / 2, CIRCLE_RADIUS, paint);
            canvas.rotate(180 * mInterpolatedTime, CIRCLE_RADIUS * 7, getHeight() / 2);
            canvas.drawCircle(CIRCLE_RADIUS * 5, getHeight() / 2, CIRCLE_RADIUS, paint);
            canvas.drawCircle(CIRCLE_RADIUS * 9, getHeight() / 2, CIRCLE_RADIUS, paint);
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (getVisibility() == VISIBLE)
            startAnimation();
        else
            stopAnimation();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(CIRCLE_RADIUS * 10,
            MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(CIRCLE_RADIUS * 6,
            MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class TogicLoadingAnimation extends Animation
    {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);
            mInterpolatedTime = interpolatedTime;
            invalidate();
            if (mInterpolatedTime == 1)
                sign = !sign;
        }
    }

    private void init()
    {
        paint = new Paint();
        paint.setAntiAlias(true); // 设置画笔为抗锯齿
        paint.setColor(Color.GRAY); // 设置画笔颜色
        paint.setStyle(Paint.Style.FILL);
    }

    public void startAnimation()
    {
        tlAnimation = new TogicLoadingAnimation();
        tlAnimation.setDuration(500);
        tlAnimation.setInterpolator(new DecelerateInterpolator());
        tlAnimation.setRepeatCount(Animation.INFINITE);
        tlAnimation.setRepeatMode(Animation.RESTART);
        startAnimation(tlAnimation);
    }

    public void stopAnimation()
    {
        this.clearAnimation();
        postInvalidate();
    }

}
