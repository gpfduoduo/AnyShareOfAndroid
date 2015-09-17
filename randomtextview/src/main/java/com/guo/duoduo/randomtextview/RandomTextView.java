package com.guo.duoduo.randomtextview;


import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * Created by 郭攀峰 on 2015/9/17.
 */
public class RandomTextView extends FrameLayout
    implements
        ViewTreeObserver.OnGlobalLayoutListener
{

    private static final String tag = RandomTextView.class.getSimpleName();

    public static final int MAX = 10;
    public static final int IDX_X = 0;
    public static final int IDX_Y = 1;
    public static final int IDX_TXT_LENGTH = 2;
    public static final int IDX_DIS_Y = 3;
    public static final int TEXT_SIZE_MAX = 25;
    public static final int TEXT_SIZE_MIN = 15;

    private Random random;
    private Vector<String> vecKeywords;
    private int width;
    private int height;

    public interface OnTextViewClickListener
    {
        void onTextViewClicked(View view);
    }

    private OnTextViewClickListener onTextViewClickListener;

    public RandomTextView(Context context)
    {
        super(context);
        init();
    }

    public RandomTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RandomTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public RandomTextView(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setOnTextViewClickListener(OnTextViewClickListener listener)
    {
        onTextViewClickListener = listener;
    }

    /**
     * 添加TextView的内容
     * 
     * @param keyword
     */
    public void addKeyWord(String keyword)
    {
        if (vecKeywords.size() < MAX)
        {
            if (!vecKeywords.contains(keyword))
                vecKeywords.add(keyword);
        }
        show();
    }

    private void init()
    {
        random = new Random();
        vecKeywords = new Vector<String>(MAX);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout()
    {
        int tmpW = getWidth();
        int tmpH = getHeight();
        if (width != tmpW || height != tmpH)
        {
            width = tmpW;
            height = tmpH;
            Log.d(tag, "RandowTextView width = " + width + "; height = " + height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void show()
    {
        this.removeAllViews();

        if (width > 0 && height > 0 && vecKeywords != null && vecKeywords.size() > 0)
        {
            //找到中心点
            int xCenter = width >> 1;
            int yCenter = height >> 1;
            //关键字的个数。
            int size = vecKeywords.size();
            int xItem = width / size;
            int yItem = height / size;
            LinkedList<Integer> listX = new LinkedList<Integer>();
            LinkedList<Integer> listY = new LinkedList<Integer>();
            for (int i = 0; i < size; i++)
            {
                // 准备随机候选数，分别对应x/y轴位置
                listX.add(i * xItem);
                listY.add(i * yItem + (yItem >> 2));
            }
            LinkedList<TextView> listTxtTop = new LinkedList<>();
            LinkedList<TextView> listTxtBottom = new LinkedList<>();

            for (int i = 0; i < size; i++)
            {
                String keyword = vecKeywords.get(i);
                // 随机颜色  
                int ranColor = 0xff000000 | random.nextInt(0x0077ffff);
                // 随机位置，糙值  
                int xy[] = randomXY(random, listX, listY, xItem);
                // 随机字体大小  
                int txtSize = TEXT_SIZE_MIN
                    + random.nextInt(TEXT_SIZE_MAX - TEXT_SIZE_MIN + 1);
                // 实例化TextView  
                final TextView txt = new TextView(getContext());
                txt.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (onTextViewClickListener != null)
                            onTextViewClickListener.onTextViewClicked(view);
                    }
                });
                txt.setText(keyword);
                txt.setTextColor(ranColor);
                txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
                txt.setShadowLayer(1, 1, 1, 0xdd696969);
                txt.setGravity(Gravity.CENTER);

                // 获取文本长度  
                Paint paint = txt.getPaint();
                int strWidth = (int) Math.ceil(paint.measureText(keyword));
                xy[IDX_TXT_LENGTH] = strWidth;
                // 第一次修正:修正x坐标  
                if (xy[IDX_X] + strWidth > width - (xItem >> 1))
                {
                    int baseX = width - strWidth;
                    // 减少文本右边缘一样的概率  
                    xy[IDX_X] = baseX - xItem + random.nextInt(xItem >> 1);
                }
                else if (xy[IDX_X] == 0)
                {
                    // 减少文本左边缘一样的概率  
                    xy[IDX_X] = Math.max(random.nextInt(xItem), xItem / 3);
                }
                xy[IDX_DIS_Y] = Math.abs(xy[IDX_Y] - yCenter);
                txt.setTag(xy);
                if (xy[IDX_Y] > yCenter)
                {
                    listTxtBottom.add(txt);
                }
                else
                {
                    listTxtTop.add(txt);
                }
            }

            attach2Screen(listTxtTop, xCenter, yCenter, yItem);
            attach2Screen(listTxtBottom, xCenter, yCenter, yItem);
        }
    }

    /** 修正TextView的Y坐标将将其添加到容器上。 */
    private void attach2Screen(LinkedList<TextView> listTxt, int xCenter, int yCenter,
            int yItem)
    {
        int size = listTxt.size();
        sortXYList(listTxt, size);
        for (int i = 0; i < size; i++)
        {
            TextView txt = listTxt.get(i);
            int[] iXY = (int[]) txt.getTag();
            // 第二次修正:修正y坐标
            int yDistance = iXY[IDX_Y] - yCenter;
            // 对于最靠近中心点的，其值不会大于yItem<br/>  
            // 对于可以一路下降到中心点的，则该值也是其应调整的大小<br/>  
            int yMove = Math.abs(yDistance);
            inner : for (int k = i - 1; k >= 0; k--)
            {
                int[] kXY = (int[]) listTxt.get(k).getTag();
                int startX = kXY[IDX_X];
                int endX = startX + kXY[IDX_TXT_LENGTH];
                // y轴以中心点为分隔线，在同一侧  
                if (yDistance * (kXY[IDX_Y] - yCenter) > 0)
                {
                    if (isXMixed(startX, endX, iXY[IDX_X], iXY[IDX_X]
                        + iXY[IDX_TXT_LENGTH]))
                    {
                        int tmpMove = Math.abs(iXY[IDX_Y] - kXY[IDX_Y]);
                        if (tmpMove > yItem)
                        {
                            yMove = tmpMove;
                        }
                        else if (yMove > 0)
                        {
                            // 取消默认值。  
                            yMove = 0;
                        }
                        break inner;
                    }
                }
            }

            if (yMove > yItem)
            {
                int maxMove = yMove - yItem;
                int randomMove = random.nextInt(maxMove);
                int realMove = Math.max(randomMove, maxMove >> 1) * yDistance
                    / Math.abs(yDistance);
                iXY[IDX_Y] = iXY[IDX_Y] - realMove;
                iXY[IDX_DIS_Y] = Math.abs(iXY[IDX_Y] - yCenter);
                // 已经调整过前i个需要再次排序  
                sortXYList(listTxt, i + 1);
            }
            FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
            layParams.gravity = Gravity.LEFT | Gravity.TOP;
            layParams.leftMargin = iXY[IDX_X];
            layParams.topMargin = iXY[IDX_Y];
            addView(txt, layParams);
        }
    }

    private int[] randomXY(Random ran, LinkedList<Integer> listX,
            LinkedList<Integer> listY, int xItem)
    {
        int[] arr = new int[4];
        arr[IDX_X] = listX.remove(ran.nextInt(listX.size()));
        arr[IDX_Y] = listY.remove(ran.nextInt(listY.size()));
        return arr;
    }

    /** A线段与B线段所代表的直线在X轴映射上是否有交集。 */
    private boolean isXMixed(int startA, int endA, int startB, int endB)
    {
        boolean result = false;
        if (startB >= startA && startB <= endA)
        {
            result = true;
        }
        else if (endB >= startA && endB <= endA)
        {
            result = true;
        }
        else if (startA >= startB && startA <= endB)
        {
            result = true;
        }
        else if (endA >= startB && endA <= endB)
        {
            result = true;
        }
        return result;
    }

    /**
     * 根据与中心点的距离由近到远进行冒泡排序。
     *
     * @param endIdx 起始位置。
     * @param listTxt 待排序的数组。
     *
     */
    private void sortXYList(LinkedList<TextView> listTxt, int endIdx)
    {
        for (int i = 0; i < endIdx; i++)
        {
            for (int k = i + 1; k < endIdx; k++)
            {
                if (((int[]) listTxt.get(k).getTag())[IDX_DIS_Y] < ((int[]) listTxt
                        .get(i).getTag())[IDX_DIS_Y])
                {
                    TextView iTmp = listTxt.get(i);
                    TextView kTmp = listTxt.get(k);
                    listTxt.set(i, kTmp);
                    listTxt.set(k, iTmp);
                }
            }
        }
    }
}
