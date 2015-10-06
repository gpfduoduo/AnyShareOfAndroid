package com.guo.duoduo.p2pmanager.p2pcore;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;


/**
 * Created by 郭攀峰 on 2015/9/19.
 * 自定义的HandlerThread
 */
public class CustomHandlerThread extends Thread
{

    /**
     * 处理线程消息的handler
     */
    Handler mHandler;

    /**
     * 线程优先级别
     */
    int mPriority;
    /**
     * 当前线程的ID
     */
    int mTid;
    /**
     * 当前线程的Looper
     */
    Looper mLooper;

    boolean mIsReady = false;
    /**
     * 处理消息的Handler
     */
    Class<? extends Handler> mHandlerClass;

    public CustomHandlerThread(String threadName, Class<? extends Handler> handlerClass)
    {
        super(threadName);
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
        mHandlerClass = handlerClass;
    }

    public CustomHandlerThread(String threadName, int prority,
            Class<? extends Handler> handlerClass)
    {
        super(threadName);
        mPriority = prority;
        this.mHandlerClass = handlerClass;
    }

    public Handler getLooperHandler()
    {
        return mHandler;
    }

    /**
     * 确保mHandler被创建
     */
    public void isReady()
    {
        synchronized (this)
        {
            while (mIsReady == false)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run()
    {
        mTid = Process.myTid();
        //准备循环条件
        Looper.prepare();
        //持有锁来获取当前线程的Looper对象
        synchronized (this)
        {
            mLooper = Looper.myLooper();
            //发出通知，当前线程的Looper已经创建成功，主要是通知getLooper中的wait
            notifyAll();
        }

        Process.setThreadPriority(mPriority);

        try
        {
            Constructor<? extends Handler> handler_creater = mHandlerClass
                    .getConstructor(Looper.class);
            mHandler = (Handler) handler_creater.newInstance(mLooper);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        //该方法实现体是空的，子类可以实现该方法，作用就是在线程循环之前做一些准备工作，当然子类也可以不实现。
        onLooperPrepared();
        //启动Looper
        Looper.loop();
        mTid = -1;
    }

    protected void onLooperPrepared()
    {
        synchronized (this) //确保 mHandler一ing被创建
        {
            mIsReady = true;
            notifyAll();
        }
    }

    private Looper getLooper()
    {
        if (!isAlive())
            return null;

        //如果线程已经启动，但是Looper还没有创建的话，等待直到Looper创建成功
        synchronized (this)
        {
            while (isAlive() && mLooper == null)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return mLooper;
    }

    public boolean quit()
    {
        Looper looper = getLooper();
        if (looper != null)
        {
            looper.quit();
            return true;
        }
        return false;
    }

    //安全退出循环
    @TargetApi(18)
    public boolean quitSafely()
    {
        Looper looper = getLooper();
        if (looper != null)
        {
            looper.quitSafely();
            return true;
        }
        return false;
    }
}
