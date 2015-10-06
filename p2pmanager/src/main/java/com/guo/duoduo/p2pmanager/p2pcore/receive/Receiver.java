package com.guo.duoduo.p2pmanager.p2pcore.receive;


import android.util.Log;

import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pcore.MelonHandler;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;
import com.guo.duoduo.p2pmanager.p2pentity.P2PNeighbor;
import com.guo.duoduo.p2pmanager.p2pentity.param.ParamIPMsg;

/**
 * Created by 郭攀峰 on 2015/9/21.
 */
public class Receiver
{
    private static final String tag = Receiver.class.getSimpleName();
    public ReceiveManager receiveManager;
    public P2PNeighbor neighbor;
    public P2PFileInfo[] files;
    public MelonHandler p2PHandler;
    protected ReceiveTask receiveTask = null;
    boolean flagPercent = false;

    public Receiver(ReceiveManager receiveManager, P2PNeighbor neighbor,
            P2PFileInfo[] files)
    {
        this.receiveManager = receiveManager;
        this.neighbor = neighbor;
        this.files = files;
        p2PHandler = receiveManager.p2PHandler;
    }

    public void dispatchCommMSG(int cmd, ParamIPMsg ipMsg)
    {
        switch (cmd)
        {
            case P2PConstant.CommandNum.SEND_FILE_START : //接收端收到开始发送文件的消息
                //开始tcp
                Log.d(tag, "start receiver task");
                receiveTask = new ReceiveTask(p2PHandler, this);
                receiveTask.start();
                break;
            case P2PConstant.CommandNum.SEND_ABORT_SELF : //发送者退出
                clearSelf();
                if (p2PHandler != null)
                {
                    p2PHandler.send2UI(cmd, ipMsg);
                }
                break;
        }
    }

    public void dispatchTCPMSG(int cmd, Object obj)
    {
        switch (cmd)
        {
            case P2PConstant.CommandNum.RECEIVE_TCP_ESTABLISHED :
                break;
            case P2PConstant.CommandNum.RECEIVE_PERCENT :
                if(p2PHandler != null)
                    p2PHandler.send2UI(P2PConstant.CommandNum.RECEIVE_PERCENT, obj);
                break;
            case P2PConstant.CommandNum.RECEIVE_OVER :
                clearSelf();
                if(p2PHandler != null)
                    p2PHandler.send2UI(P2PConstant.CommandNum.RECEIVE_OVER, null);
                break;
        }
    }

    public void dispatchUIMSG(int cmd, Object obj)
    {
        switch (cmd)
        {
            case P2PConstant.CommandNum.RECEIVE_FILE_ACK : //发送接收文件的消息给发送者
                if (p2PHandler != null)
                    p2PHandler.send2Sender(neighbor.inetAddress, cmd, null);
                break;
            case P2PConstant.CommandNum.RECEIVE_ABORT_SELF : //接收者退出
                clearSelf();
                //通知发送者接收者已经推出
                if (p2PHandler != null)
                    p2PHandler.send2Sender(neighbor.inetAddress, cmd, null);
                break;
        }
    }

    private void clearSelf()
    {
        receiveManager.init();
    }
}
