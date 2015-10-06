package com.guo.duoduo.p2pmanager.p2pcore.send;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by 郭攀峰 on 2015/9/22.
 */
public class OneByOneRunnable implements Runnable
{
    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    public void waitRun()
    {
        pauseLock.lock();
        try
        {
            while (isPaused)
                unpaused.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            pauseLock.unlock();
        }
    }

    public void pause()
    {
        pauseLock.lock();
        try
        {
            isPaused = true;
        }
        finally
        {
            pauseLock.unlock();
        }
    }

    public void resume()
    {
        pauseLock.lock();
        try
        {
            isPaused = false;
            unpaused.signalAll();
        }
        finally
        {
            pauseLock.unlock();
        }
    }

    @Override
    public void run()
    {

    }
}
