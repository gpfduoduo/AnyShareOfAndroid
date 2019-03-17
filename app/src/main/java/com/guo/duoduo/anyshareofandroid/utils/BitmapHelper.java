package com.guo.duoduo.anyshareofandroid.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

public class BitmapHelper {

    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull final Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getBitmapFromDrawableAndroidO(drawable);
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Bitmap getBitmapFromDrawableAndroidO(@NonNull final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }  else if (drawable instanceof AdaptiveIconDrawable) {
            final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bmp);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bmp;
        }
        return null;
    }
}
