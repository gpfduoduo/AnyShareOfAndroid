package com.guo.duoduo.httpserver.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.guo.duoduo.httpserver.http.RequestListenerThread;
import com.guo.duoduo.httpserver.utils.Constant;


public class WebService extends Service
{

    private RequestListenerThread thread;

    public WebService()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        thread = new RequestListenerThread(Constant.Config.PORT, Constant.Config.Web_Root);
        thread.setDaemon(false);
        thread.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        new Thread()
        {
            public void run()
            {
                if (thread != null)
                    thread.destroy();
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
