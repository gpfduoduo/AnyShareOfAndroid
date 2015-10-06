package com.guo.duoduo.p2pmanager.p2pcore.receive;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;

import android.util.Log;

import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pcore.MelonHandler;
import com.guo.duoduo.p2pmanager.p2pcore.P2PManager;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;

/**
 * Created by 郭攀峰 on 2015/9/21.
 * 接收端的线程实现
 */
public class ReceiveTask extends Thread
{
    private static final String tag = ReceiveTask.class.getSimpleName();

    private MelonHandler p2PHandler;
    private Receiver receiver;
    String sendIp;
    Socket socket;
    boolean finished = false;
    File receiveFile;
    BufferedOutputStream bufferedOutputStream;
    BufferedInputStream bufferedInputStream;
    byte[] readBuffer = new byte[512];

    public ReceiveTask(MelonHandler handler, Receiver receiver)
    {
        this.p2PHandler = handler;
        this.receiver = receiver;
        this.sendIp = receiver.neighbor.ip;
    }

    @Override
    public void run()
    {
        loop : for (int i = 0; i < receiver.files.length; i++)
        {
            if (isInterrupted())
                break;
            try
            {
                socket = new Socket(sendIp, P2PConstant.PORT);
                notifyReceiver(P2PConstant.CommandNum.RECEIVE_TCP_ESTABLISHED, null);

                P2PFileInfo fileInfo = receiver.files[i];

                Log.d(tag, "prepare to receive file:" + fileInfo.name + "; files size = "
                    + receiver.files.length);

                String path = P2PManager.getSavePath(fileInfo.type);
                File fileDir = new File(path);
                if (!fileDir.exists())
                    fileDir.mkdirs();

                receiveFile = new File(fileDir, fileInfo.name);
                if (receiveFile.exists())
                    receiveFile.delete();

                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                    receiveFile));

                long total = 0L;
                int len = 0;
                int lastPercent = 0, percent = 0;
                bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                while ((len = bufferedInputStream.read(readBuffer)) != -1)
                {
                    if (isInterrupted())
                    {
                        receiveFile.delete();
                        break loop;
                    }
                    bufferedOutputStream.write(readBuffer, 0, len);

                    total += len;
                    percent = (int) (((float) total / fileInfo.size) * 100);

                    if (percent - lastPercent > 1 || percent == 100)
                    {
                        lastPercent = percent;
                        fileInfo.setPercent(percent);
                        notifyReceiver(P2PConstant.CommandNum.RECEIVE_PERCENT, fileInfo);
                    }

                    if (total >= fileInfo.size)
                    {
                        Log.d(tag, "total > file info size");
                        break;
                    }
                } // end of while

                receiveFile = null;
                fileInfo.setPercent(100);
                notifyReceiver(P2PConstant.CommandNum.RECEIVE_PERCENT, fileInfo);

                Log.d(tag, "receive file " + fileInfo.name + " success");

                socket.close();

                if (i == receiver.files.length - 1)
                {
                    Log.d(tag, "receive file over");
                    notifyReceiver(P2PConstant.CommandNum.RECEIVE_OVER, null);
                    finished = true;
                }
            }
            catch (InterruptedIOException e)
            {
                e.printStackTrace();
                finished = true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                finished = true;
            }
            finally
            {
                release();
            }
        } // end of loop

        release();
    }

    private void release()
    {
        if (bufferedOutputStream != null)
        {
            try
            {
                bufferedOutputStream.close();
                bufferedOutputStream = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (bufferedInputStream != null)
        {
            try
            {
                bufferedInputStream.close();
                bufferedInputStream = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (socket != null)
        {
            try
            {
                socket.close();
                socket = null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void notifyReceiver(int cmd, Object obj)
    {
        if (!finished)
        {
            if (p2PHandler != null)
                p2PHandler.send2Handler(cmd, P2PConstant.Src.RECEIVE_TCP_THREAD,
                    P2PConstant.Recipient.FILE_RECEIVE, obj);
        }
    }
}
