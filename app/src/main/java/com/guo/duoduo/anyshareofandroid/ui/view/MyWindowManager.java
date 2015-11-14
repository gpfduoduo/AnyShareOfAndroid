package com.guo.duoduo.anyshareofandroid.ui.view;


import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


public class MyWindowManager
{

    /** 小悬浮窗View的实例 */
    private static FloatWindowSmallView smallWindow;
    /** 小悬浮窗View的参数 */
    private static LayoutParams smallWindowParams;
    /** 用于控制在屏幕上添加或移除悬浮窗 */
    private static WindowManager mWindowManager;

    public static boolean isWindowShowing()
    {
        return smallWindow != null;
    }

    public static void createSmallWindow(Context context, float x, float y, float xto,
            float yto, Drawable icon)
    {
        WindowManager windowManager = getWindowManager(context);

        if (smallWindow == null)
        {
            smallWindow = new FloatWindowSmallView(context, icon, xto, yto);
            if (smallWindowParams == null)
            {
                smallWindowParams = new LayoutParams();
                smallWindowParams.type = LayoutParams.TYPE_TOAST; //不需要权限就可以通过WindowManager显示浮动View
                smallWindowParams.format = PixelFormat.RGB_565;
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;//必须是left
                smallWindowParams.width = LayoutParams.WRAP_CONTENT;
                smallWindowParams.height = LayoutParams.WRAP_CONTENT;
                smallWindowParams.x = (int) x;
                smallWindowParams.y = (int) y;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }

        smallWindow.launchImg();
    }

    private static WindowManager getWindowManager(Context context)
    {
        if (mWindowManager == null)
        {
            mWindowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /** 将小悬浮窗从屏幕上移除。 */
    public static void removeSmallWindow(Context context)
    {
        if (smallWindow != null)
        {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindowParams = null;
            smallWindow = null;
        }
    }

}
