package com.guo.duoduo.anyshareofandroid;


import android.app.Application;


/**
 * Created by 郭攀峰 on 2015/9/11.
 */
public class MyApplication extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
